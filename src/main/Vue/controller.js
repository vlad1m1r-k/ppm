import MainBlock from './components/mainBlock.vue';
import loginForm from "./components/loginForm.vue";
import tokenProvider from "./tokenProvider/tokenProvider";
import cryptoProvider from "./tokenProvider/cryptoProvider";

Vue.use({
    install(Vue) {
        Vue.cryptoProvider = cryptoProvider;
    }
});
Vue.use({
    install(Vue) {
        Vue.errorParser = function (error) {
            if (error.responseJSON) {
                return 'Error ' + error.responseJSON.status + ' ' + error.responseJSON.error + ' ' + error.responseJSON.message;
            }
            return 'Error ' + error.status;
        }
    }
});

Vue.cryptoProvider.init();

new Vue({
    el: '#app',
    components: {
        'main-block': MainBlock,
        'login-form': loginForm
    },
    data: {
        tokenProvider: tokenProvider,
        language: {
            name: "",
            data: {}
        },
        langLoadStatus: false
    },
    methods: {
        getPreferredLang() {
            let lang = (document.cookie.match('lang=(.+?)(;|$)') || [])[1] || '';
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
                            return;
                        }
                        component.langLoadStatus = true;
                        component.loadLanguage("en-US");
                    }
                })
        }
    },
    mounted() {
        this.loadLanguage(this.getPreferredLang());
    }
});