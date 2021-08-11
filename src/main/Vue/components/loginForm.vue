<template>
    <div class="l-modal" v-if="tokenProvider.token === null">
        <div class="l-modal-body">
            <div class="alert alert-danger" v-if="message" @click="message = ''">
                {{ message }}
            </div>
            <div class="form-group">
                <input type="text" class="form-control" :placeholder="language.data.lf1" v-model="login" autofocus>
            </div>
            <div class="form-group">
                <input type="password" class="form-control" :placeholder="language.data.lf2" v-model="password" @keypress.enter="doLogin">
            </div>
            <div class="container">
                <div class="row justify-content-md-center">
                    <button class="btn btn-primary" @click=doLogin>{{ language.data.lf3 }}</button>
                </div>
            </div>
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
    }
}
</script>

<style scoped>
</style>