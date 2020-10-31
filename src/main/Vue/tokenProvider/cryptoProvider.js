export default {
    serverKeyExpireDate: null,
    serverPublicKey: null,
    frontKeyPair: null,

    encrypt(data) {
        this.checkKey();
        data.publicKey = {};
        data.publicKey.n = forge.util.encode64(this.frontKeyPair.publicKey.n.data.toString());
        data.publicKey.e = forge.util.encode64(this.frontKeyPair.publicKey.e.data.toString());
        const aesKey = forge.random.getBytesSync(32);
        const aesIv = forge.random.getBytesSync(16);
        const cipher = forge.cipher.createCipher("AES-CBC", aesKey);
        cipher.start({iv: aesIv});
        cipher.update(forge.util.createBuffer(JSON.stringify(data)));
        cipher.finish();

        const aesKeyBundle = {key: forge.util.encode64(aesKey), iv: forge.util.encode64(aesIv)};
        const srvPubKey = forge.pki.rsa.setPublicKey(this.serverPublicKey.m, this.serverPublicKey.e);
        // const encryptedAESKey = srvPubKey.encrypt(JSON.stringify(aesKeyBundle));
        const encryptedAESKey = srvPubKey.encrypt("Hello World!!!");
        console.log(encryptedAESKey.length);


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
                const hexModulus = forge.util.bytesToHex(forge.util.decode64(data.modulus));
                const hexExponent = forge.util.bytesToHex(forge.util.decode64(data.exponent));
                component.serverPublicKey = {
                    m: new forge.jsbn.BigInteger(hexModulus, 16),
                    e: new forge.jsbn.BigInteger(hexExponent, 16)
                };
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