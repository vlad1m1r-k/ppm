<template>
    <div :class="{'blur': tokenProvider.token === null}">
        <div class="container-fluid">
            <div class="row justify-content-between form-bg">
                <div class="col-sm mr-auto">
                    <button class="btn btn-sm btn-outline-secondary" @click="currentTab = 'mainTab'">{{ language.data.mp1 }}</button>
                    <button class="btn btn-sm btn-outline-danger" @click="currentTab = 'adminSettings'" v-if="tokenProvider.adminSettings">{{ language.data.as1 }}</button>
                </div>
                <div class="col-sm-auto">
                    <div class="row">
                        <div class="col-sm p-0">
                            <user-menu @change-tab="setTab"></user-menu>
                        </div>
                        <div class="col-sm pl-0">
                            <language-selector></language-selector>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col">
                    <keep-alive>
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

export default {
    name: "mainBlock",
    components: {
        languageSelector,
        userMenu,
        mainTab,
        userSettings,
        adminSettings
    },
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            currentTab: "mainTab"
        }
    },
    methods: {
        setTab(tabName) {
            this.currentTab = tabName;
        }
    }
}
</script>

<style scoped>
    .blur {
        filter: blur(5px);
    }
    .form-bg {
        background-color: lightgrey;
    }
</style>