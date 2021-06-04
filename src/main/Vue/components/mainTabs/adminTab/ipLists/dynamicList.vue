<template>
    <div class="modal-dlg">
        <div class="modal-dlg-body">
            <div class="row">
                <div class="col">
                    {{ language.data.sec8 }}
                    <button class="close" @click="$emit('close-dlg')">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </div>
            <div class="modal-dlg-scroll mt-3 p-1">
                <table class="table table-bordered table-striped table-sm mt-1">
                    <thead class="tab-header-area">
                    <tr>
                        <th>{{ language.data.sec11 }}</th>
                        <th>{{ language.data.sec12 }}</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-if="list.length === 0">
                        <td colspan="5">{{ language.data.cm9 }}</td>
                    </tr>
                    <tr v-for="(item, id) in list" :key="'dynls' + id">
                        <td>{{ item.ip }}</td>
                        <td>{{ item.expire }}</td>
                        <td>
                            <button class="btn btn-sm btn-outline-danger" :title="language.data.cm5"
                                    @click="removeIp(item.ip)">
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
    name: "dynamicList",
    emits: ["close-dlg"],
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            list: []
        }
    },
    methods: {
        async getIpList() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token
                });
                const answer = await $.ajax({
                    url: "/settings/getDynamicList",
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
                        url: "/settings/removeIpFromDynamicList",
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