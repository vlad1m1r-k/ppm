<template>
    <div class="l-modal">
        <div class="l-modal-body">
            <div class="row">
                <div class="col">
                    {{ user.login }}{{language.data.gs1}}
                    <button class="close" @click="$emit('close-dlg')">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </div>
            <div class="scroll">
                <table class="table table-bordered table-striped table-sm mt-3 rounded">
                    <thead class="tab-header-area">
                    <tr>
                        <th></th>
                        <th><button class="btn btn-sm btn-link" @click="setSort('name')">{{ language.data.gp2 }}</button></th>
                        <th><button class="btn btn-sm btn-link" @click="setSort('adminSettings')">{{ language.data.gp3 }}</button></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="group in groups">
                        <td><input type="checkbox" :checked="isChecked(group.id)" @change="setGroup(group.id, $event.target.checked)"></td>
                        <td>{{ group.name }}</td>
                        <td :class="{'bg-danger': group.adminSettings}">{{ group.adminSettings }}</td>
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
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            groups: [],
            sort: new Sort("name", "asc"),
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
        async setGroup(id, checked) {
            console.log(id + " is " + checked);
            //TODO
        }
    },
    beforeMount() {
        this.getGroups();
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