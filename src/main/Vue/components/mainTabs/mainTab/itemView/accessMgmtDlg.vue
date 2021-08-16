<template>
    <div class="modal-dlg">
        <div class="modal-dlg-body">
            <div class="row">
                <div class="col">
                    {{ item.name }}
                    <button class="close" @click="$emit('close-dlg')">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </div>
            <div class="modal-dlg-scroll mt-3 p-1">
                <div class="row m-0">
                    <div class="col p-0">
                        <button class="btn btn-sm btn-outline-success" @click="addDlg = !addDlg">&#x2795;</button>
                    </div>
                </div>
                <div class="row m-0 mt-1" v-if="addDlg">
                    <div class="col p-0">
                        <select class="form-control-sm align-middle" v-model="groupId">
                            <option selected disabled value="null">{{ language.data.gp1 }}</option>
                            <option v-for="grp in groups" :value="grp.id">
                                {{ grp.name }}
                            </option>
                        </select>
                        <select class="form-control-sm align-middle" v-model="access">
                            <option selected disabled value="null">{{ language.data.amg1 }}</option>
                            <option value="NA">{{ language.data.amg2 }}</option>
                            <option value="PT">{{ language.data.amg3 }}</option>
                            <option value="RO">{{ language.data.amg4 }}</option>
                            <option value="RW">{{ language.data.amg5 }}</option>
                        </select>
                        <button class="btn btn-sm btn-outline-success" :disabled="groupId === null && access === null"
                                @click="setAccess">&check;
                        </button>
                        <button class="btn btn-sm btn-danger" @click="cancel">X</button>
                        <br>
                        <input type="checkbox" v-model="ptAbove"> {{ language.data.amg6 }}
                        <br>
                        <input type="checkbox" v-model="sameBelow"> {{ language.data.amg7 }}
                    </div>
                </div>
                <table class="table table-bordered table-striped table-sm mt-1">
                    <thead></thead>
                    <tbody>
                    <tr v-for="group in assignedGroups" :key="'access' + group.id">
                        <td>{{ group.name }}</td>
                        <td :class="paintAccess(group.access)">{{ printAccess(group.access) }}</td>
                        <td>
                            <button class="btn btn-sm btn-outline-danger" :title="language.data.cm5"
                                    @click="removeAccess(group.id, group.access, group.name)">
                                &#x1f5d1;
                            </button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>

<script>
import Sort from "../../../../sort";

export default {
    name: "accessMgmtDlg",
    props: {
        item: Object
    },
    emits: ['close-dlg'],
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            assignedGroups: [],
            groups: [],
            sort: new Sort("name", "asc"),
            addDlg: false,
            groupId: null,
            access: null,
            ptAbove: false,
            sameBelow: false
        }
    },
    methods: {
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
            this.addDlg = false;
            this.groupId = null;
            this.access = null;
            this.ptAbove = false;
            this.sameBelow = false;
        },
        printAccess(access) {
            switch (access) {
                case "NA": return this.language.data.amg2
                case "PT": return this.language.data.amg3
                case "RO": return this.language.data.amg4
                case "RW": return this.language.data.amg5
            }
            return access;
        },
        paintAccess(access) {
            switch (access) {
                case "NA": return ""
                case "PT": return "bg-info"
                case "RO": return "bg-success"
                case "RW": return "bg-danger"
            }
        },
        async getAssignedGroups() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    containerId: this.item.id
                });
                const answer = await $.ajax({
                    url: "/container/getAssignedGroups",
                    method: "POST",
                    data: encryptedData
                });
                this.assignedGroups = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async setAccess() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    containerId: this.item.id,
                    groupId: this.groupId,
                    access: this.access,
                    ptAbove: this.ptAbove,
                    sameBelow: this.sameBelow
                });
                await $.ajax({
                    url: "/container/setAccess",
                    method: "POST",
                    data: encryptedData
                });
                this.cancel();
                this.getAssignedGroups();
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async removeAccess(groupId, access, groupName) {
            if (confirm(this.language.data.amg8 + groupName + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        containerId: this.item.id,
                        groupId: groupId,
                        access: access,
                    });
                    await $.ajax({
                        url: "/container/removeAccess",
                        method: "POST",
                        data: encryptedData
                    });
                    this.getAssignedGroups();
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        }
    },
    beforeMount() {
        this.getGroups();
        this.getAssignedGroups();
    }
}
</script>

<style scoped>

</style>