<template>
    <div :class="{'blur': tokenProvider.token === null}">
        <div class="container-fluid">
            <div class="row justify-content-between form-bg">
                <div class="col-sm mr-auto">
                    <button class="btn btn-sm btn-outline-secondary" @click="currentTab = 'mainTab'">{{ language.data.mp1 }}</button>
                    <button class="btn btn-sm btn-outline-danger" @click="currentTab = 'adminSettings'" v-if="tokenProvider.adminSettings">{{ language.data.as1 }}</button>
                    <button class="btn btn-sm btn-outline-danger" @click="showTrash" v-if="tokenProvider.adminSettings" :title="language.data.di1">&#x1f5d1;</button>
                    <pwd-gen></pwd-gen>
                </div>
                <div class="col-sm-auto">
                    <div class="row">
                        <div class="col-sm p-0">
                            <search-form></search-form>
                        </div>
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
            message: ""
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
            this.eventHub.emit('toggle-trash', this.currentTab);
            this.setTab('mainTab');
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
    .blur {
        filter: blur(5px);
    }
    .form-bg {
        background-color: lightgrey;
    }
</style>