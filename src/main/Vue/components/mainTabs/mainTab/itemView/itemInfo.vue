<template>
    <span>
        <button class="btn-img info" :title="language.data.cm8" @click.stop="show = !show"></button>
        <div v-if="show" class="info-box" @click.stop="show = false">
            {{ language.data.div2 }} : &nbsp; {{ item.createdDate }} <br>
            {{ language.data.div3 }} : &nbsp; {{ item.createdBy }} <br>
            {{ language.data.div4 }} : &nbsp; {{ item.editedDate }} <br>
            {{ language.data.div5 }} : &nbsp; {{ item.editedBy }} <br>
        </div>
    </span>
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
    },
    methods: {
        closeMenu() {
            this.show = false;
        }
    },
    created() {
        this.eventHub.on("close-menu", this.closeMenu);
    },
    beforeUnmount() {
        this.eventHub.off("close-menu", this.closeMenu);
    }
}
</script>

<style scoped>

</style>