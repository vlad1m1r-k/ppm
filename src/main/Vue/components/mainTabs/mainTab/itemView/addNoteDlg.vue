<template>
    <button class="btn-img acpt" :disabled="name.length === 0" @click="addNote" :title="language.data.cm3"></button>
    <button class="btn-img cncl" @click="$emit('close-dlg')" :title="language.data.cm4"></button>
    <input class="iv-input" v-model="name" :placeholder="language.data.iv6" ref="ivAddNote" @keydown.esc="$emit('close-dlg')">
    <textarea class="text-box" rows="4" v-model="text"></textarea>
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