export default {
    token: null,
    setToken(val) {
        this.token = val;
    },
    login(login, password, link) {
        link.message = "ERROR";
        Vue.cryptoProvider.tt2();
    }
}