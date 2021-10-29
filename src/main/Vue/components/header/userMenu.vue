<template>
    <span class="dropdown">
        <button class="btn-sm btn-outline-secondary dropdown-toggle" type="button" @click.stop="showMenu = !showMenu">
            {{ tokenProvider.userName }}
        </button>
        <div class="dropdown-menu dropdown-menu-right" :class="{show: showMenu}">
            <a class="dropdown-item" @click="$emit('show-user-settings')">{{ language.data.um1 }}</a>
            <a class="dropdown-item" @click="logout">{{ language.data.um2 }}</a>
        </div>
    </span>
</template>

<script>
export default {
    name: "userMenu",
    emits: ["show-user-settings"],
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            showMenu: false
        }
    },
    methods: {
        logout() {
            this.tokenProvider.logout();
        },
        closeMenu() {
            this.showMenu = false;
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