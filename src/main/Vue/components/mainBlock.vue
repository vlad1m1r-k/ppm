<template>
    <user-settings v-if="showUserSettings || tokenProvider.changePwd" @close-dlg="showUserSettings = false"></user-settings>
    <div class="h-100" :class="{'blur': tokenProvider.token === null}" @click="clickEvent">
        <div class="container-fluid">
            <div class="header form-bg">
                <span class="h-b1">
                    <button class="btn btn-sm btn-outline-secondary" @click="showMain">{{ language.data.mp1 }}</button>
                    <button class="btn btn-sm btn-outline-danger" @click="currentTab = 'adminSettings'" v-if="tokenProvider.adminSettings">{{ language.data.as1 }}</button>
                    <button class="btn btn-sm btn-outline-danger" @click="showTrash" v-if="tokenProvider.adminSettings" :title="language.data.di1">&#x1f5d1;</button>
                </span>
                <pwd-gen></pwd-gen>
                <search-form class="h-b3"></search-form>
                <span class="h-b4">
                    <user-menu @show-user-settings="showUserSettings = true"></user-menu>
                    <language-selector></language-selector>
                </span>
            </div>
            <div class="row">
                <div class="col">
                    <div class="alert alert-danger" v-if="message">
                        {{ message }}
                        <button class="close" @click="message = ''">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col">
                    <keep-alive exclude="adminSettings">
                        <component :is="currentTab"></component>
                    </keep-alive>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import languageSelector from "./header/languageSelector.vue";
import userMenu from "./header/userMenu.vue";
import mainTab from "./mainTabs/mainTab.vue";
import userSettings from "./mainTabs/userSettings.vue";
import adminSettings from "./mainTabs/adminSettings.vue";
import pwdGen from "./header/pwdGen.vue";
import searchForm from "./header/searchForm.vue";

export default {
    name: "mainBlock",
    components: {
        languageSelector,
        userMenu,
        mainTab,
        userSettings,
        adminSettings,
        pwdGen,
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