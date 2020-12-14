<template>
    <div>
        <div class="row">
            <div class="col">
                <button class="btn btn-sm btn-outline-success" :disabled="name.length === 0" @click="addNote">&check;</button>
                <button class="btn btn-sm btn-outline-danger" @click="$emit('close-dlg')">&Chi;</button>
            </div>
        </div>
        <div class="row">
            <div class="col">
                <input class="form-control" v-model="name" :placeholder="language.data.iv6">
            </div>
        </div>
        <div class="row">
            <div class="col">
                <textarea class="form-control" rows="4" v-model="text"></textarea>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "addNoteDlg",
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
        async addNote() {
            this.$eventHub.$emit("show-msg", "");
            //TODO
        }
    }
}
</script>

<style scoped>

</style>