<template>
    <div>
        <input class="form-control-sm" v-model="name" @keypress.enter="addContainer" @keydown.esc="$emit('close-dlg')" ref="ivAddCnt">
        <button class="btn btn-sm btn-outline-success" :disabled="name.length === 0" @click="addContainer">&check;
        </button>
        <button class="btn btn-sm btn-outline-danger" @click="$emit('close-dlg')">&Chi;</button>
    </div>
</template>

<script>
export default {
    name: "addDlg",
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
        async addContainer() {
            this.eventHub.emit("show-msg", "");
            if (this.name) {
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        parent: this.item.id,
                        name: this.name
                    });
                    const answer = await $.ajax({
                        url: "/container/add",
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
        },
        cancel() {
            this.$emit("close-dlg");
        }
    },
    mounted() {
        this.$refs.ivAddCnt.focus();
    }
}
</script>

<style scoped>

</style>