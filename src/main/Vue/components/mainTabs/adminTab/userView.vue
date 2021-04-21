<template>
    <tr v-if="showEditDlg">
        <td colspan="10">
            <input type="text" class="form-control-sm align-middle" :placeholder="language.data.lf1" v-model="login">
            <input type="password" class="form-control-sm align-middle" min="1" :placeholder="language.data.lf2" v-model="pwd">
            <select class="form-control-sm align-middle" v-model="status" :class="{'bg-danger': status === 'DISABLED'}">
                <option class="bg-light" v-for="status in statuses" :value="status">{{ status }}</option>
            </select>
            <button class="btn btn-sm btn-outline-success" :disabled="login.length === 0" :title="language.data.cm3"
                    @click="save">&check;
            </button>
            <button class="btn btn-sm btn-danger" :title="language.data.cm4" @click="cancel">X</button>
        </td>
    </tr>
    <tr v-else>
        <td>{{ user.login }}</td>
        <td :class="{'text-danger': user.status === 'DISABLED'}">{{ user.status }}</td>
        <td>
            groups
        </td>
        <td><button class="btn btn-sm btn-outline-success" :title="language.data.cm2" @click="showEditDlg = true">&#x1f589;</button></td>
        <td>delete</td>
    </tr>
</template>

<script>
export default {
    name: "userView",
    props: {
        user: Object,
        statuses: Array
    },
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            showEditDlg: false,
            login: this.user.login,
            pwd: "",
            status: this.user.status
        }
    },
    methods: {
        async save() {
            //TODO emit save event
            //TODO check pwd changed
        },
        cancel() {
            this.login = this.user.login;
            this.pwd = "";
            this.status = this.user.status;
            this.showEditDlg = false;
        }
    }
}
</script>

<style scoped>

</style>