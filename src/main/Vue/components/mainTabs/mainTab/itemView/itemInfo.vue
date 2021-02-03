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
.btn-dc {
    cursor: pointer;
    user-select: none;
    padding-left: 2px;
    padding-right: 2px;
}

.btn-dc:hover {
    background-color: darkgray;
    border-radius: 4px;
}
.info-box {
    margin-left: 15px;
    padding: 10px;
    display: inline;
    position: absolute;
    z-index: 1000000;
    background-color: #c0c4c4;
    border-bottom-width: 0;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Roboto", "Oxygen",
    "Ubuntu", "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue",
    sans-serif;
    border-radius: 4px;
    user-select: none;
}
</style>