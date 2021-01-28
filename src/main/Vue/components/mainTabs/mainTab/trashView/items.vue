<template>
    <div>
        <div class="decor" @click="showNotes = !showNotes">{{ items.notes.length }} {{ language.data.iv7 }}</div>
        <div v-show="showNotes">
            <table class="table table-bordered table-striped table-sm">
                <thead class="tab-header-area">
                <tr>
                    <th><input type="checkbox" @change="checkToggle($event.target.checked, 'notes')"></th>
                    <!--                    TODO move to lang-->
                    <th></th>
                    <th>
                        <button class="btn btn-sm btn-link" @click="setNotesSort('name')">name</button>
                    </th>
                    <th>created</th>
                    <th>created by</th>
                    <th>edited</th>
                    <th>edited by</th>
                    <th>deleted</th>
                    <th>deleted by</th>
                    <th></th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <note-view v-for="note in items.notes" :note="note" @update-items="getItems"
                           :key="'DN' + note.id"></note-view>
                </tbody>
            </table>
        </div>
        <div class="decor" @click="showPass = !showPass">{{ items.passwords.length }} {{ language.data.iv8 }}</div>
        <div v-show="showPass">
            <pwd-view v-for="pwd in items.passwords" :pwd="pwd" @update-items="getItems"
                      :key="'DP' + pwd.id"></pwd-view>
        </div>
    </div>
</template>

<script>
import noteView from "./noteView.vue";
import pwdView from "./pwdView.vue";
import Sort from "../../../../sort";

export default {
    name: "items",
    components: {
        noteView, pwdView
    },
    props: {
        item: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            items: {notes: [], passwords: []},
            showNotes: true,
            showPass: true,
            sortNotes: new Sort("name", "desc"),
            sortPwd: new Sort("name", "desc"),
            checkedNotes: []
        }
    },
    watch: {
        'item.id'() {
            this.getItems();
        }
    },
    methods: {
        async getItems() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    item: this.item.id,
                    sortNotes: this.sortNotes.toString(),
                    sortPwd: this.sortPwd.toString()
                });
                const answer = await $.ajax({
                    url: "/container/getDeletedItems",
                    method: "POST",
                    data: encryptedData
                });
                this.items = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        checkToggle(checked, item) {
            if (item === "notes") {
                this.checkedNotes = [];
                if (checked) {
                    this.items.notes.forEach(note => {
                        this.checkedNotes.push(note.id);
                    })
                }
            } else {
                //TODO passwords
            }
        },
        setNotesSort(field) {
            this.sortNotes.setField(field);
            this.getItems();
        },
        setPwdSort(field) {
            //TODO
        }
    },
    mounted() {
        this.getItems();
    }
}
</script>

<style scoped>
.decor {
    white-space: nowrap;
    border: 2px solid #ffab03;
    border-radius: 10px;
    padding-left: 5px;
    cursor: pointer;
}
</style>