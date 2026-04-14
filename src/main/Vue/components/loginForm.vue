<template>
    <div class="modal" v-if="tokenProvider.token === null || tokenProvider.tfaSetup || tokenProvider.tfaRequired">
        <div class="modal-body login">
            <div class="alert" v-if="message" @click="message = ''">
                {{ message }}
            </div>
            <template v-if="tokenProvider.token === null">
                <input type="text" class="input" :placeholder="language.data.lf1" v-model="login" ref="lfLogin">
                <input type="password" class="input" :placeholder="language.data.lf2" v-model="password" @keypress.enter="doLogin" ref="lfPwd">
                <button class="btn blue" @click=doLogin>{{ language.data.lf3 }}</button>
            </template>
            <template v-else>
                <template v-if="tokenProvider.tfaSetup">
                    {{ language.data.lf4 }}
                    <img :src="tokenProvider.tfaQrCode">
                </template>
                {{ language.data.lf6 }}
                <input class="input" type="text" v-model="tfaCode" @keypress.enter="tfaVerify">
                <button class="btn blue" :disabled="!isCodeValid" @click="tfaVerify">{{ language.data.lf5 }}</button>
            </template>
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
            password: "",
            tfaCode: ""
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
    computed: {
        isCodeValid() {
            return this.tfaCode.match("^[0-9]{6}$");
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
        },
        async tfaVerify() {
            if (this.isCodeValid) {
                this.message="";
                try {
                    await this.tokenProvider.verifyTfaCode(this.tfaCode);
                } catch (e) {
                    if (e.message) {
                        this.message = this.language.data[e.message];
                    } else {
                        this.message = this.errorParser(e);
                    }
                } finally {
                    this.tfaCode = "";
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