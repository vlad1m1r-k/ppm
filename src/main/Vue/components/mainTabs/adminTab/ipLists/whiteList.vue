<template>
    <div class="modal">
        <div class="modal-body dialog">
            <div class="modal-header">
                {{ language.data.sec7 }}
                <button class="btn-img cncl" @click="$emit('close-dlg')"></button>
            </div>
            <div>
                <button class="btn blue" @click="addDlg = !addDlg">&#x2795;</button>
                <div class="flex-view" v-if="addDlg">
                        <input type="text" v-model="ip" placeholder="x.x.x.x/xx" ref="admWLst" @keypress.enter="addIp" @keydown.esc="ip = ''; addDlg = false">
                        <button class="btn-img acpt" :disabled="!isIpValid" @click="addIp"></button>
                        <button class="btn-img cncl" @click="ip = ''; addDlg = false"></button>
                </div>
                <table class="table">
                    <thead></thead>
                    <tbody>
                    <tr v-if="list.length === 0">
                        <td>{{ language.data.cm9 }}</td>
                    </tr>
                    <tr v-for="(item, id) in list" :key="'whtls' + id">
                        <td>{{ item }}</td>
                        <td class="fit">
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
    name: "whiteList",
    emits: ["close-dlg"],
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
                this.$nextTick(() => this.$refs.admWLst.focus());
            }
        }
    },
    methods: {
        async addIp() {
            if (this.isIpValid) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        ip: this.ip
                    });
                    const answer = await $.ajax({
                        url: "/settings/addIpToWhiteList",
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
        async getIpList() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token
                });
                const answer = await $.ajax({
                    url: "/settings/getIpWhiteList",
                    method: "POST",
                    data: encryptedData
                });
                this.list = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async removeIp(ip) {
            if (confirm(this.language.data.cm5 + " " + ip + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        ip: ip
                    });
                    await $.ajax({
                        url: "/settings/removeIpFromWhiteList",
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