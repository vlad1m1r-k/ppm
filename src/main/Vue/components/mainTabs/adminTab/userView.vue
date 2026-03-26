<template>
    <group-selector v-if="showGroupSelector" @close-dlg="showGroupSelector = false; $emit('user-changed')" :user="user"></group-selector>
    <user-ip-view v-if="showIpEditor" @close-dlg="showIpEditor = false" :user="user"></user-ip-view>
    <tr v-if="showEditDlg">
        <td colspan="10" class="flex-view">
            <input type="text" :placeholder="language.data.lf1" v-model="login">
            <input type="password" min="1" :placeholder="language.data.lf2" v-model="pwd">
            <select v-model="status">
                <option v-for="status in statuses" :value="status">{{ status }}</option>
            </select>
            <input type="checkbox" v-model="changePwdAtNextLogon"> {{ language.data.us5 }}
            <input type="checkbox" v-model="tfaStatus"> TFA
            <button class="btn-img acpt" :disabled="login.length === 0" :title="language.data.cm3" @click="save"></button>
            <button class="btn-img cncl" :title="language.data.cm4" @click="cancel"></button>
        </td>
    </tr>
    <tr v-else>
        <td>{{ user.login }}</td>
        <td :class="{'text-danger': user.status === 'DISABLED'}">{{ user.status }}</td>
        <td> {{ user.tfaStatus }} </td>
        <td>
            <button class="btn blue" @click="showGroupSelector = true">{{ language.data.gp1 }} ({{ user.groups.length }})</button>
        </td>
        <td>
            <button class="btn blue" @click="showIpEditor = true">{{ language.data.us4 }}</button>
        </td>
        <td class="fit"><button class="btn-img edit" :title="language.data.cm2" @click="showEditDlg = true"></button></td>
        <td class="fit"><button class="btn-img rmv" :title="language.data.cm5" @click="deleteUser"></button></td>
    </tr>
</template>

<script>
import groupSelector from "./groupSelector.vue";
import userIpView from "./userIpView.vue";

export default {
    name: "userView",
    emits: ["user-changed"],
    props: {
        user: Object,
        statuses: Array
    },
    components: {groupSelector, userIpView},
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            showEditDlg: false,
            showGroupSelector: false,
            showIpEditor: false,
            login: this.user.login,
            pwd: "",
            status: this.user.status,
            changePwdAtNextLogon: this.user.changePwd,
            tfaStatus: false
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
                    status: this.status,
                    changePwd: this.changePwdAtNextLogon,
                    tfaStatus: this.tfaStatus
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
    },
    beforeMount() {
        this.tfaStatus = this.user.tfaStatus === "DISABLED" ? false : true;
    }
}
</script>

<style scoped>

</style>