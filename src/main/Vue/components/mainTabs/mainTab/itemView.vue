<template>
    <access-mgmt-dlg v-if="showAccessMgmtDlg" :item="item" @close-dlg="showAccessMgmtDlg = false"></access-mgmt-dlg>
    <input type="file" v-if="item.access === 'RW'" style="display: none" ref="cnt_file" @change="addFile">
    <div class="decor-iv">
        <div class="row m-0">
            <div class="col-sm-auto">
                <div class="dropdown" v-if="item.access === 'RW'">
                    <button class="btn-sm btn-outline-primary dropdown-toggle" type="button" id="dropdownMenuButton"
                            data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" @click.stop="showMenu = !showMenu">
                        {{ item.name }}
                    </button>
                    <div class="dropdown-menu dropdown-menu-right" :class="{show: showMenu}" aria-labelledby="dropdownMenuButton">
                        <a class="dropdown-item cursor-pointer" @click="showAddDlg = true">{{ language.data.iv1 }}</a>
                        <a class="dropdown-item cursor-pointer" @click="showRenameDlg = true" v-if="item.name !== 'root'">
                            {{ language.data.iv4 }}
                        </a>
                        <a class="dropdown-item cursor-pointer" @click="showAddNoteDlg = true">{{ language.data.iv5 }}</a>
                        <a class="dropdown-item cursor-pointer" @click="showAddPwdDlg = true">{{ language.data.iv11 }}</a>
                        <a class="dropdown-item cursor-pointer" @click="$refs.cnt_file.click()">{{ language.data.iv13 }}</a>
                        <a class="dropdown-item cursor-pointer" v-if="tokenProvider.adminSettings"
                           @click="showAccessMgmtDlg = true">
                            {{ language.data.iv12 }}
                        </a>
                        <div class="dropdown-divider" v-if="item.name !== 'root'"></div>
                        <span :title="item.children.length > 0 ? language.data.iv3 : false"
                              :class="{'cursor-stop': item.children.length > 0}" v-if="item.name !== 'root'">
                        <a class="dropdown-item text-danger cursor-pointer"
                           :class="{disabled: item.children.length > 0 || item.name === 'root'}"
                           @click="deleteContainer">
                            {{ language.data.iv2 }}
                        </a>
                        </span>
                    </div>
                </div>
            </div>
            <div class="col">
                <span style="user-select: none">{{ language.data.cm10 }} &nbsp;</span>$id:{{ item.id }}
            </div>
        </div>
        <div class="row m-0">
            <div class="col m-2">
                <add-dlg :item="item" v-if="showAddDlg" @close-dlg="showAddDlg = false"></add-dlg>
                <rename-dlg :item="item" v-if="showRenameDlg" @close-dlg="showRenameDlg = false"></rename-dlg>
                <add-note :item="item" v-if="showAddNoteDlg" @close-dlg="showAddNoteDlg = false"></add-note>
                <add-pwd :item="item" v-if="showAddPwdDlg" @close-dlg="showAddPwdDlg = false"></add-pwd>
                {{ language.data.iv7 }} <br>
                <note-view v-for="note in item.notes" :note="note" :access="item.access" :key="'N' + note.id" :search-data="searchData"></note-view>
                {{ language.data.iv8 }} <br>
                <pwd-view v-for="pwd in item.passwords" :pwd="pwd" :access="item.access" :key="'P' + pwd.id" :search-data="searchData"></pwd-view>
                {{ language.data.fl1 }} <br>
                <table class="table table-bordered table-striped table-sm">
                    <thead></thead>
                    <tbody>
                        <fls-view v-for="file in item.files" :file="file" :access="item.access" :key="'F' + file.id" :search-data="searchData"></fls-view>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>

<script>
import addDlg from "./itemView/addDlg.vue";
import renameDlg from "./itemView/renameDlg.vue";
import addNote from "./itemView/addNoteDlg.vue";
import noteView from "./itemView/noteView.vue";
import addPwd from "./itemView/addPwdDlg.vue";
import pwdView from "./itemView/pwdView.vue";
import accessMgmtDlg from "./itemView/accessMgmtDlg.vue";
import flsView from "./itemView/flsView.vue";

export default {
    name: "itemView",
    components: {
        "add-dlg": addDlg,
        "rename-dlg": renameDlg,
        "add-note": addNote,
        "note-view": noteView,
        "add-pwd": addPwd,
        "pwd-view": pwdView,
        accessMgmtDlg, flsView
    },
    props: {
        item: Object,
        searchData: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            showAddDlg: false,
            showRenameDlg: false,
            showAddNoteDlg: false,
            showAddPwdDlg: false,
            showAccessMgmtDlg: false,
            showMenu: false
        }
    },
    watch: {
        item() {
            this.showAddDlg = false;
            this.showRenameDlg = false;
            this.showAddNoteDlg = false;
            this.showAddPwdDlg = false;
        }
    },
    methods: {
        async deleteContainer() {
            this.eventHub.emit("show-msg", "");
            if (this.item.children.length === 0 && this.item.name !== 'root') {
                const confResult = confirm(this.language.data.iv2 + " \"" + this.item.name + "\" ?");
                if (confResult) {
                    try {
                        const token = await this.tokenProvider.getToken();
                        const encryptedData = await cryptoProvider.encrypt({
                            token: token,
                            item: this.item.id,
                        });
                        const answer = await $.ajax({
                            url: "/container/delete",
                            method: "POST",
                            data: encryptedData
                        });
                        const data = cryptoProvider.decrypt(answer);
                        if (data.message) {
                            this.eventHub.emit("show-msg", this.language.data[data.message]);
                        } else {
                            this.eventHub.emit("update-tree");
                        }
                    } catch (e) {
                        this.eventHub.emit("show-msg", this.errorParser(e));
                    }
                }
            }
        },
        addFile() {
            if (this.$refs.cnt_file.files[0].size > 27262976) {
                this.eventHub.emit("show-msg", this.language.data.fle3);
                return;
            }
            const reader = new FileReader();
            reader.onload = d => this.sendFile(d.target.result);
            reader.readAsDataURL(this.$refs.cnt_file.files[0]);
        },
        async sendFile(fileBody) {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    containerId: this.item.id,
                    name: this.$refs.cnt_file.files[0].name,
                    size: this.$refs.cnt_file.files[0].size,
                    body: fileBody
                });
                const answer = await $.ajax({
                    url: "/container/addFile",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                if (data.message) {
                    this.eventHub.emit("show-msg", this.language.data[data.message]);
                } else {
                    this.eventHub.emit("update-tree");
                }
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        closeMenu() {
            this.showMenu = false;
        }
    },
    created() {
        this.eventHub.on("close-menu", this.closeMenu);
    },
    beforeUnmount() {
        this.eventHub.off("close-menu", this.closeMenu);
    }
}
</script>

<style scoped>

</style>