<template>
    <div>
        <div class="row">
            <div class="col">
                <button class="btn btn-sm btn-outline-success" :disabled="name.length === 0" @click="addNote">&check;
                </button>
                <button class="btn btn-sm btn-outline-danger" @click="$emit('close-dlg')">&Chi;</button>
            </div>
        </div>
        <div class="row">
            <div class="col">
                <input class="form-control" v-model="name" :placeholder="language.data.iv6" ref="ivAddNote" @keydown.esc="$emit('close-dlg')">
            </div>
        </div>
        <div class="row">
            <div class="col">
                <textarea class="form-control" rows="4" v-model="text"></textarea>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "addNoteDlg",
    props: {
        item: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            name: "",
            text: ""
        }
    },
    methods: {
        async addNote() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    parent: this.item.id,
                    name: this.name,
                    text: this.text
                });
                const answer = await $.ajax({
                    url: "/container/addNote",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                if (data.message) {
                    this.eventHub.emit("show-msg", this.language.data[data.message]);
                } else {
                    this.$emit('close-dlg');
                }
                this.eventHub.emit("update-tree");
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        }
    },
    mounted() {
        this.$refs.ivAddNote.focus();
    }
}
</script>

<style scoped>

</style>