<template>
    <div>
        <div class="decor">
            {{ note.name }} &nbsp;
            <span class="btn-dc" @click="toggle" :title="language.data.cm1">&#x1f441;</span>
            <span class="btn-dc float-right" :title="language.data.cm5" @click="remove">&#x1f5d1;</span>
            <div class="btn-dc float-right text-success" :title="language.data.cm7" @click="restore">&#x21ba;</div>
        </div>
        <textarea class="form-control" rows="4" readonly v-show="show" v-model="text"></textarea>
    </div>
</template>

<script>
export default {
    name: "noteView",
    props: {
        note: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            show: false,
            text: ""
        }
    },
    methods: {
        toggle() {
            if (this.show) {
                this.show = false;
                this.text = "";
            } else {
                this.loadNote();
                this.show = true;
            }
        },
        async loadNote() {
            this.$eventHub.$emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await Vue.cryptoProvider.encrypt({
                    token: token,
                    note: this.note.id,
                });
                const answer = await $.ajax({
                    url: "/container/getNote",
                    method: "POST",
                    data: encryptedData
                });
                const data = Vue.cryptoProvider.decrypt(answer);
                this.text = data.message;
            } catch (e) {
                this.$eventHub.$emit("show-msg", Vue.errorParser(e));
            }
        },
        async remove() {
            if (confirm(this.language.data.iv10 + this.note.name + "?")) {
                this.$eventHub.$emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await Vue.cryptoProvider.encrypt({
                        token: token,
                        note: this.note.id
                    });
                    const answer = await $.ajax({
                        url: "/container/removeNote",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = Vue.cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.$eventHub.$emit("show-msg", this.language.data[data.message]);
                    }
                    this.$emit("update-items");
                } catch (e) {
                    this.$eventHub.$emit("show-msg", Vue.errorParser(e));
                }
            }
        },
        async restore() {
            if (confirm(this.language.data.cm7 + " " + this.note.name + "?")) {
                this.$eventHub.$emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await Vue.cryptoProvider.encrypt({
                        token: token,
                        noteId: this.note.id
                    });
                    const answer = await $.ajax({
                        url: "/container/restoreNote",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = Vue.cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.$eventHub.$emit("show-msg", this.language.data[data.message]);
                    }
                    this.$emit("update-items");
                    this.$eventHub.$emit("update-tree");
                } catch (e) {
                    this.$eventHub.$emit("show-msg", Vue.errorParser(e));
                }
            }
        }
    }
}
</script>

<style scoped>
.decor {
    white-space: nowrap;
    border: 2px solid #ffab03;
    border-radius: 10px;
    padding-left: 5px;
}

.btn-dc {
    cursor: pointer;
    user-select: none;
    padding-left: 2px;
    padding-right: 2px;
}

.btn-dc:hover {
    background-color: darkgray;
    border-radius: 4px;
}
</style>