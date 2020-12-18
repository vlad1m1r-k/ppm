<template>
    <div>
        <div class="row">
            <div class="col">
                <button class="btn btn-sm btn-outline-success" :disabled="name.length === 0" @click="addPasswd">&check;
                </button>
                <button class="btn btn-sm btn-outline-danger" @click="$emit('close-dlg')">&Chi;</button>
            </div>
        </div>
        <div class="row">
            <div class="col">
                <input class="form-control" v-model="name" :placeholder="language.data.iv6">
            </div>
        </div>
        <div class="row">
            <div class="col">
                <input class="form-control" v-model="login" :placeholder="language.data.lf1">
            </div>
        </div>
        <div class="row">
            <div class="col">
                <input class="form-control" v-model="pass" :placeholder="language.data.lf2">
            </div>
        </div>
        <div class="row">
            <div class="col">
                <textarea class="form-control" rows="3" v-model="note"></textarea>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "addPwdDlg",
    props: {
        item: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            name: "",
            login: "",
            pass: "",
            note: ""
        }
    },
    methods: {
        async addPasswd() {
            this.$eventHub.$emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await Vue.cryptoProvider.encrypt({
                    token: token,
                    parent: this.item.id,
                    name: this.name,
                    login: this.login,
                    pass: this.pass,
                    note: this.note
                });
                const answer = await $.ajax({
                    url: "/container/addPasswd",
                    method: "POST",
                    data: encryptedData
                });
                const data = Vue.cryptoProvider.decrypt(answer);
                if (data.message) {
                    this.$eventHub.$emit("show-msg", this.language.data[data.message]);
                } else {
                    this.$emit('close-dlg');
                }
                this.$eventHub.$emit("update-tree");
            } catch (e) {
                this.$eventHub.$emit("show-msg", Vue.errorParser(e));
            }
        }
    }
}
</script>

<style scoped>

</style>