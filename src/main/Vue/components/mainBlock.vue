<template>
    <div :class="{'blur': tokenProvider.token === null}">
        Page Content
    </div>
</template>

<script>
export default {
    name: "mainBlock",
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider
        }
    }
}
</script>

<style scoped>
    .blur {
        filter: blur(5px);
    }
</style>