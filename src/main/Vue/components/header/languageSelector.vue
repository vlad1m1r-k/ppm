<template>
    <div>
        <select class="form-control-sm" v-model="selectedLang" @change="setLang">
            <option value="en-US">En</option>
            <option value="uk-UA">Ua</option>
        </select>
    </div>
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
    }
}
</script>

<style scoped>

</style>