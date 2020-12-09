<template>
    <div class="form-bg pl-3 pr-3 pt-1">
        <div class="row">
            <div class="col">
                <button class="btn btn-sm btn-outline-primary" @click="currentTab = 'users'" :class="{selected: currentTab === 'users'}">
                    {{ language.data.us1 }}
                </button>
                <button class="btn btn-sm btn-outline-primary" @click="currentTab = 'dbSettings'" :class="{selected: currentTab === 'dbSettings'}">
                    {{ language.data.db1 }}
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

export default {
    name: "adminSettings",
    components: {
        dbSettings, users
    },
    data() {
        return {
            language: this.$root.$data.language,
            currentTab: "",
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
    border-color: darkblue;
}
</style>