export default {
    lifeTime: null,
    renewTime: null,
    token: null,
    userName: null,
    adminSettings: false,
    async login(login, password) {
        let data = {login: "", password: ""};
        data.login = login;
        data.password = password;
        const encryptedData = await Vue.cryptoProvider.encrypt(data);
        const answer = await $.ajax({
            url: "/user/login",
            method: "POST",
            data: encryptedData
        });
        const decryptedAnswer = Vue.cryptoProvider.decrypt(answer);
        if (decryptedAnswer.message) {
            throw new Error(decryptedAnswer.message);
        }
        this.lifeTime = decryptedAnswer.lifeTime;
        this.token = decryptedAnswer.token;
        this.adminSettings = decryptedAnswer.adminSettings;
        this.userName = login;
        this.renewTime = Date.now() + (decryptedAnswer.lifeTime - Date.now()) / 2;
        return decryptedAnswer.systemClosed;
    },
    logout() {
        this.token = null;
    },
    async getToken() {
        if (this.token !== null && Date.now() > this.renewTime && Date.now() < this.lifeTime) {
            await this.renewToken(this.token);
        }
        if (this.token === null || Date.now() > this.lifeTime) {
            this.token = null;
            while (this.token === null) {
                await (() => new Promise((resolve) => setTimeout(resolve, 1000)))();
            }
        }
        return this.token;
    },
    async renewToken(token) {
        const data = {token: token};
        const encryptedData = await Vue.cryptoProvider.encrypt(data);
        const answer = await $.ajax({
            url: "/user/renewToken",
            method: "POST",
            data: {
                key: encryptedData.key,
                data: encryptedData.data
            }
        });
        const decryptedAnswer = Vue.cryptoProvider.decrypt(answer);
        this.lifeTime = decryptedAnswer.lifeTime;
        this.token = decryptedAnswer.token;
        this.adminSettings = decryptedAnswer.adminSettings;
        this.renewTime = Date.now() + (decryptedAnswer.lifeTime - Date.now()) / 2;
    }
}