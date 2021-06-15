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
                        <td></td>
                    </tr>
                    <tr>
                        <td>{{ language.data.pg3 }}</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>{{ language.data.pg4 }}</td>
                        <td></td>
                    </tr>
                    </tbody>
                </table>
                <button class="btn btn-sm btn-success" @click="">{{ language.data.cm3 }}</button>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "pwdGenSettings",
    emits: ["close-dlg"],
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
        }
    },
    methods: {

    }
}
</script>

<style scoped>

</style>