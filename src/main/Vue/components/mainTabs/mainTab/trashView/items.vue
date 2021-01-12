<template>
    <div>
        {{ language.data.iv7 }} <br>
        <note-view v-for="note in items.notes" :note="note" @update-items="getItems" :key="'DN' + note.id"></note-view>
        {{ language.data.iv8 }} <br>
        <pwd-view v-for="pwd in items.passwords" :pwd="pwd" @update-items="getItems" :key="'DP' + pwd.id"></pwd-view>
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
            items: {}
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

</style>