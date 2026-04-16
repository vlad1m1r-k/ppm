<template>
    <div class="modal">
        <div class="modal-body dialog">
            <div class="modal-header">
                {{ item.name }}
                <button class="btn-img cncl" @click="$emit('close-dlg')"></button>
            </div>
            <div>
                <button class="btn blue" @click="addDlg = !addDlg">&#x2795;</button>
                <div v-if="addDlg">
                    <div style="display: flex;">
                        <select v-model="groupId">
                            <option selected disabled value="null">{{ language.data.gp1 }}</option>
                            <option v-for="grp in groups" :value="grp.id">
                                {{ grp.name }}
                            </option>
                        </select>
                        <select v-model="access">
                            <option selected disabled value="null">{{ language.data.amg1 }}</option>
                            <option value="NA">{{ language.data.amg2 }}</option>
                            <option value="PT">{{ language.data.amg3 }}</option>
                            <option value="RO">{{ language.data.amg4 }}</option>
                            <option value="RW">{{ language.data.amg5 }}</option>
                        </select>
                        <button class="btn-img acpt" :title="language.data.cm3" :disabled="groupId === null || access === null" @click="setAccess"></button>
                        <button class="btn-img cncl" :title="language.data.cm4" @click="cancel"></button>
                    </div>
                    <br>
                    <input type="checkbox" v-model="ptAbove"> {{ language.data.amg6 }}
                    <br>
                    <input type="checkbox" v-model="sameBelow"> {{ language.data.amg7 }}
                </div>
                <table class="table">
                    <thead></thead>
                    <tbody>
                        <tr v-if="assignedGroups.length === 0">
                            <td>{{ language.data.cm9 }}</td>
                        </tr>
                    <tr v-for="group in assignedGroups" :key="'access' + group.id">
                        <td>{{ group.name }}</td>
                        <td :class="paintAccess(group.access)">{{ printAccess(group.access) }}</td>
                        <td>
                            <button class="btn-img rmv" :title="language.data.cm5" @click="removeAccess(group.id, group.access, group.name)"></button>
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