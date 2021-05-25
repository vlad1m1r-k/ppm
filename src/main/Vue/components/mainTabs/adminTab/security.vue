<template>
    <table>
        <thead></thead>
        <tbody>
        <tr>
            <td>{{ language.data.sec2 }}</td>
            <td>
                <input type="number" class="form-control-sm" min="1" max="20" v-model="incorrectLoginAttempts" size="3"> 1 - 20
            </td>
        </tr>
        <tr>
            <td>{{ language.data.sec3 }}</td>
            <td>
                <input type="number" class="form-control-sm" min="1" max="20" v-model="ipBanTimeDays" size="3"> 0 - 20
                {{ language.data.sec4 }}
            </td>
        </tr>
        <tr>
            <td>{{ language.data.sec5 }}</td>
            <td>
                <input type="number" class="form-control-sm" min="1" max="20" v-model="incorrectPasswdAttempts" size="3"> 1 - 20
            </td>
        </tr>
        </tbody>
    </table>
    <button class="btn btn-sm btn-success" @click="saveSettings">{{ language.data.cm3 }}</button>
</template>

<script>
export default {
    name: "security",
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            incorrectLoginAttempts: null,
            ipBanTimeDays: null,
            incorrectPasswdAttempts: null
        }
    },
    methods: {
        async getSettings() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({token: token});
                const answer = await $.ajax({
                    url: "/settings/getSettings",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                this.incorrectLoginAttempts = data.incorrectLoginAttempts;
                this.ipBanTimeDays = data.ipBanTimeDays;
                this.incorrectPasswdAttempts = data.incorrectPasswdAttempts;
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async saveSettings() {
            //TODO
        }
    },
    beforeMount() {
        this.getSettings();
    }
}
</script>

<style scoped>

</style>