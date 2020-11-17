<template>
    <div :class="{'blur': tokenProvider.token === null}">
        <div class="container-fluid">
            <div class="row justify-content-between">
                <div class="col-sm mr-auto">
                    <button class="btn btn-sm btn-outline-secondary" @click="currentTab = 'mainTab'">{{ language.data.mp1 }}</button>
                </div>
                <div class="col-sm-auto">
                    <div class="row">
                        <div class="col-sm p-0">
                            <user-menu @change-tab="currentTab = $event"></user-menu>
                        </div>
                        <div class="col-sm pl-0">
                            <language-selector :lang-name="language.name" @language-changed="setLang"></language-selector>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-auto">
                    <component :is="currentTab"></component>
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

export default {
    name: "mainBlock",
    components: {
        languageSelector,
        userMenu,
        mainTab,
        userSettings
    },
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            currentTab: mainTab
        }
    },
    methods: {
        setLang(langName) {
            document.cookie = "lang=" + langName + "; expires=Fri, 31 Dec 9999 23:59:59 GMT; samesite=strict";
            this.$root.loadLanguage(langName);
        }
    }
}
</script>

<style scoped>
    .blur {
        filter: blur(5px);
    }
</style>