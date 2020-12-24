<template>
    <div>
        Items {{item.id}}
    </div>
</template>

<script>
export default {
    name: "items",
    props: {
        item: Object
    },
    data() {
        return {
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
                this.$eventHub.$emit("update-tree");
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