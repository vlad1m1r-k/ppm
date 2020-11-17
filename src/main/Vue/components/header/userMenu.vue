<template>
    <div class="dropdown">
        <button class="btn-sm btn-outline-secondary dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            {{ tokenProvider.userName }}
        </button>
        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton">
            <a class="dropdown-item" @click="$emit('change-tab', 'userSettings')">{{ language.data.um1 }}</a>
            <a class="dropdown-item" @click="logout">{{ language.data.um2 }}</a>
        </div>
    </div>
</template>

<script>
export default {
    name: "userMenu",
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider
        }
    },
    methods: {
        logout() {
            this.tokenProvider.logout();
        }
    }
}
</script>

<style scoped>

</style>