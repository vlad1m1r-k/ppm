import MainBlock from './components/mainBlock.vue';
import loginForm from "./components/loginForm.vue";
import tokenProvider from "./provider/tokenProvider";
import cryptoProvider from "./provider/cryptoProvider";
import mitt from "mitt";

const app = Vue.createApp({
    components: {
        MainBlock,
        loginForm
    },
    data() {
        return {
            tokenProvider: tokenProvider,
            language: {
                name: "",
                data: {}
            },
            langLoadStatus: false
        }
    },
    methods: {
        getPreferredLang() {
            const lang = (document.cookie.match('lang=(.+?)(;|$)') || [])[1] || '';
            if (lang === "") {
                return navigator.language;
            }
            return lang;
        },
        loadLanguage(lang) {
            const component = this;
            $.getJSON("/language/" + lang + ".json",
                (data) => {
                    component.language.name = data.name;
                    component.language.data = data.data;
                }
            )
                .fail((error) => {
                    if (error.status === 404) {
                        if (component.langLoadStatus) {
                            console.log("Error loading language.");
                        } else {
                            component.langLoadStatus = true;
                            component.loadLanguage("en-US");
                        }
                    }
                })
        }
    },
    mounted() {
        this.loadLanguage(this.getPreferredLang());
    }
});

app.use({
    install(app) {
        app.cryptoProvider = cryptoProvider;
    }
});
app.use({
    install(app) {
        app.errorParser = function (error) {
            if (error.responseJSON) {
                return 'Error ' + error.responseJSON.status + ' ' + error.responseJSON.error + ' ' + error.responseJSON.message;
            }
            return 'Error ' + error.status;
        }
    }
});
app.config.globalProperties.eventHub = mitt();
app.cryptoProvider.init();
app.mount("#app");