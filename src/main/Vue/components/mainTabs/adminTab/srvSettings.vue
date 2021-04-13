<template>
  <table>
    <thead></thead>
    <tbody>
    <tr>
      <td>{{ language.data.srv2 }}</td>
      <td>
        <input type="number" class="form-control-sm" min="1" max="366" v-model="serverKeyLifeTime" :title="language.data.srv4">
        1 - 366
      </td>
    </tr>
    <tr>
      <td>{{ language.data.srv3 }}</td>
      <td><input type="number" class="form-control-sm" min="1" max="59" v-model="userTokenLifeTime"> 1 - 59</td>
    </tr>
    <tr><td class="text-primary">{{ language.data.srv5 }}</td></tr>
    <tr>
      <td>{{ language.data.srv6 }}</td>
      <td><input type="number" class="form-control-sm" min="3" max="20" v-model="pwdMinLength"> 3 - 20</td>
    </tr>
    <tr>
      <td>{{ language.data.srv7 }}</td>
      <td><input type="checkbox" class="form-control-sm" v-model="pwdComplexity"></td>
    </tr>
    <tr>
      <td>{{ language.data.srv8 }}</td>
      <td><input type="checkbox" class="form-control-sm" v-model="pwdSpecialChar"></td>
    </tr>
    </tbody>
  </table>
  <button class="btn btn-sm btn-success" @click="saveSettings">{{ language.data.cm3 }}</button>
</template>

<script>
export default {
  name: "srvSettings",
  data() {
    return {
      tokenProvider: this.$root.$data.tokenProvider,
      language: this.$root.$data.language,
      serverKeyLifeTime: null,
      userTokenLifeTime: null,
      pwdMinLength: null,
      pwdComplexity: null,
      pwdSpecialChar: null
    }
  },
  methods: {
    async getSettings() {
      this.eventHub.emit("show-msg", "");
      try {
        const token = await this.tokenProvider.getToken();
        const encryptedData = await cryptoProvider.encrypt({token: token});
        const answer = await $.ajax({
          url: "/settings/getSettings",
          method: "POST",
          data: encryptedData
        });
        const data = cryptoProvider.decrypt(answer);
        this.serverKeyLifeTime = data.serverKeyLifeTimeDays;
        this.userTokenLifeTime = data.tokenLifeTimeMinutes;
        this.pwdMinLength = data.pwdMinLength;
        this.pwdComplexity = data.pwdComplexity;
        this.pwdSpecialChar = data.pwdSpecialChar;
      } catch (e) {
        this.eventHub.emit("show-msg", this.errorParser(e));
      }
    },
    async saveSettings() {
      if (confirm(this.language.data.as2) && this.serverKeyLifeTime > 0 && this.serverKeyLifeTime <= 366 && this.userTokenLifeTime > 0
          && this.userTokenLifeTime <= 59 && this.pwdMinLength > 2 && this.pwdMinLength <= 20 && this.pwdComplexity !== null
          && this.pwdSpecialChar !== null) {
        this.eventHub.emit("show-msg", "");
        try {
          const token = await this.tokenProvider.getToken();
          const encryptedData = await cryptoProvider.encrypt({
            token: token,
            serverKeyLifeTime: this.serverKeyLifeTime,
            tokenLifeTime: this.userTokenLifeTime,
            pwdMinLength: this.pwdMinLength,
            pwdComplexity: this.pwdComplexity,
            pwdSpecialChar: this.pwdSpecialChar
          });
          const answer = await $.ajax({
            url: "/settings/saveSettings",
            method: "POST",
            data: encryptedData
          });
          const data = cryptoProvider.decrypt(answer);
          if (data.message) {
            this.eventHub.emit("show-msg", this.language.data[data.message]);
          }
        } catch (e) {
          this.eventHub.emit("show-msg", this.errorParser(e));
        }
      }
    }
  },
  beforeMount() {
    this.getSettings();
  }
}
</script>

<style scoped>

</style>