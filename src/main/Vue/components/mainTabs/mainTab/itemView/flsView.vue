<template>
    <tr v-if="edit">
        <td colspan="15">
            <span style="display: flex;">
                <button class="btn-img acpt" :title="language.data.cm3" :disabled="name.length === 0" @click="save"></button>
                <button class="btn-img cncl" :title="language.data.cm4" @click="cancel"></button>
                <input type="text" class="input" v-model="name">
            </span>
        </td>
    </tr>
    <tr :class="{'searchTarget': isSearchTarget}" v-else>
        <td class="fit">
            <img class="iv-icon" src="/svg/lock.svg">
        </td>
        <td class="fit">
            <item-info :item="file"></item-info>
            <span>
                <button class="btn-img menu" v-show="access === 'RW'" @click.stop="showMenu = !showMenu" :title="language.data.cm12"></button>
                <div class="dropdown" :class="{ show: showMenu }">
                    <a class="dropdown-item" @click.stop="showInfo = true; showMenu = false">{{ language.data.cm8 }}</a>
                    <a class="dropdown-item" @click="edit = true" v-show="access === 'RW' && !edit">{{ language.data.cm2 }}</a>
                    <a class="dropdown-item" @click="copyToClipboard" v-show="access === 'RW'">{{ language.data.cf1 }}</a>
                    <a class="dropdown-item" @click="remove" v-show="access === 'RW'">{{ language.data.cm5 }}</a>
                </div>
            </span>
        </td>
        <td><span class="iv-item-name" @click="download">{{ file.name }}</span></td>
        <td>{{ file.size }} bytes</td>
        <td class="fit">
            <span style="user-select: none">{{ language.data.cm10 }} &nbsp;</span>$id:{{ $parent.$props.item.id }}f{{ file.id }}&nbsp;
        </td>
    </tr>
</template>

<script>
import itemInfo from "./itemInfo.vue";

export default {
    name: "flsView",
    props: {
        file: Object,
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
            clipboard: this.$root.$data.clipboard,
            showMenu: false,
            showInfo: false,
            edit: false,
            name: ""
        }
    },
    computed: {
        isSearchTarget() {
            if (this.searchData) {
                return this.searchData.cntId === this.$parent.$props.item.id && this.searchData.type === "f" && this.searchData.itemId === this.file.id;
            }
            return false;
        }
    },
    methods: {
        async download() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    fileId: this.file.id
                });
                const answer = await $.ajax({
                    url: "/container/getFile",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                if (data.message) {
                    let fileType = data.message.match(":(.*);")[1];
                    let bodyStr = atob(data.message.split(",")[1]);
                    let bodyCharArray = [];
                    for (let i = 0; i < bodyStr.length; i++) {
                        bodyCharArray.push(bodyStr.charCodeAt(i));
                    }
                    let bodyBytes = new Uint8Array(bodyCharArray);
                    let bodyBlob = new Blob([bodyBytes], {type: fileType});
                    let blobURL = URL.createObjectURL(bodyBlob);
                    let a = document.createElement("a");
                    a.href = blobURL;
                    a.setAttribute("download", this.file.name);
                    a.click();
                } else {
                    this.eventHub.emit("show-msg", this.language.data.fle2);
                }
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async save() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    fileId: this.file.id,
                    name: this.name
                });
                const answer = await $.ajax({
                    url: "/container/editFile",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                if (data.message) {
                    this.eventHub.emit("show-msg", this.language.data[data.message]);
                } else {
                    this.edit = false;
                    this.eventHub.emit("update-tree");
                }
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        cancel() {
            this.edit = false;
            this.name = this.file.name;
        },
        async remove() {
            if (this.name && confirm(this.language.data.cm5 + " " + this.file.name + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        fileId: this.file.id
                    });
                    const answer = await $.ajax({
                        url: "/container/removeFile",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    } else {
                        this.eventHub.emit("update-tree");
                    }
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        closeMenu() {
            this.showMenu = false;
        },
        copyToClipboard() {
            this.clipboard.id = this.file.id;
            this.clipboard.type = "f";
            this.clipboard.name = this.file.name;
        }
    },
    mounted() {
        this.name = this.file.name;
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