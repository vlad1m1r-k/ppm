<template>
    <div>
        {{ item.name }}
    </div>
</template>

<script>
export default {
    name: "itemView",
    props: {
        item: Object
    }
}
</script>

<style scoped>

</style>