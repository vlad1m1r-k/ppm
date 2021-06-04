<template>
    <div class="modal-dlg">
        <div class="modal-dlg-body">
            <div class="row">
                <div class="col">
                    {{ language.data.sec7 }}
                    <button class="close" @click="$emit('close-dlg')">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </div>
            <div class="modal-dlg-scroll mt-3 p-1">
                <div class="row m-0">
                    <div class="col p-0">
                        <button class="btn btn-sm btn-outline-success" @click="addDlg = !addDlg">&#x2795;</button>
                    </div>
                </div>
                <div class="row m-0 mt-1" v-if="addDlg">
                    <div class="col p-0">
                        <input type="text" class="form-control-sm align-middle" v-model="ip" placeholder="x.x.x.x/xx">
                        <button class="btn btn-sm btn-outline-success" :disabled="isIpValid" @click="addIp">&check;
                        </button>
                        <button class="btn btn-sm btn-danger" @click="ip = ''; addDlg = false">X</button>
                    </div>
                </div>
                <table class="table table-bordered table-striped table-sm mt-1">
                    <thead></thead>
                    <tbody>
                    <tr v-if="list.length === 0">
                        <td>{{ language.data.cm9 }}</td>
                    </tr>
                    <tr v-for="(item, id) in list" :key="'whtls' + id">
                        <td>{{ item }}</td>
                        <td>
                            <button class="btn btn-sm btn-outline-danger" :title="language.data.cm5"
                                    @click="removeIp(item)">
                                &#x1f5d1;
                            </button>
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
            return !this.ip.match("^(?:(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])(\\.(?!$)|$|(\\/([1-2][0-9]|3[0-2]|[0-9])))){4}$");
        }
    },
    methods: {
        async addIp() {
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