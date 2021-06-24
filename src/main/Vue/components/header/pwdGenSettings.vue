<template>
    <div class="modal-dlg">
        <div class="modal-dlg-body">
            <div class="row">
                <div class="col">
                    {{ language.data.pg1 }}
                    <button class="close" @click="$emit('close-dlg')">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </div>
            <div class="modal-dlg-scroll mt-3 p-1">
                <table>
                    <thead></thead>
                    <tbody>
                    <tr>
                        <td>{{ language.data.pg2 }}</td>
                        <td><input type="number" class="form-control-sm" min="1" v-model="settings.pwdLength"></td>
                    </tr>
                    <tr>
                        <td>{{ language.data.pg3 }}</td>
                        <td><input type="checkbox" class="form-control-sm" v-model="settings.numbers"></td>
                    </tr>
                    <tr>
                        <td>{{ language.data.pg4 }}</td>
                        <td><input type="checkbox" class="form-control-sm" v-model="settings.symbols"></td>
                    </tr>
                    </tbody>
                </table>
                <button class="btn btn-sm btn-success" @click="save">{{ language.data.cm3 }}</button>
            </div>
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