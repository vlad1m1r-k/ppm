<template>
    <div>
        <input class="form-control-sm" v-model="name">
        <button class="btn btn-sm btn-outline-success" :disabled="name.length === 0">&check;</button>
        <button class="btn btn-sm btn-outline-danger" @click="$emit('close-dlg')">&Chi;</button>
    </div>
</template>

<script>
export default {
    name: "addDlg",
    props: {
        item: Object
    },
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            name: ""
        }
    },
    methods: {
        async addContainer() {
            try {
                //TODO
            } catch (e) {
                this.$emit("msg-evt", e);
            }
        }
    }
}
</script>

<style scoped>

</style>