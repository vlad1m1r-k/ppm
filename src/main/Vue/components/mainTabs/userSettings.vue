<template>
    <div class="modal">
        <div class="modal-body dialog">
            <div class="modal-header">
                {{ language.data.uss1 }}
                <button class="btn-img cncl" @click="$emit('close-dlg')"></button>
            </div>
            <div class="alert" v-if="msg">
                {{ msg }}
            </div>
            <table>
                <thead></thead>
                <tbody>
                    <tr>
                        <td>{{ language.data.uss4 }}</td>
                        <td><input type="password" class="input" v-model="oldPwd" ref="usPwd1" @keypress.enter="send"></td>
                    </tr>
                    <tr>
                        <td>{{ language.data.uss2 }}</td>
                        <td><input type="password" class="input" v-model="pwd" @keypress.enter="send"></td>
                    </tr>
                    <tr>
                        <td>{{ language.data.uss3 }}</td>
                        <td><input type="password" class="input" v-model="pwd2" @keypress.enter="send">
                        </td>
                    </tr>
                </tbody>
            </table>
            <button class="btn blue" @click="send">{{ language.data.cm3 }}</button>
        </div>
    </div>
</template>

<script>
export default {
    name: "userSettings",
    emits: ["close-dlg"],
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            msg: "",
            pwd: "",
            pwd2: "",
            oldPwd: ""
        }
    },
    methods: {
        async send() {
            this.msg = "";
            if (this.pwd && this.pwd.length > 0 && this.pwd2 && this.pwd2.length > 0 && this.oldPwd && this.oldPwd.length > 0) {
                if (this.pwd === this.pwd2) {
                    this.eventHub.emit("show-msg", "");
                    try {
                        const token = await this.tokenProvider.getToken(true);
                        const encryptedData = await cryptoProvider.encrypt({
                            token: token,
                            pwd: this.pwd,
                            oldPwd: this.oldPwd
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
                            this.tokenProvider.setToken(JSON.parse(data.data));
                            this.pwd = "";
                            this.pwd2 = "";
                            this.oldPwd = "";
                            this.$emit('close-dlg');
                        }
                    } catch (e) {
                        this.pwd = "";
                        this.pwd2 = "";
                        this.oldPwd = "";
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