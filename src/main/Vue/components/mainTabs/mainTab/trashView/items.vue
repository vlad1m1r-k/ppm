<template>
    <div>
        <div v-if="items.length === 0">
            <br>
            <h2>{{ language.data.cm9 }}</h2>
        </div>
        <ul class="item-tree">
            <item-view v-for="cont in items" :item="cont" @update-items="getItems"></item-view>
        </ul>
    </div>
</template>

<script>
import itemView from './itemView.vue';

export default {
    name: "items",
    components: {
        itemView
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            items: []
        }
    },
    methods: {
        async getItems() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token
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
        }
    },
    mounted() {
        this.getItems();
    }
}
</script>

<style scoped>

</style>