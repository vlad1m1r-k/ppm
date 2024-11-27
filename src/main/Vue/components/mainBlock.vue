<template>
    <user-settings v-if="showUserSettings || tokenProvider.changePwd" @close-dlg="showUserSettings = false"></user-settings>
    <div class="main-blck" :class="{'blur': tokenProvider.token === null}" @click="clickEvent">
        <div class="m-row1">
            <span class="h-col1">
                <button class="btn blue" @click="showMain">{{ language.data.mp1 }}</button>
                <button class="btn red" @click="currentTab = 'adminSettings'" v-if="tokenProvider.adminSettings">{{ language.data.as1 }}</button>
                <button class="btn red icon" @click="showTrash" v-if="tokenProvider.adminSettings" :title="language.data.di1">
                    <svg class="s-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M5 7.5H19L18 21H6L5 7.5Z" stroke="#000000" stroke-linejoin="round"/>
                        <path d="M15.5 9.5L15 19" stroke="#000000" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M12 9.5V19" stroke="#000000" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M8.5 9.5L9 19" stroke="#000000" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M16 5H19C20.1046 5 21 5.89543 21 7V7.5H3V7C3 5.89543 3.89543 5 5 5H8M16 5L15 3H9L8 5M16 5H8" stroke="#000000" stroke-linejoin="round"/>
                    </svg>
                </button>
            </span>
            <pwd-gen></pwd-gen>
            <search-form class="h-col3"></search-form>
            <span class="h-col4">
                <user-menu @show-user-settings="showUserSettings = true"></user-menu>
                <language-selector></language-selector>
            </span>
        </div>
        <div class="m-row2" v-if="message">
            {{ message }}
            <button class="close" @click="message = ''">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <keep-alive exclude="adminSettings">
            <component class="m-row3" :is="currentTab"></component>
        </keep-alive>
    </div>
</template>

<script>
import { defineAsyncComponent } from "vue";
import languageSelector from "./header/languageSelector.vue";
import userMenu from "./header/userMenu.vue";
import mainTab from "./mainTabs/mainTab.vue";
import userSettings from "./mainTabs/userSettings.vue";
import searchForm from "./header/searchForm.vue";

export default {
    name: "mainBlock",
    components: {
        languageSelector,
        userMenu,
        mainTab,
        userSettings,
        adminSettings: defineAsyncComponent(() => import("./mainTabs/adminSettings.vue")),
        pwdGen: defineAsyncComponent(() => import("./header/pwdGen.vue")),
        searchForm
    },
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            currentTab: "mainTab",
            message: "",
            showUserSettings: false
        }
    },
    methods: {
        setTab(tabName) {
            this.currentTab = tabName;
        },
        showMsg(msg) {
            this.message = msg;
        },
        showTrash() {
            this.eventHub.emit("toggle-trash", this.currentTab);
            this.setTab("mainTab");
        },
        showMain() {
            this.setTab("mainTab");
            this.eventHub.emit("show-main");
        },
        clickEvent() {
            this.eventHub.emit("close-menu");
        }
    },
    created() {
        this.eventHub.on("show-msg", this.showMsg);
    },
    beforeUnmount() {
        this.eventHub.off("show-msg", this.showMsg);
    }
}
</script>

<style scoped>
</style>