<template>
    <tr>
        <td>
            <input type="checkbox" :value="pwd.id" v-model="$parent.$data.checkedPass">
        </td>
        <td>
            <span class="btn-dc" @click="toggle" :title="language.data.cm1">&#x1f441;</span>
        </td>
        <td>{{ pwd.name }}</td>
        <td>{{ pwd.createdDate }}</td>
        <td>{{ pwd.createdBy }}</td>
        <td>{{ pwd.editedDate }}</td>
        <td>{{ pwd.editedBy }}</td>
        <td>{{ pwd.deletedDate }}</td>
        <td>{{ pwd.deletedBy }}</td>
        <td>
            <span class="btn-dc text-success" :title="language.data.cm7" @click="restore">&#x21ba;</span>
        </td>
        <td>
            <span class="btn-dc" :title="language.data.cm5" @click="remove">&#x1f5d1;</span>
        </td>
    </tr>
    <tr v-show="show">
        <td colspan="15">
            <input class="form-control" v-model="login" readonly>
            <div style="display: flex">
                <div class="btn-st fs" @click="loadPwdBody" :title="language.data.cm1">&#x1f441;</div>
                <div class="btn-st" :title="language.data.cm6" @click="pwdToClipboard">&#x1f4cb;</div>
                <input class="form-control er" v-model="pass" readonly placeholder="******">
            </div>
            <textarea class="form-control" rows="4" readonly v-model="note"></textarea>
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
.btn-dc {
    cursor: pointer;
    user-select: none;
    padding-left: 2px;
    padding-right: 2px;
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