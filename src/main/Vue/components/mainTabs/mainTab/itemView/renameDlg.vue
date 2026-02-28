<template>
    <div style="display: flex;">
        <input class="input-sm" v-model="name" ref="ivRenCnt" @keypress.enter="renameContainer" @keydown.esc="$emit('close-dlg')">
        <button class="btn-img acpt" :disabled="name.length === 0" @click="renameContainer"></button>
        <button class="btn-img cncl" @click="$emit('close-dlg')"></button>
    </div>
</template>

<script>
export default {
    name: "renameDlg",
    props: {
        item: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            name: ""
        }
    },
    methods: {
        async renameContainer() {
            this.eventHub.emit("show-msg", "");
            const confResult = confirm(this.language.data.iv4 + " \"" + this.item.name + "\" ?");
            if (confResult && this.item.name !== "root") {
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        item: this.item.id,
                        name: this.name
                    });
                    const answer = await $.ajax({
                        url: "/container/rename",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    } else {
                        this.$emit('close-dlg');
                        this.eventHub.emit("update-tree");
                    }
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        }
    },
    mounted() {
        this.$refs.ivRenCnt.focus();
        this.name = this.item.name;
    }
}
</script>

<style scoped>

</style>