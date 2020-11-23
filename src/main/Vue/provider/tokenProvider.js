export default {
    lifeTime: null,
    token: null,
    userName: null,
    adminSettings: false,
    setToken(val) {
        this.token = val;
    },
    login(login, password, link) {
        let data = {login: "", password: ""};
        data.login = login;
        data.password = password;
        const encryptedData = Vue.cryptoProvider.encrypt(data);
        $.ajax({
            url: "/user/login",
            method: "POST",
            data: {
                key: encryptedData.key,
                data: encryptedData.data
            },
            success: (data) => {
                const decryptedData = Vue.cryptoProvider.decrypt(data);
                if (decryptedData.message) {
                    link.message = link.language.data[decryptedData.message];
                    return;
                }
                this.lifeTime = decryptedData.lifeTime;
                this.token = decryptedData.token;
                this.adminSettings = decryptedData.adminSettings;
                this.userName = login;
            },
            error: (error) => {
                link.message = Vue.errorParser(error);
            }
        })
    },
    logout() {
        this.token = null;
    },
    getToken() {
        //TODO
    }
}