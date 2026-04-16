<template>
    <div>
        <div>
            <button class="btn blue" @click="currentTab = 'users'" :class="{ selected: currentTab === 'users' }">{{ language.data.us1 }}</button>
            <button class="btn blue" @click="currentTab = 'groups'" :class="{ selected: currentTab === 'groups' }">{{ language.data.gp1 }}</button>
            <button class="btn blue" @click="currentTab = 'dbSettings'" :class="{ selected: currentTab === 'dbSettings' }">{{ language.data.db1 }}</button>
            <button class="btn blue" @click="currentTab = 'srvSettings'" :class="{ selected: currentTab === 'srvSettings' }">{{ language.data.srv1 }}</button>
            <button class="btn blue" @click="currentTab = 'security'" :class="{ selected: currentTab === 'security' }">{{ language.data.sec1 }}</button>
            <button class="btn blue" @click="currentTab = 'logs'" :class="{ selected: currentTab === 'logs' }">{{ language.data.lg1 }}</button>
        </div>
        <div>
            <component :is="currentTab"></component>
        </div>
    </div>
</template>

<script>
import dbSettings from "./adminTab/dbSettings.vue";
import users from "./adminTab/users.vue";
import srvSettings from "./adminTab/srvSettings.vue";
import groups from "./adminTab/groups.vue";
import security from "./adminTab/security.vue";
import logs from "./adminTab/logs.vue";

export default {
    name: "adminSettings",
    components: {
        dbSettings, users, srvSettings, groups, security, logs
    },
    data() {
        return {
            language: this.$root.$data.language,
            currentTab: "users",
        }
    }
}
</script>

<style scoped>

</style>