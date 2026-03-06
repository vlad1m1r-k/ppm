<template>
    <div class="modal">
        <div class="modal-body dialog">
            <div class="modal-header">
                {{ group.name }}
                <button class="btn-img cncl" @click="$emit('close-dlg')"></button>
            </div>
            <div>
                <access-item :item="tree" :group="group" @update-tree="getAccessTree"></access-item>
            </div>
        </div>
    </div>
</template>

<script>
import accessItem from "./accessTreeItem.vue";

export default {
    name: "accessTree",
    emits: ["close-dlg"],
    props: {
        group: Object
    },
    components: {accessItem},
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            tree: {}
        }
    },
    methods: {
        async getAccessTree() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    groupId: this.group.id
                });
                const answer = await $.ajax({
                    url: "/container/getAccessTree",
                    method: "POST",
                    data: encryptedData
                });
                this.tree = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        }
    },
    beforeMount() {
        this.getAccessTree();
    }
}
</script>

<style scoped>

</style>