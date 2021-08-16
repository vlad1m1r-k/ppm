<template>
    <div class="modal-dlg">
        <div class="modal-dlg-body">
            <div class="row">
                <div class="col">
                    {{ group.name }}
                    <button class="close" @click="$emit('close-dlg')">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </div>
            <div class="modal-dlg-scroll mt-3 p-1">
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