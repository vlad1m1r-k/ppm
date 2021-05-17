<template>
    <table class="table table-bordered table-striped table-sm">
        <thead class="tab-header-area">
        <tr>
            <th><button class="btn btn-sm btn-link" @click="setSort('name')">{{ language.data.gp2 }}</button></th>
            <th><button class="btn btn-sm btn-link" @click="setSort('adminSettings')">{{ language.data.gp3 }}</button></th>
            <th>{{ language.data.us1 }}</th>
            <th>{{ language.data.amg1 }}</th>
            <th><span class="btn-dc text-success" @click="showAddDlg = !showAddDlg">&#x2795;</span></th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <tr v-show="showAddDlg">
            <td colspan="10">
                <input type="text" class="form-control-sm align-middle" min="1" :placeholder="language.data.gp2" v-model="name">
                &nbsp;{{ language.data.gp3 }}:
                <select class="form-control-sm align-middle" :class="{'bg-danger': adminSettings}" v-model="adminSettings"
                        :title="language.data.gp3">
                    <option class="bg-light" :value=false>FALSE</option>
                    <option class="bg-danger" :value=true>TRUE</option>
                </select>
                <button class="btn btn-sm btn-outline-success" :disabled="name.length === 0" @click="addGroup">&check;
                </button>
                <button class="btn btn-sm btn-danger" @click="cancel">X</button>
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
    },
    beforeMount() {
        this.getGroups();
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