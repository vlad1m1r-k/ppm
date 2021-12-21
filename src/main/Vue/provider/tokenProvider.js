export default {
    lifeTime: null,
    renewTime: null,
    token: null,
    userName: null,
    adminSettings: false,
    changePwd: false,
    async login(login, password) {
        let data = {login: "", password: ""};
        data.login = login;
        data.password = password;
        const encryptedData = await cryptoProvider.encrypt(data);
        const answer = await $.ajax({
            url: "/user/login",
            method: "POST",
            data: encryptedData
        });
        const decryptedAnswer = cryptoProvider.decrypt(answer);
        if (decryptedAnswer.message) {
            throw new Error(decryptedAnswer.message);
        }
        this.userName = login;
        this.setToken(decryptedAnswer);
        return decryptedAnswer.systemClosed;
    },
    logout() {
        this.token = null;
    },
    async getToken(any) {
        if (this.token !== null && Date.now() > this.renewTime && Date.now() < this.lifeTime) {
            await this.renewToken(this.token);
        }
        if (this.token === null || Date.now() > this.lifeTime) {
            this.token = null;
            while (this.token === null) {
                await (() => new Promise((resolve) => setTimeout(resolve, 1000)))();
            }
        }
        if (this.changePwd && !any) {
            while (this.changePwd) {
                await (() => new Promise((resolve) => setTimeout(resolve, 1000)))();
            }
        }
        return this.token;
    },
    async renewToken(token) {
        const data = {token: token};
        const encryptedData = await cryptoProvider.encrypt(data);
        const answer = await $.ajax({
            url: "/user/renewToken",
            method: "POST",
            data: {
                key: encryptedData.key,
                data: encryptedData.data
            }
        });
        const decryptedAnswer = cryptoProvider.decrypt(answer);
        this.setToken(decryptedAnswer);
    },
    setToken(token) {
        this.lifeTime = token.lifeTime;
        this.token = token.token;
        this.adminSettings = token.adminSettings;
        this.renewTime = Date.now() + (token.lifeTime - Date.now()) / 2;
        this.changePwd = token.changePwd;
    }
}