<template>
    <group-selector v-if="showGroupSelector" @close-dlg="showGroupSelector = false; $emit('user-changed')" :user="user"></group-selector>
    <tr v-if="showEditDlg">
        <td colspan="10">
            <input type="text" class="form-control-sm align-middle" :placeholder="language.data.lf1" v-model="login">
            <input type="password" class="form-control-sm align-middle" min="1" :placeholder="language.data.lf2" v-model="pwd">
            <select class="form-control-sm align-middle" v-model="status" :class="{'bg-danger': status === 'DISABLED'}">
                <option class="bg-light" v-for="status in statuses" :value="status">{{ status }}</option>
            </select>
            <button class="btn btn-sm btn-outline-success" :disabled="login.length === 0" :title="language.data.cm3"
                    @click="save">&check;
            </button>
            <button class="btn btn-sm btn-danger" :title="language.data.cm4" @click="cancel">X</button>
        </td>
    </tr>
    <tr v-else>
        <td>{{ user.login }}</td>
        <td :class="{'text-danger': user.status === 'DISABLED'}">{{ user.status }}</td>
        <td>
            <button class="btn btn-sm btn-outline-success" @click="showGroupSelector = true">{{ language.data.gp1 }} ({{ user.groups.length }})</button>
        </td>
        <td><button class="btn btn-sm btn-outline-success" :title="language.data.cm2" @click="showEditDlg = true">&#x1f589;</button></td>
        <td><button class="btn btn-sm btn-outline-danger" :title="language.data.cm5" @click="deleteUser">&#x1f5d1;</button></td>
    </tr>
</template>

<script>
import groupSelector from "./groupSelector.vue";

export default {
    name: "userView",
    emits: ["user-changed"],
    props: {
        user: Object,
        statuses: Array
    },
    components: {groupSelector},
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            showEditDlg: false,
            showGroupSelector: false,
            login: this.user.login,
            pwd: "",
            status: this.user.status
        }
    },
    methods: {
        async save() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    id: this.user.id,
                    login: this.login,
                    pwd: this.pwd,
                    status: this.status
                });
                const answer = await $.ajax({
                    url: "/user/editUser",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                if (data.message) {
                    this.eventHub.emit("show-msg", this.language.data[data.message]);
                } else {
                    this.showEditDlg = false;
                    this.pwd = "";
                    this.$emit("user-changed");
                }
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        cancel() {
            this.login = this.user.login;
            this.pwd = "";
            this.status = this.user.status;
            this.showEditDlg = false;
        },
        async deleteUser() {
            if (confirm(this.language.data.cm5 + " " + this.user.login + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        id: this.user.id,
                    });
                    await $.ajax({
                        url: "/user/deleteUser",
                        method: "POST",
                        data: encryptedData
                    });
                    this.$emit("user-changed");
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        }
    }
}
</script>

<style scoped>

</style>