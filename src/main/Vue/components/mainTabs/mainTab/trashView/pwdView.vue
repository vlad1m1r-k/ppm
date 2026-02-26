<template>
    <tr>
        <td>
            <input type="checkbox" :value="pwd.id" v-model="$parent.$data.checkedPass">
        </td>
        <td><button class="btn link" @click="toggle" :title="language.data.cm1">{{ pwd.name }}</button></td>
        <td>{{ pwd.createdDate }}</td>
        <td>{{ pwd.createdBy }}</td>
        <td>{{ pwd.editedDate }}</td>
        <td>{{ pwd.editedBy }}</td>
        <td>{{ pwd.deletedDate }}</td>
        <td>{{ pwd.deletedBy }}</td>
        <td class="fit">
            <button class="btn-img redo" :title="language.data.cm7" @click="restore"></button>
        </td>
        <td class="fit">
            <button class="btn-img rmv" :title="language.data.cm5" @click="remove"></button>
        </td>
    </tr>
    <tr v-show="show">
        <td colspan="15">
            <input class="iv-input" v-model="login" readonly>
            <div style="display: flex">
                <button class="btn-img show" @click="loadPwdBody" :title="language.data.cm1"></button>
                <button class="btn-img copy" :title="language.data.cm6" @click="pwdToClipboard"></button>
                <input class="iv-input" v-model="pass" readonly placeholder="******">
            </div>
            <textarea class="text-box" rows="4" readonly v-model="note"></textarea>
        </td>
    </tr>
</template>

<script>
export default {
    name: "pwdView",
    props: {
        pwd: Object
    },
    emits: ['update-items'],
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            show: false,
            login: "",
            pass: "",
            note: ""
        }
    },
    methods: {
        toggle() {
            if (this.show) {
                this.show = false;
                this.login = "";
                this.pass = "";
                this.note = "";
            } else {
                this.loadPwdEnv();
                this.show = true;
            }
        },
        async loadPwdEnv() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    pwd: this.pwd.id,
                });
                const answer = await $.ajax({
                    url: "/container/getPwdEnv",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                this.login = data.login;
                this.note = data.note;
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async loadPwdBody() {
            if (this.pass) {
                this.pass = "";
            } else {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        pwd: this.pwd.id,
                    });
                    const answer = await $.ajax({
                        url: "/container/getPwdBody",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    this.pass = data.password;
                    setTimeout(this.clearPwd, 10000);
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        async pwdToClipboard() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    pwd: this.pwd.id,
                });
                const answer = await $.ajax({
                    url: "/container/getPwdBody",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                navigator.clipboard.writeText(data.password);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async remove(event, mass) {
            if (mass || confirm(this.language.data.cm5 + " " + this.pwd.name + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        pwd: this.pwd.id,
                        permanent: true
                    });
                    const answer = await $.ajax({
                        url: "/container/removePassword",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    }
                    this.$emit("update-items");
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        async restore(event, mass) {
            if (mass || confirm(this.language.data.cm7 + " " + this.pwd.name + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        pwdId: this.pwd.id
                    });
                    const answer = await $.ajax({
                        url: "/container/restorePasswd",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    }
                    this.$emit("update-items");
                    this.eventHub.emit("update-tree");
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        clearPwd() {
            this.pass = "";
        },
        removePass(pass) {
            if (pass.includes(this.pwd.id)) {
                this.remove(null, true);
            }
        },
        restorePass(pass) {
            if (pass.includes(this.pwd.id)) {
                this.restore(null, true);
            }
        }
    },
    mounted() {
        this.eventHub.on("remove-passwords", this.removePass);
        this.eventHub.on("restore-passwords", this.restorePass);
    },
    beforeUnmount() {
        this.eventHub.off("remove-passwords", this.removePass);
        this.eventHub.off("restore-passwords", this.restorePass);
    }
}
</script>

<style scoped>

</style>