<template>
    <div class="form-bg pl-3 pr-3 pt-1">
        <div class="row">
            <div class="col">
                <button class="btn btn-sm btn-outline-primary" @click="currentTab = 'users'" :class="{selected: currentTab === 'users'}">
                    {{ language.data.us1 }}
                </button>
                <button class="btn btn-sm btn-outline-primary" @click="currentTab = 'groups'" :class="{selected: currentTab === 'groups'}">
                    {{ language.data.gp1 }}
                </button>
                <button class="btn btn-sm btn-outline-primary" @click="currentTab = 'dbSettings'" :class="{selected: currentTab === 'dbSettings'}">
                    {{ language.data.db1 }}
                </button>
              <button class="btn btn-sm btn-outline-primary" @click="currentTab = 'srvSettings'" :class="{selected: currentTab === 'srvSettings'}">
                {{ language.data.srv1 }}
              </button>
                <button class="btn btn-sm btn-outline-primary" @click="currentTab = 'security'" :class="{selected: currentTab === 'security'}">
                    {{ language.data.sec1 }}
                </button>
                <button class="btn btn-sm btn-outline-primary" @click="currentTab = 'logs'" :class="{selected: currentTab === 'logs'}">
                    {{ language.data.lg1 }}
                </button>
            </div>
        </div>
        <div class="row">
            <div class="col">
                <component :is="currentTab"></component>
            </div>
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
    },
    //TODO
    // watch: {
    //     currentTab(newValue) {
    //         this.eventHub.emit("logTab-active", newValue === "logs");
    //     }
    // }
}
</script>

<style scoped>
.form-bg {
    background-color: #f1f1f1 !important;
    border-radius: 3px;
}
.selected {
    border-bottom: 4px solid darkblue;
}
</style>