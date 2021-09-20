<template>
    <tr>
        <td>
            <input type="checkbox" :value="note.id" v-model="$parent.$data.checkedNotes">
        </td>
        <td><span class="btn-link cursor-pointer" @click="toggle" :title="language.data.cm1">{{ note.name }}</span></td>
        <td>{{ note.createdDate }}</td>
        <td>{{ note.createdBy }}</td>
        <td>{{ note.editedDate }}</td>
        <td>{{ note.editedBy }}</td>
        <td>{{ note.deletedDate }}</td>
        <td>{{ note.deletedBy }}</td>
        <td>
            <span class="btn-dc text-success" :title="language.data.cm7" @click="restore">&#x21ba;</span>
        </td>
        <td>
            <span class="btn-dc" :title="language.data.cm5" @click="remove">&#x1f5d1;</span>
        </td>
    </tr>
    <tr v-show="show">
        <td colspan="15">
            <textarea class="form-control" rows="4" readonly v-model="text"></textarea>
        </td>
    </tr>
</template>

<script>
export default {
    name: "noteView",
    props: {
        note: Object
    },
    emits: ['update-items'],
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
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    note: this.note.id,
                });
                const answer = await $.ajax({
                    url: "/container/getNote",
                    method: "POST",
                    data: encryptedData
                });
                const data = cryptoProvider.decrypt(answer);
                this.text = data.message;
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async remove(event, mass) {
            if (mass || confirm(this.language.data.iv10 + this.note.name + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        note: this.note.id,
                        permanent: true
                    });
                    const answer = await $.ajax({
                        url: "/container/removeNote",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    } else {
                        this.$emit("update-items");
                    }
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        async restore(event, mass) {
            if (mass || confirm(this.language.data.cm7 + " " + this.note.name + "?")) {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        noteId: this.note.id
                    });
                    const answer = await $.ajax({
                        url: "/container/restoreNote",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    } else {
                        this.$emit("update-items");
                        this.eventHub.emit("update-tree");
                    }
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        removeNotes(notes) {
            if (notes.includes(this.note.id)) {
                this.remove(null, true);
            }
        },
        restoreNotes(notes) {
            if (notes.includes(this.note.id)) {
                this.restore(null, true);
            }
        }
    },
    mounted() {
        this.eventHub.on("remove-notes", this.removeNotes);
        this.eventHub.on("restore-notes", this.restoreNotes);
    },
    beforeUnmount() {
        this.eventHub.off("remove-notes", this.removeNotes);
        this.eventHub.off("restore-notes", this.restoreNotes);
    }
}
</script>

<style scoped>
</style>