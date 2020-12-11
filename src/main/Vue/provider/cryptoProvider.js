export default {
    serverKeyExpireDate: null,
    serverPublicKey: null,
    frontKeyPair: null,

    async encrypt(data) {
        await this.checkKey();
        if (this.serverPublicKey) {
            data.publicKey = forge.pki.publicKeyToPem(this.frontKeyPair.publicKey);
            const aesKey = forge.random.getBytesSync(32);
            const aesIv = forge.random.getBytesSync(16);
            const cipher = forge.cipher.createCipher("AES-CBC", aesKey);
            cipher.start({iv: aesIv});
            cipher.update(forge.util.createBuffer(JSON.stringify(data), "utf8"));
            cipher.finish();

            const aesKeyBundle = {key: forge.util.encode64(aesKey), iv: forge.util.encode64(aesIv)};
            const srvPubKey = forge.pki.publicKeyFromPem(this.serverPublicKey);
            const encryptedAESKey = srvPubKey.encrypt(JSON.stringify(aesKeyBundle));

            return {
                key: forge.util.encode64(encryptedAESKey),
                data: forge.util.encode64(cipher.output.data)
            }
        }

    },
    decrypt(data) {
        const aesKeyBytes = forge.util.decode64(data.key);
        const aesKeyBundle = JSON.parse(this.frontKeyPair.privateKey.decrypt(aesKeyBytes));
        const key = forge.util.decode64(aesKeyBundle.key);
        const iv = forge.util.decode64(aesKeyBundle.iv);
        const decipher = forge.cipher.createDecipher("AES-CBC", key);
        decipher.start({iv: iv});
        decipher.update(forge.util.createBuffer(forge.util.decode64(data.data)));
        decipher.finish();
        return JSON.parse(forge.util.decodeUtf8(decipher.output.data));
    },
    async checkKey() {
        if (this.serverPublicKey === null || this.serverKeyExpireDate === null || this.serverKeyExpireDate < Date.now()) {
            await this.getPublicKey();
        }
    },
    async getPublicKey() {
        const component = this;
        try {
            const data = await $.ajax({
                url: "/crypto/getKey"
            });
            component.serverPublicKey = data.keyPEM;
            component.serverKeyExpireDate = data.keyPairExpireDate;
        } catch (error) {
            console.log('Error getting public key ' + error.responseJSON.status + ' ' + error.responseJSON.error + ' ' + error.responseJSON.message);
        }
    },
    init() {
        forge.pki.rsa.generateKeyPair({bits: 2048, e: 0x10001, workers: 2}, (err, keypair) => {
            this.frontKeyPair = keypair;
        })
    }
}