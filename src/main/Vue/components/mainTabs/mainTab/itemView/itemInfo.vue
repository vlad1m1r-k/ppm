<template>
    <span>
        <div v-if="$parent.$data.showInfo" class="info-box" @click.stop="$parent.$data.showInfo = false">
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
            language: this.$root.$data.language
        }
    },
    methods: {
        closeMenu() {
            this.$parent.$data.showInfo = false;
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