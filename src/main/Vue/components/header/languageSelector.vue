<template>
    <select v-model="selectedLang" @change="setLang">
        <option value="en-US">En</option>
        <option value="uk-UA">Ua</option>
    </select>
</template>

<script>
export default {
    name: "languageSelector",
    data() {
        return {
            language: this.$root.$data.language,
            selectedLang: ""
        }
    },
    methods: {
        setLang() {
            document.cookie = "lang=" + this.selectedLang + "; expires=Fri, 31 Dec 9999 23:59:59 GMT; samesite=strict";
            this.$root.loadLanguage(this.selectedLang);
        }
    },
    watch: {
        'language.name'(value) {
            this.selectedLang = value;
        }
    },
    mounted() {
        this.selectedLang = this.language.name;
    }
}
</script>

<style scoped>

</style>