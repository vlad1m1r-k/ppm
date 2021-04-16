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

export default {
    name: "adminSettings",
    components: {
        dbSettings, users, srvSettings, groups
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
.form-bg {
    background-color: #f1f1f1 !important;
    border-radius: 3px;
}
.selected {
    border-bottom: 4px solid darkblue;
}
</style>