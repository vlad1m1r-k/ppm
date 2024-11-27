<template>
    <div class="modal" v-if="tokenProvider.token === null">
        <div class="modal-body login">
            <div class="alert" v-if="message" @click="message = ''">
                {{ message }}
            </div>
            <input type="text" class="input" :placeholder="language.data.lf1" v-model="login" ref="lfLogin">
            <input type="password" class="input" :placeholder="language.data.lf2" v-model="password" @keypress.enter="doLogin" ref="lfPwd">
            <button class="btn blue" @click=doLogin>{{ language.data.lf3 }}</button>
        </div>
    </div>
</template>

<script>
export default {
    name: "loginForm",
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            message: "",
            login: "",
            password: ""
        }
    },
    watch: {
        'tokenProvider.token'(token) {
            if (!token) {
                this.$nextTick(() => {
                    if (this.login) {
                        this.$refs.lfPwd.focus();
                    } else {
                        this.$refs.lfLogin.focus();
                    }
                })
            }
        }
    },
    methods: {
        async doLogin() {
            if (this.login.length > 1) {
                this.message = "";
                try {
                    const answer = await this.tokenProvider.login(this.login, this.password);
                    if (answer) {
                        this.eventHub.emit("show-msg", this.language.data.lfe2);
                    }
                } catch (e) {
                    if (e.message) {
                        this.message = this.language.data[e.message];
                    } else {
                        this.message = this.errorParser(e);
                    }
                } finally {
                    this.password = "";
                }
            }
        }
    },
    mounted() {
        this.$refs.lfLogin.focus();
    }
}
</script>

<style scoped>
</style>