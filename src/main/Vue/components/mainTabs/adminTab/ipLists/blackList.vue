<template>
    <div class="modal-dlg">
        <div class="modal-dlg-body">
            <div class="row">
                <div class="col">
                    {{ language.data.sec6 }}
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
                //
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "blackList",
    emits: ["close-dlg"],
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            addDlg: false,
            ip: ""
        }
    },
    computed: {
        isIpValid() {
            return !this.ip.match("^(?:(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])(\\.(?!$)|$|(\\/([1-3][0-2]|[0-9])))){4}$");
        }
    },
    methods: {
        addIp() {
            //TODO
        }
    }
}
</script>

<style scoped>

</style>