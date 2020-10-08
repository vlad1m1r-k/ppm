import MainBlock from './components/mainBlock.vue';
import loginForm from "./components/loginForm.vue";
import tokenProvider from "./tokenProvider/tokenProvider";

const vm = new Vue({
    el: '#app',
    components: {
        'main-block': MainBlock,
        'login-form': loginForm
    },
    data: {
        tokenProvider: tokenProvider
    }
});