<template>
    <div :class="{'searchTarget': isSearchTarget}">
        <div class="iv-item">
            <img class="iv-icon" src="/svg/lock.svg">
            <item-info :item="pwd" v-if="access === 'RW'"></item-info><span v-else>&nbsp;</span>
            <span class="flex-view">
                <span class="iv-item-name" @click="toggle" :title="language.data.cm1">{{ pwd.name }}</span>
                <button class="btn-img edit" :title="language.data.cm2" v-show="show && access === 'RW' && !edit" @click="edit = true"></button>
                <button class="btn-img acpt" :title="language.data.cm3" v-show="show && access === 'RW' && edit" @click="save"></button>
                <button class="btn-img cncl" :title="language.data.cm4" v-show="show && access === 'RW' && edit" @click="cancel"></button>
            </span>
            <button class="btn-img rmv" :title="language.data.cm5" v-show="access === 'RW'" @click="remove"></button>
            <span class="iv-item-link"><span style="user-select: none">{{ language.data.cm10 }} &nbsp;</span>$id:{{ $parent.$props.item.id }}p{{ pwd.id }}&nbsp;</span>
        </div>
        <input class="input" v-show="show && edit" v-model="name">
        <input class="input" v-show="show" v-model="login" :readonly="!edit">
        <div v-show="show" style="display: flex">
            <button class="btn-img show" :title="language.data.cm1" v-show="!edit" @click="loadPwdBody"></button>
            <button class="btn-img copy" :title="language.data.cm6" v-show="!edit" @click="pwdToClipboard"></button>
            <input class="input" v-model="pass" :readonly="!edit" placeholder="******">
        </div>
        <textarea class="text-box" rows="4" :readonly="!edit" v-show="show" v-model="note"></textarea>
    </div>
</template>

<script>
import itemInfo from "./itemInfo.vue";

export default {
    name: "pwdView",
    props: {
        pwd: Object,
        access: String,
        searchData: Object
    },
    components: {
        itemInfo
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            show: false,
            edit: false,
            name: "",
            login: "",
            pass: "",
            note: ""
        }
    },
    computed: {
        isSearchTarget() {
            if (this.searchData) {
                return this.searchData.cntId === this.$parent.$props.item.id && this.searchData.type === "p" && this.searchData.itemId === this.pwd.id;
            }
            return false;
        }
    },
    methods: {
        toggle() {
            if (this.show) {
                this.show = false;
                this.edit = false;
                this.name = this.pwd.name;
                this.login = "";
                this.pass = "";
                this.note = "";
            } else {
                this.loadPwdEnv();
                this.show = true;
            }
        },
        async loadPwdEnv() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    pwd: this.pwd.id,
                });
                const answer = await $.ajax({
                    url: "/container/getPwdEnv",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                this.login = data.login;
                this.note = data.note;
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async loadPwdBody() {
            if (this.pass) {
                this.pass = "";
            } else {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        pwd: this.pwd.id,
                    });
                    const answer = await $.ajax({
                        url: "/container/getPwdBody",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    this.pass = data.password;
                    setTimeout(this.clearPwd, 10000);
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        async pwdToClipboard() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    pwd: this.pwd.id,
                });
                const answer = await $.ajax({
                    url: "/container/getPwdBody",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                navigator.clipboard.writeText(data.password);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async save() {
            if (this.name && confirm(this.language.data.cm3 + " " + this.pwd.name + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        pwd: this.pwd.id,
                        name: this.name,
                        login: this.login,
                        pass: this.pass,
                        note: this.note
                    });
                    const answer = await $.ajax({
                        url: "/container/editPassword",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    }
                    this.edit = false;
                    this.pass = "";
                    this.eventHub.emit("update-tree");
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        cancel() {
            this.edit = false;
            this.name = this.pwd.name;
            this.pass = "";
            this.loadPwdEnv();
        },
        async remove() {
            if (this.name && confirm(this.language.data.cm5 + " " + this.pwd.name + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        pwd: this.pwd.id
                    });
                    const answer = await $.ajax({
                        url: "/container/removePassword",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    }
                    this.eventHub.emit("update-tree");
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        clearPwd() {
            if (!this.edit) {
                this.pass = "";
            }
        }
    },
    mounted() {
        this.name = this.pwd.name;
    }
}
</script>

<style scoped>

</style>