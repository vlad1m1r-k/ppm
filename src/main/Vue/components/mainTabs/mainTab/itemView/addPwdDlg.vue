<template>

</template>

<script>
export default {
    name: "addPwdDlg",
    props: {
        item: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            name: "",
            text: ""
        }
    },
    methods: {

    }
}
</script>

<style scoped>

</style>