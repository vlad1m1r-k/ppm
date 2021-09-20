<template>
    <div class="us-form-bg pl-3 pr-3 pt-1">
        <div class="text-primary">{{ language.data.uss1 }}</div>
        <table>
            <thead></thead>
            <tbody>
            <tr v-if="msg">
                <td colspan="10" class="alert-danger">
                    {{ msg }}
                </td>
            </tr>
            <tr>
                <td>{{ language.data.uss2 }}</td>
                <td><input type="password" class="form-control-sm" v-model="pwd" ref="usPwd1" @keypress.enter="send"></td>
            </tr>
            <tr>
                <td>{{ language.data.uss3 }}</td>
                <td><input type="password" class="form-control-sm" v-model="pwd2" @keypress.enter="send"></td>
            </tr>
            </tbody>
        </table>
        <button class="btn btn-sm btn-success" @click="send">{{ language.data.cm3 }}</button>
    </div>
</template>

<script>
export default {
    name: "userSettings",
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            msg: "",
            pwd: "",
            pwd2: ""
        }
    },
    methods: {
        async send() {
            this.msg = "";
            if (this.pwd && this.pwd.length > 0 && this.pwd2 && this.pwd2.length > 0) {
                if (this.pwd === this.pwd2) {
                    this.eventHub.emit("show-msg", "");
                    try {
                        const token = await this.tokenProvider.getToken();
                        const encryptedData = await cryptoProvider.encrypt({
                            token: token,
                            pwd: this.pwd
                        });
                        const answer = await $.ajax({
                            url: "/user/setPwd",
                            method: "POST",
                            data: encryptedData
                        });
                        const data = cryptoProvider.decrypt(answer);
                        if (data.message) {
                            this.msg = this.language.data[data.message];
                        } else {
                            this.pwd = "";
                            this.pwd2 = "";
                        }
                    } catch (e) {
                        this.pwd = "";
                        this.pwd2 = "";
                        this.eventHub.emit("show-msg", this.errorParser(e));
                    }
                } else {
                    this.msg = this.language.data.usse5;
                }
            } else {
                this.msg = this.language.data.usse2;
            }
        }
    },
    mounted() {
        this.$refs.usPwd1.focus();
    }
}
</script>

<style scoped>

</style>