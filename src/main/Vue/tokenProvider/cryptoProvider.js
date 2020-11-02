export default {
    serverKeyExpireDate: null,
    serverPublicKey: null,
    frontKeyPair: null,

    encrypt(data) {
        this.checkKey();
        data.publicKey = forge.pki.publicKeyToPem(this.frontKeyPair.publicKey);
        const aesKey = forge.random.getBytesSync(32);
        const aesIv = forge.random.getBytesSync(16);
        const cipher = forge.cipher.createCipher("AES-CBC", aesKey);
        cipher.start({iv: aesIv});
        cipher.update(forge.util.createBuffer(JSON.stringify(data)));
        cipher.finish();

        const aesKeyBundle = {key: forge.util.encode64(aesKey), iv: forge.util.encode64(aesIv)};
        const srvPubKey = forge.pki.publicKeyFromPem(this.serverPublicKey);
        const encryptedAESKey = srvPubKey.encrypt(JSON.stringify(aesKeyBundle));

        $.ajax({
            url: "/crypto/testAES",
            type: "post",
            data: {
                key: forge.util.encode64(encryptedAESKey),
                data: forge.util.encode64(cipher.output.data)
            }
        });

        // let decipher = forge.cipher.createDecipher("AES-CBC", aesKey);
        // decipher.start({iv: aesIv});
        // decipher.update(cipher.output);
        // decipher.finish();
        // let dcp = decipher.output;
        // console.log(dcp.data);
        //TODO ========
    },
    checkKey() {
        if (this.serverPublicKey === null || this.serverKeyExpireDate === null || this.serverKeyExpireDate < Date.now()) {
            this.getPublicKey();
        }
    },
    getPublicKey() {
        const component = this;
        $.ajax({
            url: "/crypto/getKey",
            async: false,
            success: (data) => {
                component.serverPublicKey = data.keyPEM;
                component.serverKeyExpireDate = data.keyPairExpireDate;
            },
            error: (error) => {
                console.log('Error getting public key ' + error.responseJSON.status + ' ' + error.responseJSON.error + ' ' + error.responseJSON.message)
            }
        })
    },
    init() {
        forge.pki.rsa.generateKeyPair({bits: 2048, e: 0x10001, workers: 2}, (err, keypair) => {
            this.frontKeyPair = keypair;
        })
    }
}