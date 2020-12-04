<template>
    <div class="decor-iv">
        <div class="row">
            <div class="col">
                <div class="dropdown" v-if="item.access === 'RW'">
                    <button class="btn-sm btn-outline-primary dropdown-toggle" type="button" id="dropdownMenuButton"
                            data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        {{ item.name }}
                    </button>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton">
                        <a class="dropdown-item" @click="showAddDlg = true">{{ language.data.iv1 }}</a>
                        <!--                        //TODO rename container-->
                        <div class="dropdown-divider"></div>
                        <span :title="item.children.length > 0 ? language.data.iv3 : false" :class="{'cursor-stop': item.children.length > 0}">
                        <a class="dropdown-item text-danger" :class="{disabled: this.item.children.length > 0}" @click="console.log('Delete item')">
                            {{ language.data.iv2 }}
                            <!--                        TODO delete container-->
                        </a>
                        </span>
                        <!--                        TODO access settings-->
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-auto m-2">
                <add-dlg class="decor-dlg" :item="item" v-if="showAddDlg" @close-dlg="showAddDlg = false"></add-dlg>
            </div>
        </div>
    </div>
</template>

<script>
import addDlg from "./itemView/addDlg.vue";

export default {
    name: "itemView",
    components: {
        "add-dlg": addDlg
    },
    props: {
        item: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            showAddDlg: false
        }
    },
    watch: {
        item() {
            this.showAddDlg = false;
        }
    }
}
</script>

<style scoped>
.decor-iv {
    background-color: #eaeaea;
    border-radius: 3px;
}

.decor-dlg {
    background-color: #dbdbdb;
    border-radius: 2px;
}
.cursor-stop {
    cursor: not-allowed;
}
</style>