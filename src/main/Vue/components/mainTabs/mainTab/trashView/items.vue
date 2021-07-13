<template>
    <div>
        <div class="decor" @click="showNotes = !showNotes">
            {{ items.notes.length }} {{ language.data.iv7 }}
            <span class="btn-dc text-success" :title="language.data.cm7" @click.stop="restoreNotes">&#x21ba;</span>
            <span class="btn-dc" :title="language.data.cm5" @click.stop="removeNotes">&#x1f5d1;</span>
        </div>
        <div v-show="showNotes">
            <table class="table table-bordered table-striped table-sm">
                <thead class="tab-header-area">
                <tr>
                    <th><input type="checkbox" @change="checkToggle($event.target.checked, 'notes')"></th>
                    <th></th>
                    <th>
                        <button class="btn btn-sm btn-link" @click="setNotesSort('name')">{{ language.data.div1 }}</button>
                    </th>
                    <th><button class="btn btn-sm btn-link" @click="setNotesSort('createdDate')">{{ language.data.div2 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setNotesSort('createdBy')">{{ language.data.div3 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setNotesSort('editedDate')">{{ language.data.div4 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setNotesSort('editedBy')">{{ language.data.div5 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setNotesSort('deletedDate')">{{ language.data.div6 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setNotesSort('deletedBy')">{{ language.data.div7 }}</button></th>
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
        <div class="decor" @click="showPass = !showPass">
            {{ items.passwords.length }} {{ language.data.iv8 }}
            <span class="btn-dc text-success" :title="language.data.cm7" @click.stop="restorePass">&#x21ba;</span>
            <span class="btn-dc" :title="language.data.cm5" @click.stop="removePass">&#x1f5d1;</span>
        </div>
        <div v-show="showPass">
            <table class="table table-bordered table-striped table-sm">
                <thead class="tab-header-area">
                <tr>
                    <th><input type="checkbox" @change="checkToggle($event.target.checked, 'pass')"></th>
                    <th></th>
                    <th>
                        <button class="btn btn-sm btn-link" @click="setPwdSort('name')">{{ language.data.div1 }}</button>
                    </th>
                    <th><button class="btn btn-sm btn-link" @click="setPwdSort('createdDate')">{{ language.data.div2 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setPwdSort('createdBy')">{{ language.data.div3 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setPwdSort('editedDate')">{{ language.data.div4 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setPwdSort('editedBy')">{{ language.data.div5 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setPwdSort('deletedDate')">{{ language.data.div6 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setPwdSort('deletedBy')">{{ language.data.div7 }}</button></th>
                    <th></th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                    <pwd-view v-for="pwd in items.passwords" :pwd="pwd" @update-items="getItems"
                          :key="'DP' + pwd.id"></pwd-view>
                </tbody>
            </table>
        </div>
        <div class="decor" @click="showFls = !showFls">
            {{ items.files.length }} {{ language.data.fl1 }}
            <span class="btn-dc text-success" :title="language.data.cm7" @click.stop="restoreFiles">&#x21ba;</span>
            <span class="btn-dc" :title="language.data.cm5" @click.stop="removeFiles">&#x1f5d1;</span>
        </div>
        <div v-show="showFls">
            <table class="table table-bordered table-striped table-sm">
                <thead class="tab-header-area">
                <tr>
                    <th><input type="checkbox" @change="checkToggle($event.target.checked, 'files')"></th>
                    <th>
                        <button class="btn btn-sm btn-link" @click="setFlsSort('name')">{{ language.data.div1 }}</button>
                    </th>
                    <th><button class="btn btn-sm btn-link" @click="setFlsSort('size')">{{ language.data.div8 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setFlsSort('createdDate')">{{ language.data.div2 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setFlsSort('createdBy')">{{ language.data.div3 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setFlsSort('editedDate')">{{ language.data.div4 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setFlsSort('editedBy')">{{ language.data.div5 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setFlsSort('deletedDate')">{{ language.data.div6 }}</button></th>
                    <th><button class="btn btn-sm btn-link" @click="setFlsSort('deletedBy')">{{ language.data.div7 }}</button></th>
                    <th></th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                    <fls-view v-for="file in items.files" :file="file" @update-items="getItems" :key="'DF' + file.id"></fls-view>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script>
import noteView from "./noteView.vue";
import pwdView from "./pwdView.vue";
import flsView from "./flsView.vue";
import Sort from "../../../../sort";

export default {
    name: "items",
    components: {
        noteView, pwdView, flsView
    },
    props: {
        item: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            items: {notes: [], passwords: [], files: []},
            showNotes: true,
            showPass: true,
            showFls: true,
            sortNotes: new Sort("name", "desc"),
            sortPwd: new Sort("name", "desc"),
            sortFls: new Sort("name", "desc"),
            checkedNotes: [],
            checkedPass: [],
            checkedFls: []
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
                    sortPwd: this.sortPwd.toString(),
                    sortFls: this.sortFls.toString()
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
            }
            if (item === "files") {
                this.checkedFls = [];
                if (checked) {
                    this.items.files.forEach(file => {
                        this.checkedFls.push(file.id);
                    })
                }
            } else {
                this.checkedPass = [];
                if (checked) {
                    this.items.passwords.forEach(pwd => {
                        this.checkedPass.push(pwd.id);
                    })
                }
            }
        },
        setNotesSort(field) {
            this.sortNotes.setField(field);
            this.getItems();
        },
        setPwdSort(field) {
            this.sortPwd.setField(field);
            this.getItems();
        },
        setFlsSort(field) {
            this.sortFls.setField(field);
            this.getItems();
        },
        removeNotes() {
            if (this.checkedNotes.length && confirm(this.language.data.di7)) {
                this.eventHub.emit("remove-notes", this.checkedNotes);
            }
        },
        restoreNotes() {
            if (this.checkedNotes.length && confirm(this.language.data.di6)) {
                this.eventHub.emit("restore-notes", this.checkedNotes);
            }
        },
        removePass() {
            if (this.checkedPass.length && confirm(this.language.data.di9)) {
                this.eventHub.emit("remove-passwords", this.checkedPass);
            }
        },
        restorePass() {
            if (this.checkedPass.length && confirm(this.language.data.di8)) {
                this.eventHub.emit("restore-passwords", this.checkedPass);
            }
        },
        removeFiles() {
            if (this.checkedFls.length && confirm(this.language.data.di11)) {
                this.eventHub.emit("remove-files", this.checkedFls);
            }
        },
        restoreFiles() {
            if (this.checkedFls.length && confirm(this.language.data.di10)) {
                this.eventHub.emit("restore-files", this.checkedFls);
            }
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