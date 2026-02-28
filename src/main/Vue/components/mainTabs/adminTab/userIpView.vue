<template>
    <div class="modal">
        <div class="modal-body dialog">
            <div class="modal-header">
                {{ language.data.us3 }} ({{ user.login }})
                <button class="btn-img cncl" @click="$emit('close-dlg')"></button>
            </div>
            <div>
                <button class="btn blue" @click="addDlg = !addDlg">&#x2795;</button>
                <div class="flex-view" v-if="addDlg">
                    <input type="text" class="input-sm" v-model="ip" placeholder="x.x.x.x/xx" ref="admUsrAddIp" @keypress.enter="addIp" @keydown.esc="ip = ''; addDlg = false">
                    <button class="btn-img acpt" :disabled="!isIpValid" @click="addIp"></button>
                    <button class="btn-img cncl" @click="ip = ''; addDlg = false"></button>
                </div>
                <table class="table">
                    <thead></thead>
                    <tbody>
                        <tr v-if="list.length === 0">
                            <td>{{ language.data.cm9 }}</td>
                        </tr>
                        <tr v-for="(item, id) in list" :key="'usrip' + id">
                            <td>{{ item }}</td>
                            <td>
                                <button class="btn-img rmv" :title="language.data.cm5" @click="removeIp(item)"></button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "userIpView",
    emits: ["close-dlg"],
    props: {
        user: Object
    },
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            addDlg: false,
            list: [],
            ip: ""
        }
    },
    computed: {
        isIpValid() {
            return this.ip.match("^(?:(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])(\\.(?!$)|$|(\\/([1-2][0-9]|3[0-2]|[0-9])))){4}$");
        }
    },
    watch: {
        addDlg(value) {
            if (value) {
                this.$nextTick(() => this.$refs.admUsrAddIp.focus());
            }
        }
    },
    methods: {
        async getIpList() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    userId: this.user.id
                });
                const answer = await $.ajax({
                    url: "/user/getAllowedIp",
                    method: "POST",
                    data: encryptedData
                });
                this.list = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async addIp() {
            this.eventHub.emit("show-msg", "");
            if (this.isIpValid) {
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        ip: this.ip,
                        userId: this.user.id
                    });
                    const answer = await $.ajax({
                        url: "/user/addAllowedIp",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    } else {
                        this.ip = "";
                        this.getIpList();
                    }
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        async removeIp(ip) {
            if (confirm(this.language.data.cm5 + " " + ip + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        userId: this.user.id,
                        ip: ip
                    });
                    await $.ajax({
                        url: "/user/removeAllowedIp",
                        method: "POST",
                        data: encryptedData
                    });
                    this.getIpList();
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        }
    },
    beforeMount() {
        this.getIpList();
    }
}
</script>

<style scoped>

</style>