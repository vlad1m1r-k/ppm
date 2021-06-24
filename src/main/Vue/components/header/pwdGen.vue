<template>
    <pwd-gen-settings v-if="showSettings" @close-dlg="showSettings = false" :settings="settings"></pwd-gen-settings>
    <span class="ml-2">
        <button class="btn btn-sm btn-outline-secondary" :title="language.data.um1" @click="showSettings = true">&#x2699;</button>
        <input type="text" class="form-control-sm align-middle" readonly size="20">
        <button class="btn btn-sm btn-outline-secondary" :title="language.data.db6">G</button>
    </span>
</template>

<script>
import pwdGenSettings from "./pwdGenSettings.vue";

export default {
    name: "pwdGen",
    components: {
        pwdGenSettings
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            settings: null,
            showSettings: false
        }
    },
    methods: {
        async getSettings() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({token: token});
                const answer = await $.ajax({
                    url: "/user/getPwdGenSettings",
                    method: "POST",
                    data: encryptedData
                });
                this.settings = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        }
    },
    beforeMount() {
        this.getSettings();
    }
}
</script>

<style scoped>

</style>