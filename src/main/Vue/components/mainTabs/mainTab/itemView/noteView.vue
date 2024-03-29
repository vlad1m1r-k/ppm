<template>
    <div :class="{'searchTarget': isSearchTarget}">
        <div class="decor">
            &#x1f512;
            <item-info :item="note" v-if="access === 'RW'"></item-info>
            &nbsp; <span class="btn-link cursor-pointer" @click="toggle" :title="language.data.cm1">{{ note.name }}</span> &nbsp;
            <span class="btn-dc" v-show="show && access === 'RW' && !edit" :title="language.data.cm2" @click="edit = true">&#x1f589;</span>
            <span class="btn-dc" v-show="show && access === 'RW' && edit" :title="language.data.cm3" @click="save">&#x2705;</span>
            <span class="btn-dc" v-show="show && access === 'RW' && edit" :title="language.data.cm4" @click="cancel">&#x274c;</span>
            <span class="btn-dc float-right" v-show="access === 'RW'" :title="language.data.cm5" @click="remove">&#x1f5d1;</span>
            <span class="float-right"><span style="user-select: none">{{ language.data.cm10 }} &nbsp;</span>$id:{{ $parent.$props.item.id }}n{{ note.id }}&nbsp;</span>
        </div>
        <input type="text" class="form-control" v-show="show && edit" v-model="name">
        <textarea class="form-control" rows="4" :readonly="!edit" v-show="show" v-model="text"></textarea>
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
        }
    },
    mounted() {
        this.name = this.note.name;
    }
}
</script>

<style scoped>

</style>