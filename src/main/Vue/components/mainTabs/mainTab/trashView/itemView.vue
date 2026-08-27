<template>
   <li>
        <span class="di-cont" @click="showContent = !showContent">{{ item.name }}</span>
        <ul v-if="showContent">
            <li>
                {{ language.data.iv7 }} {{ item.notes.length }}
                <table class="table">
                <thead>
                <tr>
                    <th class="fit">
                        <input type="checkbox" @change="checkToggle($event.target.checked, 'notes')">
                        <button class="btn-img redo" :title="language.data.cm7" @click.stop="restoreNotes"></button>
                        <button class="btn-img rmv" :title="language.data.cm5" @click.stop="removeNotes"></button>
                    </th>
                    <th>{{ language.data.div1 }}</th>
                    <th>{{ language.data.div2 }}</th>
                    <th>{{ language.data.div3 }}</th>
                    <th>{{ language.data.div4 }}</th>
                    <th>{{ language.data.div5 }}</th>
                    <th>{{ language.data.div6 }}</th>
                    <th>{{ language.data.div7 }}</th>
                    <th></th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <note-view v-for="note in item.notes" :note="note" @update-items="$emit('update-items')" :key="'DN' + note.id"></note-view>
                </tbody>
            </table>
            </li>
            <li>
                {{ language.data.iv8 }} {{ item.passwords.length }}
                <table class="table">
                <thead>
                <tr>
                    <th class="fit">
                        <input type="checkbox" @change="checkToggle($event.target.checked, 'pass')">
                        <button class="btn-img redo" :title="language.data.cm7" @click.stop="restorePass"></button>
                        <button class="btn-img rmv" :title="language.data.cm5" @click.stop="removePass"></button>
                    </th>
                    <th>{{ language.data.div1 }}</th>
                    <th>{{ language.data.div2 }}</th>
                    <th>{{ language.data.div3 }}</th>
                    <th>{{ language.data.div4 }}</th>
                    <th>{{ language.data.div5 }}</th>
                    <th>{{ language.data.div6 }}</th>
                    <th>{{ language.data.div7 }}</th>
                    <th></th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                    <pwd-view v-for="pwd in item.passwords" :pwd="pwd" @update-items="$emit('update-items')" :key="'DP' + pwd.id"></pwd-view>
                </tbody>
            </table>
            </li>
            <li>
                {{ language.data.fl1 }} {{ item.files.length }}
                <table class="table">
                <thead>
                <tr>
                    <th>
                        <input type="checkbox" @change="checkToggle($event.target.checked, 'files')">
                        <button class="btn-img redo" :title="language.data.cm7" @click.stop="restoreFiles"></button>
                        <button class="btn-img rmv" :title="language.data.cm5" @click.stop="removeFiles"></button>
                    </th>
                    <th>{{ language.data.div1 }}</th>
                    <th>{{ language.data.div8 }}</th>
                    <th>{{ language.data.div2 }}</th>
                    <th>{{ language.data.div3 }}</th>
                    <th>{{ language.data.div4 }}</th>
                    <th>{{ language.data.div5 }}</th>
                    <th>{{ language.data.div6 }}</th>
                    <th>{{ language.data.div7 }}</th>
                    <th></th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                    <fls-view v-for="file in item.files" :file="file" @update-items="$emit('update-items')" :key="'DF' + file.id"></fls-view>
                </tbody>
            </table>
            </li>
        </ul>
    </li>
</template>

<script>
import noteView from "./noteView.vue";
import pwdView from "./pwdView.vue";
import flsView from "./flsView.vue";

export default {
    name: "itemView",
    components: {noteView, pwdView, flsView},
    props: {item: Object},
    emits: ['update-items'],
    data() {
        return {
            language: this.$root.$data.language,
            showContent: true,
            checkedNotes: [],
            checkedPass: [],
            checkedFls: []
        }
    },
    methods: {
        checkToggle(checked, item) {
            if (item === "notes") {
                this.checkedNotes = [];
                if (checked) {
                    this.item.notes.forEach(note => {
                        this.checkedNotes.push(note.id);
                    })
                }
            }
            if (item === "files") {
                this.checkedFls = [];
                if (checked) {
                    this.item.files.forEach(file => {
                        this.checkedFls.push(file.id);
                    })
                }
            }
            if (item === "pass") {
                this.checkedPass = [];
                if (checked) {
                    this.item.passwords.forEach(pwd => {
                        this.checkedPass.push(pwd.id);
                    })
                }
            }
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
    }
}
</script>

<style scoped>

</style>