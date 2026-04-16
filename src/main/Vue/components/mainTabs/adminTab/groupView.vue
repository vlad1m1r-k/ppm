<template>
    <user-selector v-if="showUserSelector" :group="group" @close-dlg="showUserSelector = false; $emit('group-changed')"></user-selector>
    <access-tree v-if="showAccessTree" :group="group" @close-dlg="showAccessTree = false"></access-tree>
    <tr v-if="showEditDlg">
        <td class="flex-view" colspan="10">
            <input type="text" :placeholder="language.data.gp2" v-model="name">
            &nbsp;{{ language.data.gp3 }}:
            <select v-model="adminSettings" :title="language.data.gp3">
                <option :value=false>FALSE</option>
                <option :value=true>TRUE</option>
            </select>
            <button class="btn-img acpt" :disabled="name.length === 0" :title="language.data.cm3" @click="save"></button>
            <button class="btn-img cncl" :title="language.data.cm4" @click="cancel"></button>
        </td>
    </tr>
    <tr v-else>
        <td>{{ group.name }}</td>
        <td :class="{'text-danger': group.adminSettings}">{{ group.adminSettings }}</td>
        <td>
            <button class="btn blue" @click="showUserSelector = true">{{ language.data.us1 }} ({{ group.users.length }})</button>
        </td>
        <td>
            <button class="btn blue" @click="showAccessTree = true">{{ language.data.gp4 }}</button>
        </td>
        <td class="fit"><button class="btn-img edit" :title="language.data.cm2" @click="showEditDlg = true"></button></td>
        <td class="fit"><button class="btn-img rmv" :title="language.data.cm5" @click="deleteGroup"></button></td>
    </tr>
</template>

<script>
import userSelector from "./userSelector.vue";
import accessTree from "./accessTree.vue";

export default {
    name: "groupView",
    props: {
        group: Object
    },
    emits: ["group-changed"],
    components: {userSelector, accessTree},
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            showEditDlg: false,
            showUserSelector: false,
            showAccessTree: false,
            name: this.group.name,
            adminSettings: this.group.adminSettings
        }
    },
    methods: {
        async save() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    id: this.group.id,
                    name: this.name,
                    admin: this.adminSettings
                });
                const answer = await $.ajax({
                    url: "/group/editGroup",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                if (data.message) {
                    this.eventHub.emit("show-msg", this.language.data[data.message]);
                } else {
                    this.showEditDlg = false;
                    this.$emit("group-changed");
                }
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        cancel() {
            this.name = this.group.name;
            this.adminSettings = this.group.adminSettings;
            this.showEditDlg = false;
        },
        async deleteGroup() {
            if (confirm(this.language.data.cm5 + " " + this.group.name + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        id: this.group.id,
                    });
                    await $.ajax({
                        url: "/group/deleteGroup",
                        method: "POST",
                        data: encryptedData
                    });
                    this.$emit("group-changed");
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