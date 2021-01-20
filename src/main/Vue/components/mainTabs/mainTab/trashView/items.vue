<template>
    <div>
        <div class="decor" @click="showNotes = !showNotes">{{ items.notes.length }} {{ language.data.iv7 }}</div>
        <div v-show="showNotes">
            <note-view v-for="note in items.notes" :note="note" @update-items="getItems" :key="'DN' + note.id"></note-view>
        </div>
        <div class="decor" @click="showPass = !showPass">{{ items.passwords.length }} {{ language.data.iv8 }}</div>
        <div v-show="showPass">
            <pwd-view v-for="pwd in items.passwords" :pwd="pwd" @update-items="getItems" :key="'DP' + pwd.id"></pwd-view>
        </div>
    </div>
</template>

<script>
import noteView from "./noteView.vue";
import pwdView from "./pwdView.vue";

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
            showPass: true
        }
    },
    watch: {
        'item.id'() {
            this.getItems();
        }
    },
    methods: {
        async getItems() {
            this.$eventHub.$emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await Vue.cryptoProvider.encrypt({
                    token: token,
                    item: this.item.id
                });
                const answer = await $.ajax({
                    url: "/container/getDeletedItems",
                    method: "POST",
                    data: encryptedData
                });
                this.items = Vue.cryptoProvider.decrypt(answer);
            } catch (e) {
                this.$eventHub.$emit("show-msg", Vue.errorParser(e));
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