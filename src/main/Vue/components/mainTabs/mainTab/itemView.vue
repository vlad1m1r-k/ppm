<template>
    <access-mgmt-dlg v-if="showAccessMgmtDlg" :item="item" @close-dlg="showAccessMgmtDlg = false"></access-mgmt-dlg>
    <input type="file" v-if="item.access === 'RW'" style="display: none" ref="cnt_file" @change="addFile">
    <div class="iv-header">
        <span v-if="item.access === 'RW'">
            <button class="btn-img menu" @click.stop="showMenu = !showMenu" :title="language.data.cm12"></button>
            <div class="dropdown" :class="{ show: showMenu }">
                <a class="dropdown-item" @click="showAddDlg = true">{{ language.data.iv1 }}</a>
                <a class="dropdown-item" @click="showRenameDlg = true" v-if="item.name !== 'root'">
                    {{ language.data.iv4 }}
                </a>
                <a class="dropdown-item" @click="showAddNoteDlg = true">{{ language.data.iv5 }}</a>
                <a class="dropdown-item" @click="showAddPwdDlg = true">{{ language.data.iv11 }}</a>
                <a class="dropdown-item" @click="$refs.cnt_file.click()">{{ language.data.iv13 }}</a>
                <a class="dropdown-item" v-if="tokenProvider.adminSettings" @click="showAccessMgmtDlg = true">
                    {{ language.data.iv12 }}
                </a>
                <a class="dropdown-item" @click="copyToClipboard" v-if="item.name !== 'root'">{{ language.data.cf1 }}</a>
                <a class="dropdown-item" v-if="clipboard.id && !(clipboard.type === 'c' && clipboard.id === item.id)" :title="clipboard.name" @click="moveClipboard">{{ language.data.cf3 }}</a>
                <div class="dropdown-divider" v-if="item.name !== 'root'"></div>
                <span :title="item.children.length > 0 ? language.data.iv3 : ''" :class="{ 'cursor-stop': item.children.length > 0 }" v-if="item.name !== 'root'">
                    <a style="width: 100%;" class="dropdown-item" :class="{ disabled: item.children.length > 0 || item.name === 'root' }" @click="deleteContainer">
                        {{ language.data.iv2 }}
                    </a>
                </span>
            </div>
        </span>
        <b>&#8810 {{ item.name }} &#8811</b>
        <span class="iv-d">{{ language.data.cm10 }}</span>
        <span>$id:{{ item.id }}</span>
    </div>
    <div class="iv-d2">
        <add-dlg :item="item" v-if="showAddDlg" @close-dlg="showAddDlg = false"></add-dlg>
        <rename-dlg :item="item" v-if="showRenameDlg" @close-dlg="showRenameDlg = false"></rename-dlg>
        <add-note :item="item" v-if="showAddNoteDlg" @close-dlg="showAddNoteDlg = false"></add-note>
        <add-pwd :item="item" v-if="showAddPwdDlg" @close-dlg="showAddPwdDlg = false"></add-pwd>
        {{ language.data.iv7 }} <br><br>
        <note-view v-for="note in item.notes" :note="note" :access="item.access" :key="'N' + note.id"
            :search-data="searchData"></note-view>
        <br> {{ language.data.iv8 }} <br><br>
        <pwd-view v-for="pwd in item.passwords" :pwd="pwd" :access="item.access" :key="'P' + pwd.id"
            :search-data="searchData"></pwd-view>
        <br>{{ language.data.fl1 }} <br><br>
        <table class="table" v-if="item.files && item.files.length">
            <thead></thead>
            <tbody>
                <fls-view v-for="file in item.files" :file="file" :access="item.access" :key="'F' + file.id"
                    :search-data="searchData"></fls-view>
            </tbody>
        </table>
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
            clipboard: this.$root.$data.clipboard,
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
        moveClipboard() {
            switch (this.clipboard.type) {
                case "c":
                    this.moveContainer();
                    break;
                case "n":
                    this.moveNote();
                    break;
                case "p":
                    this.movePwd();
                    break;
                case "f":
                    this.moveFile();
            }
        },
        async moveContainer() {
            this.eventHub.emit("show-msg", "");
            const confResult = confirm(this.language.data.cf1 + " " + this.language.data.cf4 + " \"" + this.clipboard.name +
                    "\" " + this.language.data.cf2 + " \"" + this.item.name + "\" ?");
            if (confResult) {
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        item: this.clipboard.id,
                        moveTo: this.item.id
                    });
                    const answer = await $.ajax({
                        url: "/container/move",
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
            };
            this.clearClipboard();
        },
        async moveNote() {
            this.eventHub.emit("show-msg", "");
            const confResult = confirm(this.language.data.cf1 + " " + this.language.data.cf5 + " \"" + this.clipboard.name +
                    "\" " + this.language.data.cf2 + " \"" + this.item.name + "\" ?");
            if (confResult) {
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        item: this.clipboard.id,
                        moveTo: this.item.id
                    });
                    const answer = await $.ajax({
                        url: "/container/moveNote",
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
            };
            this.clearClipboard();
        },
        async movePwd() {
            this.eventHub.emit("show-msg", "");
            const confResult = confirm(this.language.data.cf1 + " " + this.language.data.cf6 + " \"" + this.clipboard.name +
                    "\" " + this.language.data.cf2 + " \"" + this.item.name + "\" ?");
            if (confResult) {
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        item: this.clipboard.id,
                        moveTo: this.item.id
                    });
                    const answer = await $.ajax({
                        url: "/container/movePwd",
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
            };
            this.clearClipboard();
        },
        async moveFile() {
            this.eventHub.emit("show-msg", "");
            const confResult = confirm(this.language.data.cf1 + " " + this.language.data.cf7 + " \"" + this.clipboard.name +
                    "\" " + this.language.data.cf2 + " \"" + this.item.name + "\" ?");
            if (confResult) {
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        item: this.clipboard.id,
                        moveTo: this.item.id
                    });
                    const answer = await $.ajax({
                        url: "/container/moveFile",
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
            };
            this.clearClipboard();
        },
        closeMenu() {
            this.showMenu = false;
        },
        copyToClipboard() {
            this.clipboard.id = this.item.id;
            this.clipboard.type = "c";
            this.clipboard.name = this.item.name;
        },
        clearClipboard() {
            this.clipboard.id = null;
            this.clipboard.type = null;
            this.clipboard.name = null;
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