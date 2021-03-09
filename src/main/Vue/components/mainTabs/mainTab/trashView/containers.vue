<template>
    <table class="table table-bordered table-striped table-sm">
        <thead class="tab-header-area">
        <tr>
            <th></th>
        </tr>
        </thead>
        <tbody>

        </tbody>
    </table>
    <div v-for="cont in containers">
        {{ cont.id }}
    </div>
</template>

<script>
import Sort from "../../../../sort";

export default {
    name: "containers",
    props: {
        item: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            containers: [],
            sort: new Sort("name", "desc"),
        }
    },
    methods: {
        async getContainers() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    sort: this.sort.toString()
                });
                const answer = await $.ajax({
                    url: "/container/getDeletedContainers",
                    method: "POST",
                    data: encryptedData
                });
                this.containers = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        }
    },
    mounted() {
        this.getContainers();
    }
}
</script>

<style scoped>

</style>