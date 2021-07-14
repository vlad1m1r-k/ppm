<template>
    <tr v-if="edit">
        <td colspan="15">
            <input type="text" class="form-control-sm" v-model="name">
            <span class="btn-dc" :title="language.data.cm3" @click="save">&#x2705;</span>
            <span class="btn-dc" :title="language.data.cm4" @click="cancel">&#x274c;</span>
        </td>
    </tr>
    <tr :class="{'searchTarget': isSearchTarget}" v-else>
        <td class="fit-cell">&#x1f512;</td>
        <td class="fit-cell" v-if="access === 'RW'"><item-info :item="file"></item-info></td>
        <td class="fit-cell" v-if="access === 'RW'"><span class="btn-dc" :title="language.data.cm2" @click="edit = true">&#x1f589;</span></td>
        <td><button class="btn btn-sm btn-link" @click="download">{{ file.name }}</button></td>
        <td>{{ file.size }} bytes</td>
        <td class="fit-cell">
            <span style="user-select: none">{{ language.data.cm10 }} &nbsp;</span>$id:{{ $parent.$props.item.id }}f{{ file.id }}&nbsp;
        </td>
        <td class="fit-cell" v-if="access === 'RW'"><span class="btn-dc" :title="language.data.cm5" @click="remove">&#x1f5d1;</span></td>
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
        }
    },
    mounted() {
        this.name = this.file.name;
    }
}
</script>

<style scoped>

</style>