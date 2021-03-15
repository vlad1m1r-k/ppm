<template>
    <table class="table table-bordered table-striped table-sm">
        <thead class="tab-header-area">
        <tr>
            <th>
                <button class="btn btn-sm btn-link" @click="setSort('name')">{{ language.data.div1 }}</button>
            </th>
            <th>{{ language.data.iv7 }}</th>
            <th>{{ language.data.iv8 }}</th>
            <th><button class="btn btn-sm btn-link" @click="setSort('createdDate')">{{ language.data.div2 }}</button></th>
            <th><button class="btn btn-sm btn-link" @click="setSort('createdBy')">{{ language.data.div3 }}</button></th>
            <th><button class="btn btn-sm btn-link" @click="setSort('editedDate')">{{ language.data.div4 }}</button></th>
            <th><button class="btn btn-sm btn-link" @click="setSort('editedBy')">{{ language.data.div5 }}</button></th>
            <th><button class="btn btn-sm btn-link" @click="setSort('deletedDate')">{{ language.data.div6 }}</button></th>
            <th><button class="btn btn-sm btn-link" @click="setSort('deletedBy')">{{ language.data.div7 }}</button></th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <tbody>
            <tr v-for="cont in containers">
                <td>{{ cont.name }}</td>
                <td>
                    <select class="form-control-sm">
                        <option selected>{{ language.data.iv7 }} {{ cont.notes.length }}</option>
                        <option v-for="note in cont.notes" disabled>{{ note.name }}</option>
                    </select>
                </td>
                <td>
                    <select class="form-control-sm">
                        <option selected>{{ language.data.iv8 }} {{ cont.passwords.length }}</option>
                        <option v-for="pwd in cont.passwords" disabled>{{ pwd.name }}</option>
                    </select>
                </td>
                <td>{{ cont.createdDate }}</td>
                <td>{{ cont.createdBy }}</td>
                <td>{{ cont.editedDate }}</td>
                <td>{{ cont.editedBy }}</td>
                <td>{{ cont.deletedDate }}</td>
                <td>{{ cont.deletedBy }}</td>
                <td>restore</td>
                <td>delete</td>
            </tr>
        </tbody>
    </table>
</template>

<script>
import Sort from "../../../../sort";

export default {
    name: "containers",
    props: {
        item: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            containers: [],
            sort: new Sort("name", "asc"),
        }
    },
    methods: {
        async getContainers() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    sort: this.sort.toString()
                });
                const answer = await $.ajax({
                    url: "/container/getDeletedContainers",
                    method: "POST",
                    data: encryptedData
                });
                this.containers = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        setSort(field) {
            this.sort.setField(field);
            this.getContainers();
        }
        //TODO restore containers
        //TODO remove containers
    },
    mounted() {
        this.getContainers();
    }
}
</script>

<style scoped>

</style>