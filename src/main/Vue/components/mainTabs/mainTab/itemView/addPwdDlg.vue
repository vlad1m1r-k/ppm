<template>
    <button class="btn-img acpt" :disabled="name.length === 0" @click="addPasswd"></button>
    <button class="btn-img cncl" @click="$emit('close-dlg')"></button>
    <input class="iv-input" v-model="name" :placeholder="language.data.iv6" ref="ivAddPwd" @keydown.esc="$emit('close-dlg')">
    <input class="iv-input" v-model="login" :placeholder="language.data.lf1">
    <input class="iv-input" v-model="pass" :placeholder="language.data.lf2">
    <textarea class="text-box" rows="4" v-model="note"></textarea>
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
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
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
                const data = cryptoProvider.decrypt(answer);
                if (data.message) {
                    this.eventHub.emit("show-msg", this.language.data[data.message]);
                } else {
                    this.$emit('close-dlg');
                }
                this.eventHub.emit("update-tree");
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        }
    },
    mounted() {
        this.$refs.ivAddPwd.focus();
    }
}
</script>

<style scoped>

</style>