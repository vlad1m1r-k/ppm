<template>
    <div>
        <div class="decor">
            &#x1f512; &nbsp; {{ pwd.name }} &nbsp;
            <span class="btn-dc" @click="toggle" :title="language.data.cm1">&#x1f441;</span>
            <span class="btn-dc" v-show="show && access === 'RW'" :title="language.data.cm2" @click="edit = true">&#x1f589;</span>
            <span class="btn-dc" v-show="show && access === 'RW' && edit" :title="language.data.cm3" @click="save">&#x2705;</span>
            <span class="btn-dc" v-show="show && access === 'RW' && edit" :title="language.data.cm4" @click="cancel">&#x274c;</span>
            <span class="btn-dc float-right" v-show="access === 'RW'" :title="language.data.cm5" @click="remove">&#x1f5d1</span>
        </div>
        <input class="form-control" v-show="show && edit" v-model="name">
        <input class="form-control" v-show="show" v-model="login" :readonly="!edit">
        <input class="form-control" v-show="show" v-model="pass" :readonly="!edit">
        <textarea class="form-control" rows="4" :readonly="!edit" v-show="show" v-model="note"></textarea>
    </div>
</template>

<script>
export default {
    name: "pwdView",
    props: {
        pwd: Object,
        access: String
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            show: false,
            edit: false,
            name: "",
            login: "",
            pass: "***",
            note: ""
        }
    },
    methods: {
        toggle() {
            if (this.show) {
                this.show = false;
                this.edit = false;
                this.name = this.note.name;
                this.login = "";
                this.pass = "***";
                this.note = "";
            } else {
                this.loadPwdEnv();
                this.show = true;
            }
        },
        loadPwdEnv() {

        },
        save() {

        },
        cancel() {

        },
        remove() {
            //TODO
        }
    },
    mounted() {
        this.name = this.pwd.name;
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