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
      serverKeyLifeTime: 0,
      userTokenLifeTime: 0
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
      } catch (e) {
        this.eventHub.emit("show-msg", this.errorParser(e));
      }
    },
    async saveSettings() {
      if (confirm(this.language.data.as2) && this.serverKeyLifeTime > 0 && this.serverKeyLifeTime <= 366 && this.userTokenLifeTime > 0
          && this.userTokenLifeTime <= 59) {
        this.eventHub.emit("show-msg", "");
        try {
          const token = await this.tokenProvider.getToken();
          const encryptedData = await cryptoProvider.encrypt({
            token: token,
            serverKeyLifeTime: this.serverKeyLifeTime,
            tokenLifeTime: this.userTokenLifeTime,
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