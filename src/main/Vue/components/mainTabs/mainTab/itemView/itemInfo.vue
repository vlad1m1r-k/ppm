<template>
    <span class="btn-dc" :title="language.data.cm8" @click="show = !show">&#x2139;</span>
    <div v-if="show" class="info-box" @click="show = false">
        {{ language.data.div2 }} : &nbsp; {{ item.createdDate }} <br>
        {{ language.data.div3 }} : &nbsp; {{ item.createdBy }} <br>
        {{ language.data.div4 }} : &nbsp; {{ item.editedDate }} <br>
        {{ language.data.div5 }} : &nbsp; {{ item.editedBy }} <br>
    </div>
</template>

<script>
export default {
    name: "itemInfo",
    props: {
        item: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            show: false
        }
    }
}
</script>

<style scoped>

</style>