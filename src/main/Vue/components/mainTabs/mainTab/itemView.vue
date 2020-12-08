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
                        <a class="dropdown-item" @click="showRenameDlg = true" v-if="item.name !== 'root'">{{ language.data.iv4 }}</a>
                        <div class="dropdown-divider" v-if="item.name !== 'root'"></div>
                        <span :title="item.children.length > 0 ? language.data.iv3 : false"
                              :class="{'cursor-stop': item.children.length > 0}" v-if="item.name !== 'root'">
                        <a class="dropdown-item text-danger"
                           :class="{disabled: this.item.children.length > 0 || item.name === 'root'}"
                           @click="deleteContainer">
                            {{ language.data.iv2 }}
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
                <rename-dlg class="decor-dlg" :item="item" v-if="showRenameDlg" @close-dlg="showRenameDlg = false"></rename-dlg>
            </div>
        </div>
    </div>
</template>

<script>
import addDlg from "./itemView/addDlg.vue";
import renameDlg from "./itemView/renameDlg.vue";

export default {
    name: "itemView",
    components: {
        "add-dlg": addDlg,
        "rename-dlg": renameDlg
    },
    props: {
        item: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            showAddDlg: false,
            showRenameDlg: false
        }
    },
    watch: {
        item() {
            this.showAddDlg = false;
            this.showRenameDlg = false;
        }
    },
    methods: {
        async deleteContainer() {
            this.$eventHub.$emit("show-msg", "");
            if (this.item.children.length === 0 && this.item.name !== 'root') {
                const confResult = confirm(this.language.data.iv2 + " \"" + this.item.name + "\" ?");
                if (confResult) {
                    try {
                        const token = await this.tokenProvider.getToken();
                        const encryptedData = await Vue.cryptoProvider.encrypt({
                            token: token,
                            item: this.item.id,
                        });
                        const answer = await $.ajax({
                            url: "/container/delete",
                            method: "POST",
                            data: encryptedData
                        });
                        const data = Vue.cryptoProvider.decrypt(answer);
                        if (data.message) {
                            this.$eventHub.$emit("show-msg", this.language.data[data.message]);
                        } else {
                            this.$eventHub.$emit("update-tree");
                        }
                    } catch (e) {
                        this.$eventHub.$emit("show-msg", Vue.errorParser(e));
                    }
                }
            }
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