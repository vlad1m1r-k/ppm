export default {
    token: null,
    setToken(val) {
        this.token = val;
    },
    login(login, password, link) {
        let data = {login: "", password: ""};
        data.login = login;
        data.password = password;
        let encryptedData = Vue.cryptoProvider.encrypt(data);
        password = "";
        data = null;
        //TODO login
    }
}