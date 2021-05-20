<template>
    <div class="l-modal">
        <div class="l-modal-body">
            <div class="row">
                <div class="col">
                    {{ group.name }}
                    <button class="close" @click="$emit('close-dlg')">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </div>
            <div class="scroll mt-3 p-1">
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
    width: 800px;
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
    max-height: calc(100vh - 110px);
    background: #f2f2f2;
}
</style>