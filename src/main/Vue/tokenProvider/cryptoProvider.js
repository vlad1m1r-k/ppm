export default {
    serverKeyExpireDate: null,
    serverPublicKey: null,
    frontKeyPair: null,

    encrypt(data) {
        this.checkKey();
        let aesKey = forge.random.getBytesSync(32);
        let aesIv = forge.random.getBytesSync(32);
        let cipher = forge.cipher.createCipher("AES-CBC", aesKey);
        cipher.start({iv: aesIv});
        cipher.update(forge.util.createBuffer(data.toString()));
        cipher.finish();
        console.log(cipher.output);


        let decipher = forge.cipher.createDecipher("AES-CBC", aesKey);
        decipher.start({iv: aesIv});
        decipher.update(cipher.output);
        decipher.finish();
        console.log(forge.util.decode64(decipher.output.data));
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
                component.serverPublicKey = data.publicKey;
                component.serverKeyExpireDate = data.keyPairExpireDate;
            },
            error: (error) => {
                console.log('Error getting public key ' + error.responseJSON.status + ' ' + error.responseJSON.error + ' ' + error.responseJSON.message)
            }
        })
    },
    init() {
        // this.frontKeyPair = forge.pki.rsa.generateKeyPair(2048, 0x10001);
        forge.pki.rsa.generateKeyPair({bits: 2048, e: 0x10001, workers: 2}, (err, keypair) => {
            this.frontKeyPair = keypair;
        })
    }
}