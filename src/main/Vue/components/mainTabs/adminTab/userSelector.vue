<template>
    <div class="modal">
        <div class="modal-body dialog">
            <div class="modal-header">
                {{ group.name }}
                <button class="btn-img cncl" @click="$emit('close-dlg')"></button>
            </div>
            <div>
                <table class="table">
                    <thead>
                    <tr>
                        <th></th>
                        <th><button class="btn link" @click="setSort('login')">{{ language.data.lf1 }}</button></th>
                        <th><button class="btn link" @click="setSort('status')">{{ language.data.us2 }}</button></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="user in users">
                        <td><input type="checkbox" :checked="isChecked(user.id)" @change="setUser(user.id, $event.target.checked)"></td>
                        <td>{{ user.login }}</td>
                        <td :class="{'text-danger': user.status === 'DISABLED'}">{{ user.status }}</td>
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
    name: "userSelector",
    props: {
        group: Object
    },
    emits: ["close-dlg"],
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            users: [],
            sort: new Sort("login", "asc")
        }
    },
    methods: {
        setSort(field) {
            this.sort.setField(field);
            this.getUsers();
        },
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
        isChecked(id) {
            return this.group.users.find(usr => id === usr.id);
        },
        async setUser(userId, checked) {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    groupId: this.group.id,
                    userId: userId,
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
        this.getUsers();
    }
}
</script>

<style scoped>

</style>