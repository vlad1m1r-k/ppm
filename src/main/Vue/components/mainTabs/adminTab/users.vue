<template>
    <table class="table table-bordered table-striped table-sm">
        <thead class="tab-header-area">
        <tr>
            <th><button class="btn btn-sm btn-link" @click="setSort('login')">{{ language.data.lf1 }}</button></th>
            <th><button class="btn btn-sm btn-link" @click="setSort('status')">{{ language.data.us2 }}</button></th>
            <th>{{ language.data.gp1 }}</th>
            <th><span class="btn-dc text-success" @click="showAddDlg = !showAddDlg">&#x2795;</span></th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <tr v-show="showAddDlg">
            <td colspan="10">
                <input type="text" class="form-control-sm align-middle" min="1" :placeholder="language.data.lf1" v-model="login">
                <input type="password" class="form-control-sm align-middle" min="1" :placeholder="language.data.lf2" v-model="pwd">
                <select class="form-control-sm align-middle" v-model="status" :class="{'bg-danger': status === 'DISABLED'}">
                    <option class="bg-light" v-for="status in statuses" :value="status">{{ status }}</option>
                </select>
                <button class="btn btn-sm btn-outline-success" :disabled="login.length === 0 || pwd.length === 0" @click="addUser">&check;
                </button>
                <button class="btn btn-sm btn-danger" @click="cancel">X</button>
            </td>
        </tr>
        <user-view v-for="user in users" :user="user" :statuses="statuses" :key="'usv' + user.id"></user-view>
        </tbody>
    </table>
</template>

<script>
import userView from "./userView.vue";
import Sort from "../../../sort";

export default {
    name: "users",
    components: {
        userView
    },
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            users: [],
            sort: new Sort("login", "asc"),
            showAddDlg: false,
            login: "",
            pwd: "",
            status: "ENABLED",
            statuses: null
        }
    },
    methods: {
        async getUsers() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    sort: this.sort.toString()
                });
                const answer = await $.ajax({
                    url: "/user/getUsers",
                    method: "POST",
                    data: encryptedData
                });
                this.users = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        setSort(field) {
            this.sort.setField(field);
            this.getUsers();
        },
        async getStatuses() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({token: token});
                const answer = await $.ajax({
                    url: "/user/getStatuses",
                    method: "POST",
                    data: encryptedData
                });
                this.statuses = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        cancel() {
            this.login = "";
            this.pwd = "";
            this.showAddDlg = false;
        },
        async addUser() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    login: this.login,
                    pwd: this.pwd,
                    status: this.status
                });
                const answer = await $.ajax({
                    url: "/user/addUser",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                if (data.message) {
                    this.eventHub.emit("show-msg", this.language.data[data.message]);
                } else {
                    this.login = "";
                    this.pwd = "";
                    this.getUsers();
                }
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        }
    },
    beforeMount() {
        this.getUsers();
        this.getStatuses();
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
</style>