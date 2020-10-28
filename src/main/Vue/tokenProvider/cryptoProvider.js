export default {
    keyExpireDate: null,
    publicKey: null,

    encrypt(data) {
        this.checkKey();

        //TODO ========
    },
    checkKey() {
        if (this.publicKey === null || this.keyExpireDate === null || this.keyExpireDate < Date.now()) {
            this.getPublicKey();
        }
    },
    getPublicKey() {
        const component = this;
        $.ajax({
            url: "/crypto/getKey",
            async: false,
            success: (data) => {
                component.publicKey = data.publicKey;
                component.keyExpireDate = data.keyPairExpireDate;
            },
            error: (error) => {
                console.log('Error getting public key ' + error.responseJSON.status + ' ' + error.responseJSON.error + ' ' + error.responseJSON.message)
            }
        })
    },

}