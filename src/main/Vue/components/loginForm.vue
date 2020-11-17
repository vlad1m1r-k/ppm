<template>
    <div class="l-modal" v-if="tokenProvider.token === null">
        <div class="l-modal-body">
            <div class="alert alert-danger" v-if="message" @click="message = ''">
                {{ message }}
            </div>
            <div class="form-group">
                <input type="text" class="form-control" :placeholder="language.data.lf1" v-model="login">
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
            tokenProvider: this.$root.tokenProvider,
            language: this.$root.$data.language,
            message: "",
            login: "",
            password: ""
        }
    },
    methods: {
        doLogin() {
            if (this.login.length < 1) {
                return;
            }
            this.message = "";
            this.tokenProvider.login(this.login, this.password, this.$data);
            this.password = "";
        }
    }
}
</script>

<style scoped>
    .l-modal {
        left: 0;
        top: 0;
        right: 0;
        bottom: 0;
        position: fixed;
        background: rgba(0, 0, 0, 0.69);
        z-index: 1050;
        transition: all 0.4s ease-in-out;
        -moz-transition: all 0.4s ease-in-out;
        -webkit-transition: all 0.4s ease-in-out;
        margin: 0;
        padding: 0;
        pointer-events: auto;
    }

    .l-modal-body {
        padding: 15px;
        width: 400px;
        margin: 30px auto;
        border-radius: 5px;
        background: #fff;
        box-shadow: 0 3px 7px rgba(0, 0, 0, .25);
        -moz-box-shadow: 0 3px 7px rgba(0, 0, 0, .25);
        -webkit-box-shadow: 0 3px 7px rgba(0, 0, 0, .25);
        box-sizing: border-box;
        -moz-box-sizing: border-box;
        -webkit-box-sizing: border-box;
    }
</style>