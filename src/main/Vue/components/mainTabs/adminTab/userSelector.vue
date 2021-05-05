<template>
    <div class="l-modal">
        <div class="l-modal-body">
            <div class="row">
                <div class="col">
                    {{ group.name }}
                    <button class="close" @click="$emit('close-dlg')">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </div>
            <div class="scroll">
                <table class="table table-bordered table-striped table-sm mt-3">
                    <thead class="tab-header-area">
                    <tr>
                        <th></th>
                        <th><button class="btn btn-sm btn-link" @click="setSort('login')">{{ language.data.lf1 }}</button></th>
                        <th><button class="btn btn-sm btn-link" @click="setSort('status')">{{ language.data.us2 }}</button></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="user in users">
                        <td><input type="checkbox" :checked="isChecked(user.id)" @change="setUser(user.id, $event.target.checked)"></td>
                        <td>{{ user.login }}</td>
                        <td :class="{'bg-danger': user.status === 'DISABLED'}">{{ user.status }}</td>
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
.l-modal {
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    position: fixed;
    background: rgba(0, 0, 0, 0.69);
    z-index: 1050;
    transition: all 0.4s ease-in-out;
    -moz-transition: all 0.4s ease-in-out;
    -webkit-transition: all 0.4s ease-in-out;
    margin: 0;
    padding: 0;
    pointer-events: auto;
}

.l-modal-body {
    padding: 15px;
    width: 600px;
    margin: 30px auto;
    border-radius: 5px;
    background: #fff;
    box-shadow: 0 3px 7px rgba(0, 0, 0, .25);
    -moz-box-shadow: 0 3px 7px rgba(0, 0, 0, .25);
    -webkit-box-shadow: 0 3px 7px rgba(0, 0, 0, .25);
    box-sizing: border-box;
    -moz-box-sizing: border-box;
    -webkit-box-sizing: border-box;
    max-height: calc(100vh - 40px);
    overflow-y: hidden;
}
.scroll {
    overflow: auto;
    max-height: calc(100vh - 70px);
}
</style>