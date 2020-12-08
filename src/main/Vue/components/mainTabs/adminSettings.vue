<template>
    <div class="form-bg">
        <div class="row">
            <div class="col">
                <button class="btn btn-sm btn-outline-primary" @click="currentTab = 'dbSettings'">{{ language.data.db1 }}</button>
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

export default {
    name: "adminSettings",
    components: {
        dbSettings
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
    background-color: #f1f1f1;
    border-radius: 3px;
}
</style>