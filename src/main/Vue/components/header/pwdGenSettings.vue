<template>
    <div class="modal">
        <div class="modal-body dialog">
            <div class="modal-header">
                {{ language.data.pg1 }}
                <button class="btn-img cncl" @click="$emit('close-dlg')"></button>
            </div>
            <table>
                <thead></thead>
                <tbody>
                    <tr>
                        <td>{{ language.data.pg2 }}</td>
                        <td><input type="number" class="input" min="1" v-model="settings.pwdLength"></td>
                    </tr>
                    <tr>
                        <td>{{ language.data.pg3 }}</td>
                        <td><input type="checkbox" class="input" v-model="settings.numbers"></td>
                    </tr>
                    <tr>
                        <td>{{ language.data.pg4 }}</td>
                        <td><input type="checkbox" class="input" v-model="settings.symbols"></td>
                    </tr>
                </tbody>
            </table>
            <button class="btn blue" @click="save">{{ language.data.cm3 }}</button>
        </div>
    </div>
</template>

<script>
export default {
    name: "pwdGenSettings",
    props: {
        settings: Object
    },
    emits: ["close-dlg"],
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
        }
    },
    methods: {
        async save() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    pwdLength: this.settings.pwdLength,
                    numbers: this.settings.numbers,
                    symbols: this.settings.symbols
                });
                const answer = await $.ajax({
                    url: "/user/setPwdGenSettings",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                if (data.message) {
                    this.eventHub.emit("show-msg", this.language.data[data.message]);
                } else {
                    this.$emit("close-dlg");
                }
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        }
    }
}
</script>

<style scoped>

</style>