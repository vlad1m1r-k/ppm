<template>
    <div>
        <select v-model="selectedLang" @change="$emit('language-changed', selectedLang)">
            <option value="en-US">En</option>
            <option value="uk-UA">Ua</option>
        </select>
    </div>
</template>

<script>
export default {
    name: "languageSelector",
    props: {
        'lang-name': String
    },
    data() {
        return {
            selectedLang: ""
        }
    },
    watch: {
        langName(value) {
            this.selectedLang = value;
        }
    }
}
</script>

<style scoped>

</style>