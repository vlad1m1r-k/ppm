export default {
    lifeTime: null,
    renewTime: null,
    token: null,
    userName: null,
    adminSettings: false,
    changePwd: false,
    tfaRequired: false,
    tfaSetup: false,
    tfaQrCode: null,
    isTokenInvalid: false,
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
        if (this.isTokenInvalid) this.logout();
        if (this.token !== null && Date.now() > this.renewTime && Date.now() < this.lifeTime) {
            await this.renewToken(this.token);
        }
        if (this.token === null || Date.now() > this.lifeTime) {
            this.token = null;
            while (this.token === null) {
                await (() => new Promise((resolve) => setTimeout(resolve, 1000)))();
            }
        }
        if ((this.changePwd || this.tfaRequired || this.tfaSetup) && !any) {
            while (this.changePwd || this.tfaRequired || this.tfaSetup) {
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
    invalidToken() {
        this.isTokenInvalid = true;
    },
    setToken(token) {
        this.lifeTime = token.lifeTime;
        this.adminSettings = token.adminSettings;
        this.renewTime = Date.now() + (token.lifeTime - Date.now()) / 2;
        this.changePwd = token.changePwd;
        this.tfaRequired = token.tfaRequired;
        this.tfaSetup = token.tfaSetup;
        this.tfaQrCode = token.tfaQrCode;
        this.isTokenInvalid = false;
        this.token = token.token;
    },
    async verifyTfaCode(code) {
        const data = {token: this.token, tfaCode: code};
        const encryptedData = await cryptoProvider.encrypt(data);
        const answer = await $.ajax({
            url: "/user/verifyTfaCode",
            method: "POST",
            data: encryptedData
        });
        const decryptedAnswer = cryptoProvider.decrypt(answer);
        if (decryptedAnswer.message) {
            throw new Error(decryptedAnswer.message);
        }
        this.setToken(decryptedAnswer);
    }
}