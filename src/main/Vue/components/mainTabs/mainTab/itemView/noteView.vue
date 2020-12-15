<template>
    <div>
        <div class="decor">
            &#x1f512; &nbsp; {{ note.name }} &nbsp;
            <span class="btn-dc" @click="toggle">&#x1f441</span>
            <span class="btn-dc" v-show="text.length && access === 'RW'">&#x1f589</span>
            <span class="btn-dc" v-show="text.length && access === 'RW'">&#x274c</span>
        </div>
        <textarea class="form-control" rows="4" readonly v-show="text" v-model="text"></textarea>
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
            text: ""
        }
    },
    methods: {
        toggle() {
            if (this.text.length) {
                this.text = "";
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
        }
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