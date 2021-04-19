<template>
    <table>
        <thead>

        </thead>
        <tbody>

        </tbody>
    </table>
</template>

<script>
export default {
    name: "users",
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            users: []
        }
    },
    methods: {
        async getUsers() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({token: token});
                const answer = await $.ajax({
                    url: "/user/getUsers",
                    method: "POST",
                    data: encryptedData
                });
                this.users = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        }
    },
    beforeMount() {
        this.getUsers();
    }
}
</script>

<style scoped>

</style>