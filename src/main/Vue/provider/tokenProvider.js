export default {
    lifeTime: null,
    renewTime: null,
    token: null,
    userName: null,
    adminSettings: false,
    async login(login, password, link) {
        let data = {login: "", password: ""};
        data.login = login;
        data.password = password;
        const encryptedData = await Vue.cryptoProvider.encrypt(data);
        if (encryptedData) {
            await $.ajax({
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
                    this.renewTime = Date.now() + (decryptedData.lifeTime - Date.now()) / 2;
                },
                error: (error) => {
                    link.message = Vue.errorParser(error);
                }
            })
        }

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
        if (encryptedData) {
            await $.ajax({
                url: "/user/renewToken",
                method: "POST",
                data: {
                    key: encryptedData.key,
                    data: encryptedData.data
                },
                success: (data) => {
                    const decryptedData = Vue.cryptoProvider.decrypt(data);
                    if (decryptedData.message) {
                        throw new Error(decryptedData.message);
                    }
                    this.lifeTime = decryptedData.lifeTime;
                    this.token = decryptedData.token;
                    this.adminSettings = decryptedData.adminSettings;
                    this.renewTime = Date.now() + (decryptedData.lifeTime - Date.now()) / 2;
                }
            })
        }
    }
}