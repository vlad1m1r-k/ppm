<template>
    <div :class="{'blur': tokenProvider.token === null}">
        Page Content
        <br>
        <language-selector :lang-name="lang.name" @language-changed="setLang"></language-selector>
    </div>
</template>

<script>
import languageSelector from "./languageSelector.vue";

export default {
    name: "mainBlock",
    components: {
        languageSelector
    },
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            lang: this.$root.$data.language
        }
    },
    methods: {
        setLang(langName) {
            document.cookie = "lang=" + langName + "; expires=Fri, 31 Dec 9999 23:59:59 GMT; samesite=strict";
            this.$root.loadLanguage(langName);
        }
    }
}
</script>

<style scoped>
    .blur {
        filter: blur(5px);
    }
</style>