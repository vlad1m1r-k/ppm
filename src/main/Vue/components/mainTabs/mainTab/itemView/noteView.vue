<template>
    <div :class="{'searchTarget': isSearchTarget}">
        <div class="iv-item">
            <img class="iv-icon" src="/svg/lock.svg">
            <item-info :item="note"></item-info>
            <span>
                <button class="btn-img menu" v-show="access === 'RW'" @click.stop="showMenu = !showMenu" :title="language.data.cm12"></button>
                <div class="dropdown" :class="{ show: showMenu }">
                    <a class="dropdown-item" @click.stop="showInfo = true; showMenu = false">{{ language.data.cm8 }}</a>
                    <a class="dropdown-item" @click="loadNote(); show = true; edit = true" v-show="access === 'RW' && !edit">{{ language.data.cm2 }}</a>
                    <a class="dropdown-item" @click="remove" v-show="access === 'RW'">{{ language.data.cm5 }}</a>
                </div>
            </span>
            <span class="flex-view">
                <span class="iv-item-name" @click="toggle" :title="language.data.cm1">{{ note.name }}</span>
                <button class="btn-img acpt" :title="language.data.cm3" v-show="show && access === 'RW' && edit" @click="save"></button>
                <button class="btn-img cncl" :title="language.data.cm4" v-show="show && access === 'RW' && edit" @click="cancel"></button>
            </span>
            <span class="iv-item-link"><span style="user-select: none">{{ language.data.cm10 }} &nbsp;</span>$id:{{ $parent.$props.item.id }}n{{ note.id }}&nbsp;</span>
        </div>
        <input type="text" class="input" v-show="show && edit" v-model="name">
        <textarea class="text-box" rows="4" :readonly="!edit" v-show="show" v-model="text"></textarea>
    </div>
</template>

<script>
import itemInfo from "./itemInfo.vue";

export default {
    name: "noteView",
    props: {
        note: Object,
        access: String,
        searchData: Object
    },
    components: {
        itemInfo
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            showMenu: false,
            showInfo: false,
            show: false,
            edit: false,
            name: "",
            text: ""
        }
    },
    computed: {
        isSearchTarget() {
            if (this.searchData) {
                return this.searchData.cntId === this.$parent.$props.item.id && this.searchData.type === "n" && this.searchData.itemId === this.note.id;
            }
            return false;
        }
    },
    methods: {
        toggle() {
            if (this.show) {
                this.show = false;
                this.text = "";
                this.edit = false;
                this.name = this.note.name;
            } else {
                this.loadNote();
                this.show = true;
            }
        },
        async loadNote() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    note: this.note.id,
                });
                const answer = await $.ajax({
                    url: "/container/getNote",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                this.text = data.message;
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async save() {
            if (this.name && confirm(this.language.data.iv9)) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        note: this.note.id,
                        name: this.name,
                        text: this.text
                    });
                    const answer = await $.ajax({
                        url: "/container/editNote",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    }
                    this.edit = false;
                    this.eventHub.emit("update-tree");
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        cancel() {
            this.edit = false;
            this.name = this.note.name;
            this.loadNote();
        },
        async remove() {
            if (this.name && confirm(this.language.data.iv10 + this.note.name + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        note: this.note.id
                    });
                    const answer = await $.ajax({
                        url: "/container/removeNote",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    }
                    this.eventHub.emit("update-tree");
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        closeMenu() {
            this.showMenu = false;
        }
    },
    mounted() {
        this.name = this.note.name;
    },
    created() {
        this.eventHub.on("close-menu", this.closeMenu);
    },
    beforeUnmount() {
        this.eventHub.off("close-menu", this.closeMenu);
    }
}
</script>

<style scoped>

</style>