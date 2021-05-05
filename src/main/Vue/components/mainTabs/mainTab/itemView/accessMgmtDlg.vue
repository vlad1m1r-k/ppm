<template>
    <div class="l-modal">
        <div class="l-modal-body">
            <div class="row">
                <div class="col">
                    {{ item.name }}
                    <button class="close" @click="$emit('close-dlg')">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </div>
            <div class="scroll mt-3 p-1">
                <div class="row m-0">
                    <div class="col p-0">
                        <button class="btn btn-sm btn-outline-success" @click="addDlg = !addDlg">&#x2795;</button>
                    </div>
                </div>
                <div class="row m-0" v-if="addDlg">
                    <div class="col p-0">
                        <select class="form-control-sm" v-model="groupId">
                            <option selected disabled value="null">{{ language.data.gp1 }}</option>
                            <option v-for="grp in groups" :value="grp.id">
                                {{ grp.name }}
                            </option>
                        </select>
                        <select class="form-control-sm" v-model="access">
                            <option selected disabled value="null">{{ language.data.amg1 }}</option>
                            <option>
<!--                                TODO-->
                                {{ grp.name }}
                            </option>
                        </select>
                    </div>
                </div>
                DATA
            </div>
        </div>
    </div>
</template>

<script>
import Sort from "../../../../sort";

export default {
    name: "accessMgmtDlg",
    props: {
        item: Object
    },
    emits: ['close-dlg'],
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            groups: [],
            sort: new Sort("name", "asc"),
            addDlg: false,
            groupId: null,
            access: null
        }
    },
    methods: {
        async getGroups() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    sort: this.sort.toString()
                });
                const answer = await $.ajax({
                    url: "/group/getGroups",
                    method: "POST",
                    data: encryptedData
                });
                this.groups = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
    },
    beforeMount() {
        this.getGroups();
    }
}
</script>

<style scoped>
.l-modal {
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    position: fixed;
    background: rgba(0, 0, 0, 0.69);
    z-index: 1050;
    transition: all 0.4s ease-in-out;
    -moz-transition: all 0.4s ease-in-out;
    -webkit-transition: all 0.4s ease-in-out;
    margin: 0;
    padding: 0;
    pointer-events: auto;
}

.l-modal-body {
    padding: 15px;
    width: 600px;
    margin: 30px auto;
    border-radius: 5px;
    background: #fff;
    box-shadow: 0 3px 7px rgba(0, 0, 0, .25);
    -moz-box-shadow: 0 3px 7px rgba(0, 0, 0, .25);
    -webkit-box-shadow: 0 3px 7px rgba(0, 0, 0, .25);
    box-sizing: border-box;
    -moz-box-sizing: border-box;
    -webkit-box-sizing: border-box;
    max-height: calc(100vh - 40px);
    overflow-y: hidden;
}
.scroll {
    overflow: auto;
    max-height: calc(100vh - 70px);
    border-radius: 5px;
    background: #f2f2f2;
}
</style>