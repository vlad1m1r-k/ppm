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
        $.ajax({
            url: "/user/login",
            method: "POST",
            data: {
                key: encryptedData.key,
                data: encryptedData.data
            },
            success: (data) => {
                link.message = data;
                //TODO token decrypt
            },
            error: (error) => {
                link.message = Vue.errorParser(error);
            }
        })
    }
}