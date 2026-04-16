<template>
    <table class="table">
        <thead>
        <tr>
            <th><button class="btn link" @click="setSort('name')">{{ language.data.gp2 }}</button></th>
            <th><button class="btn link" @click="setSort('adminSettings')">{{ language.data.gp3 }}</button></th>
            <th>{{ language.data.us1 }}</th>
            <th>{{ language.data.amg1 }}</th>
            <th><button class="btn blue" @click="showAddDlg = !showAddDlg">&#x2795;</button></th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <tr v-show="showAddDlg">
            <td class="flex-view" colspan="10">
                <input type="text" min="1" :placeholder="language.data.gp2" v-model="name" ref="admAddGrp" @keypress.enter="addGroup" @keydown.esc="cancel">
                &nbsp;{{ language.data.gp3 }}:
                <select v-model="adminSettings" :title="language.data.gp3">
                    <option :value=false>FALSE</option>
                    <option :value=true>TRUE</option>
                </select>
                <button class="btn-img acpt" :disabled="name.length === 0" @click="addGroup"></button>
                <button class="btn-img cncl" @click="cancel"></button>
            </td>
        </tr>
        <group-view v-for="group in groups" :group="group" :key="'gpv' + group.id" @group-changed="getGroups"></group-view>
        </tbody>
    </table>
</template>

<script>
import Sort from "../../../sort";
import groupView from "./groupView.vue";

export default {
    name: "groups",
    components: {groupView},
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            groups: [],
            sort: new Sort("name", "asc"),
            showAddDlg: false,
            name: "",
            adminSettings: false
        }
    },
    watch: {
        showAddDlg(value) {
            if (value) {
                this.$nextTick(() => this.$refs.admAddGrp.focus());
            }
        }
    },
    methods: {
        setSort(field) {
            this.sort.setField(field);
            this.getGroups();
        },
        async getGroups() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    sort: this.sort.toString()
                });
                const answer = await $.ajax({
                    url: "/group/getGroups",
                    method: "POST",
                    data: encryptedData
                });
                this.groups = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        cancel() {
            this.name = "";
            this.adminSettings = false;
            this.showAddDlg = false;
        },
        async addGroup() {
            this.eventHub.emit("show-msg", "");
            if (this.name) {
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        name: this.name,
                        admin: this.adminSettings
                    });
                    const answer = await $.ajax({
                        url: "/group/addGroup",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    } else {
                        this.name = "";
                        this.adminSettings = false;
                        this.getGroups();
                    }
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        }
    },
    beforeMount() {
        this.getGroups();
    }
}
</script>

<style scoped>
</style>