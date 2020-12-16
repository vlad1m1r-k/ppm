<template>
    <div>
        <div class="decor">
            &#x1f512; &nbsp; {{ note.name }} &nbsp;
            <span class="btn-dc" @click="toggle" :title="language.data.cm1">&#x1f441;</span>
            <span class="btn-dc" v-show="text.length && access === 'RW'" :title="language.data.cm2" @click="edit = true">&#x1f589;</span>
            <span class="btn-dc" v-show="text.length && access === 'RW' && edit" :title="language.data.cm3" @click="save">&#x2705;</span>
            <span class="btn-dc" v-show="text.length && access === 'RW' && edit" :title="language.data.cm4" @click="cancel">&#x274c;</span>
            <span class="btn-dc float-right" v-show="access === 'RW'" :title="language.data.cm5" @click="remove">&#x1f5d1</span>
        </div>
        <input class="form-control" v-show="text.length && edit" v-model="name">
        <textarea class="form-control" rows="4" :readonly="!edit" v-show="text" v-model="text"></textarea>
    </div>
</template>

<script>
export default {
    name: "noteView",
    props: {
        note: Object,
        access: String
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            edit: false,
            name: "",
            text: ""
        }
    },
    methods: {
        toggle() {
            if (this.text.length) {
                this.text = "";
                this.edit = false;
                this.name = this.note.name;
            } else {
                this.loadNote();
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
        async save() {
            if (this.name && confirm(this.language.data.iv9)) {
                this.$eventHub.$emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await Vue.cryptoProvider.encrypt({
                        token: token,
                        note: this.note.id,
                        name: this.name,
                        text: this.text
                    });
                    const answer = await $.ajax({
                        url: "/container/editNote",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = Vue.cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.$eventHub.$emit("show-msg", this.language.data[data.message]);
                    }
                    this.edit = false;
                    this.$eventHub.$emit("update-tree");
                } catch (e) {
                    this.$eventHub.$emit("show-msg", Vue.errorParser(e));
                }
            }
        },
        cancel() {
            this.edit = false;
            this.name = this.note.name;
            this.loadNote();
        },
        async remove() {
            //TODO
        }
    },
    mounted() {
        this.name = this.note.name;
    }
}
</script>

<style scoped>
.decor {
    white-space: nowrap;
    border: 2px solid black;
    border-radius: 10px;
    padding-left: 5px;
}
.btn-dc {
    cursor: pointer;
    user-select: none;
}
.btn-dc:hover {
    background-color: darkgray;
    border-radius: 4px;
}
</style>