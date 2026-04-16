<template>
    <div class="modal">
        <div class="modal-body dialog">
            <div class="modal-header">
                {{ user.login }}{{ language.data.gs1 }}
                <button class="btn-img cncl" @click="$emit('close-dlg')"></button>
            </div>
            <div>
                <table class="table">
                    <thead>
                    <tr>
                        <th></th>
                        <th><button class="btn link" @click="setSort('name')">{{ language.data.gp2 }}</button></th>
                        <th><button class="btn link" @click="setSort('adminSettings')">{{ language.data.gp3 }}</button></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="group in groups">
                        <td><input type="checkbox" :checked="isChecked(group.id)" @change="setGroup(group.id, $event.target.checked)"></td>
                        <td>{{ group.name }}</td>
                        <td :class="{'text-danger': group.adminSettings}">{{ group.adminSettings }}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>

<script>
import Sort from "../../../sort";

export default {
    name: "groupSelector",
    props: {
        user: Object
    },
    emits: ["close-dlg"],
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            groups: [],
            sort: new Sort("name", "asc")
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
        isChecked(id) {
            return this.user.groups.find(grp => id === grp.id);
        },
        async setGroup(groupId, checked) {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    groupId: groupId,
                    userId: this.user.id,
                    member: checked
                });
                $.ajax({
                    url: "/group/editGroupMembers",
                    method: "POST",
                    data: encryptedData
                });
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

</style>