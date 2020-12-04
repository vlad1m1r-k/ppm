<template>
    <div>
        <input class="form-control-sm" v-model="name">
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
            this.$eventHub.$emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await Vue.cryptoProvider.encrypt({
                    token: token,
                    parent: this.item.id,
                    name: this.name
                });
                const answer = await $.ajax({
                    url: "/container/add",
                    method: "POST",
                    data: encryptedData
                });
                const data = Vue.cryptoProvider.decrypt(answer);
                if (data.message) {
                    this.$eventHub.$emit("show-msg", this.language.data[data.message]);
                } else {
                    this.$emit('close-dlg');
                    this.$eventHub.$emit("update-tree");
                }
            } catch (e) {
                this.$eventHub.$emit("show-msg", Vue.errorParser(e));
            }
        }
    }
}
</script>

<style scoped>

</style>