<template>
    <tr v-if="showEditDlg">
        <td colspan="10">
            <input type="text" class="form-control-sm align-middle" :placeholder="language.data.gp2" v-model="name">
            &nbsp;{{ language.data.gp3 }}:
            <select class="form-control-sm align-middle" :class="{'bg-danger': adminSettings}" v-model="adminSettings"
                    :title="language.data.gp3">
                <option class="bg-light" :value=false>FALSE</option>
                <option class="bg-danger" :value=true>TRUE</option>
            </select>
            <button class="btn btn-sm btn-outline-success" :disabled="name.length === 0" :title="language.data.cm3"
                    @click="save">&check;
            </button>
            <button class="btn btn-sm btn-danger" :title="language.data.cm4" @click="cancel">X</button>
        </td>
    </tr>
    <tr v-else>
        <td>{{ group.name }}</td>
        <td :class="{'text-danger': group.adminSettings}">{{ group.adminSettings }}</td>
        <td>Users
<!--            TODO-->
        </td>
        <td><button class="btn btn-sm btn-outline-success" :title="language.data.cm2" @click="showEditDlg = true">&#x1f589;</button></td>
        <td><button class="btn btn-sm btn-outline-danger" :title="language.data.cm5" @click="deleteGroup">&#x1f5d1;</button></td>
    </tr>
</template>

<script>
export default {
    name: "groupView",
    props: {
        group: Object
    },
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            showEditDlg: false,
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