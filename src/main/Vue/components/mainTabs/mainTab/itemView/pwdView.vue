<template>
    <div>
        <div class="decor">
            &#x1f512; &nbsp; {{ pwd.name }} &nbsp;
            <span class="btn-dc" @click="toggle" :title="language.data.cm1">&#x1f441;</span>
            <span class="btn-dc" v-show="show && access === 'RW'" :title="language.data.cm2" @click="edit = true">&#x1f589;</span>
            <span class="btn-dc" v-show="show && access === 'RW' && edit" :title="language.data.cm3" @click="save">&#x2705;</span>
            <span class="btn-dc" v-show="show && access === 'RW' && edit" :title="language.data.cm4" @click="cancel">&#x274c;</span>
            <span class="btn-dc float-right" v-show="access === 'RW'" :title="language.data.cm5" @click="remove">&#x1f5d1;</span>
        </div>
        <input class="form-control" v-show="show && edit" v-model="name">
        <input class="form-control" v-show="show" v-model="login" :readonly="!edit">
        <div v-show="show" style="display: flex">
            <div class="btn-st fs" v-show="!edit" @click="loadPwdBody" :title="language.data.cm1">&#x1f441;</div>
            <div class="btn-st" v-show="!edit" :title="language.data.cm6" @click="pwdToClipboard">&#x1f4cb;</div>
            <input class="form-control er" v-model="pass" :readonly="!edit" placeholder="******">
        </div>
        <textarea class="form-control" rows="4" :readonly="!edit" v-show="show" v-model="note"></textarea>
    </div>
</template>

<script>
export default {
    name: "pwdView",
    props: {
        pwd: Object,
        access: String
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            show: false,
            edit: false,
            name: "",
            login: "",
            pass: "",
            note: ""
        }
    },
    methods: {
        toggle() {
            if (this.show) {
                this.show = false;
                this.edit = false;
                this.name = this.pwd.name;
                this.login = "";
                this.pass = "";
                this.note = "";
            } else {
                this.loadPwdEnv();
                this.show = true;
            }
        },
        async loadPwdEnv() {
            this.$eventHub.$emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await Vue.cryptoProvider.encrypt({
                    token: token,
                    pwd: this.pwd.id,
                });
                const answer = await $.ajax({
                    url: "/container/getPwdEnv",
                    method: "POST",
                    data: encryptedData
                });
                const data = Vue.cryptoProvider.decrypt(answer);
                this.login = data.login;
                this.note = data.note;
            } catch (e) {
                this.$eventHub.$emit("show-msg", Vue.errorParser(e));
            }
        },
        async loadPwdBody() {
            if (this.pass) {
                this.pass = "";
            } else {
                this.$eventHub.$emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await Vue.cryptoProvider.encrypt({
                        token: token,
                        pwd: this.pwd.id,
                    });
                    const answer = await $.ajax({
                        url: "/container/getPwdBody",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = Vue.cryptoProvider.decrypt(answer);
                    this.pass = data.password;
                    setTimeout(this.clearPwd, 10000);
                } catch (e) {
                    this.$eventHub.$emit("show-msg", Vue.errorParser(e));
                }
            }
        },
        async pwdToClipboard() {
            this.$eventHub.$emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await Vue.cryptoProvider.encrypt({
                    token: token,
                    pwd: this.pwd.id,
                });
                const answer = await $.ajax({
                    url: "/container/getPwdBody",
                    method: "POST",
                    data: encryptedData
                });
                const data = Vue.cryptoProvider.decrypt(answer);
                navigator.clipboard.writeText(data.password);
            } catch (e) {
                this.$eventHub.$emit("show-msg", Vue.errorParser(e));
            }
        },
        async save() {
            if (this.name && confirm(this.language.data.cm3 + " " + this.pwd.name + "?")) {
                this.$eventHub.$emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await Vue.cryptoProvider.encrypt({
                        token: token,
                        pwd: this.pwd.id,
                        name: this.name,
                        login: this.login,
                        pass: this.pass,
                        note: this.note
                    });
                    const answer = await $.ajax({
                        url: "/container/editPassword",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = Vue.cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.$eventHub.$emit("show-msg", this.language.data[data.message]);
                    }
                    this.edit = false;
                    this.pass = "";
                    this.$eventHub.$emit("update-tree");
                } catch (e) {
                    this.$eventHub.$emit("show-msg", Vue.errorParser(e));
                }
            }
        },
        cancel() {
            this.edit = false;
            this.name = this.pwd.name;
            this.pass = "";
            this.loadPwdEnv();
        },
        async remove() {
            if (this.name && confirm(this.language.data.cm5 + " " + this.pwd.name + "?")) {
                this.$eventHub.$emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await Vue.cryptoProvider.encrypt({
                        token: token,
                        pwd: this.pwd.id
                    });
                    const answer = await $.ajax({
                        url: "/container/removePassword",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = Vue.cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.$eventHub.$emit("show-msg", this.language.data[data.message]);
                    }
                    this.$eventHub.$emit("update-tree");
                } catch (e) {
                    this.$eventHub.$emit("show-msg", Vue.errorParser(e));
                }
            }
        },
        clearPwd() {
            if (!this.edit) {
                this.pass = "";
            }
        }
    },
    mounted() {
        this.name = this.pwd.name;
    }
}
</script>

<style scoped>
.decor {
    white-space: nowrap;
    border: 2px solid black;
    border-radius: 10px;
    padding-left: 5px;
}

.btn-dc {
    cursor: pointer;
    user-select: none;
}

.btn-dc:hover {
    background-color: darkgray;
    border-radius: 4px;
}
.btn-st {
    cursor: pointer;
    user-select: none;
    display: flex;
    align-items: center;
    background-color: #e0e0e0;
    padding-left: 3px;
    padding-right: 3px;
}
.btn-st:hover {
    background-color: darkgray;
}
.fs {
    border-top-left-radius: 5px;
    border-bottom-left-radius: 5px;
}
.er {
    border-top-left-radius: 0 !important;
    border-bottom-left-radius: 0 !important;
}
</style>