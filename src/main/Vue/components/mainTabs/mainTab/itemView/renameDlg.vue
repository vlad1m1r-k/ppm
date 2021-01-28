<template>
    <div>
        <input class="form-control-sm" v-model="name">
        <button class="btn btn-sm btn-outline-success" :disabled="name.length === 0" @click="renameContainer">&check;
        </button>
        <button class="btn btn-sm btn-outline-danger" @click="$emit('close-dlg')">&Chi;</button>
    </div>
</template>

<script>
export default {
    name: "renameDlg",
    props: {
        item: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            name: ""
        }
    },
    methods: {
        async renameContainer() {
            this.eventHub.emit("show-msg", "");
            const confResult = confirm(this.language.data.iv4 + " \"" + this.item.name + "\" ?");
            if (confResult && this.item.name !== "root") {
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        item: this.item.id,
                        name: this.name
                    });
                    const answer = await $.ajax({
                        url: "/container/rename",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    } else {
                        this.$emit('close-dlg');
                        this.eventHub.emit("update-tree");
                    }
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        }
    },
    mounted() {
        this.name = this.item.name;
    }
}
</script>

<style scoped>

</style>