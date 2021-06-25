<template>
    <pwd-gen-settings v-if="showSettings" @close-dlg="showSettings = false" :settings="settings"></pwd-gen-settings>
    <span class="ml-2">
        <button class="btn btn-sm btn-outline-secondary" :title="language.data.um1" @click="showSettings = true">&#x2699;</button>
        <input type="text" class="form-control-sm align-middle" readonly size="20" v-model="passwd">
        <button class="btn btn-sm btn-outline-secondary" :title="language.data.db6" @click="generate">G</button>
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
            showSettings: false,
            passwd: ""
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
        },
        generate() {
            this.passwd = "";
            let symbols = Array(26).fill(0).map((e, i) => String.fromCharCode(i + 65));
            symbols = symbols.concat(Array(26).fill(0).map((e, i) => String.fromCharCode(i + 97)));
            if (this.settings.numbers) {
                symbols = symbols.concat("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
            }
            if (this.settings.symbols) {
                symbols = symbols.concat("~", "`", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "-", "+", "=", "\\", "|",
                    "[", "]", "{", "}", ":", ";", "'", "\"", "<", ">", ",", ".", "/", "?");
            }
            this.shuffle(symbols, 3);
            for (let i = 0; i < this.settings.pwdLength; i++) {
                this.passwd += symbols[Math.floor(Math.random() * symbols.length)];
            }
        },
        shuffle(arr, count) {
            let j, tmp;
            for (let i = 0; i < count; i++) {
                for (let k = arr.length -1; k > 0; k--) {
                    j = Math.floor(Math.random() * (k + 1));
                    tmp = arr[j];
                    arr[j] = arr[k];
                    arr[k] = tmp;
                }
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