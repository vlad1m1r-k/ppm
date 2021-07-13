<template>
    <tr>
        <td>
            <input type="checkbox" :value="file.id" v-model="$parent.$data.checkedFls">
        </td>
        <td><button class="btn btn-sm btn-link" @click="downloadFile">{{ file.name }}</button></td>
        <td>{{ file.size }} bytes</td>
        <td>{{ file.createdDate }}</td>
        <td>{{ file.createdBy }}</td>
        <td>{{ file.editedDate }}</td>
        <td>{{ file.editedBy }}</td>
        <td>{{ file.deletedDate }}</td>
        <td>{{ file.deletedBy }}</td>
        <td>
            <span class="btn-dc text-success" :title="language.data.cm7" @click="restore">&#x21ba;</span>
        </td>
        <td>
            <span class="btn-dc" :title="language.data.cm5" @click="remove">&#x1f5d1;</span>
        </td>
    </tr>
</template>

<script>
export default {
    name: "flsView",
    props: {
        file: Object
    },
    emits: ['update-items'],
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider
        }
    },
    methods: {
        async downloadFile() {
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
        async restore(event, mass) {
            if (mass || confirm(this.language.data.cm7 + " " + this.file.name + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        fileId: this.file.id
                    });
                    const answer = await $.ajax({
                        url: "/container/restoreFile",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    } else {
                        this.$emit("update-items");
                        this.eventHub.emit("update-tree");
                    }
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        async remove(event, mass) {
            if (mass || confirm(this.language.data.cm5 + " " + this.file.name + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        fileId: this.file.id,
                        permanent: true
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
                        this.$emit("update-items");
                    }
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        restoreFiles(files) {
            if (files.includes(this.file.id)) {
                this.restore(null, true);
            }
        },
        removeFiles(files) {
            if (files.includes(this.file.id)) {
                this.remove(null, true);
            }
        }
    },
    mounted() {
        this.eventHub.on("restore-files", this.restoreFiles);
        this.eventHub.on("remove-files", this.removeFiles);
    },
    beforeUnmount() {
        this.eventHub.off("restore-files", this.restoreFiles);
        this.eventHub.off("remove-files", this.removeFiles);
    }
}
</script>

<style scoped>

</style>