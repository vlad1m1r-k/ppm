!function(e){var t={};function n(r){if(t[r])return t[r].exports;var o=t[r]={i:r,l:!1,exports:{}};return e[r].call(o.exports,o,o.exports,n),o.l=!0,o.exports}n.m=e,n.c=t,n.d=function(e,t,r){n.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:r})},n.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},n.t=function(e,t){if(1&t&&(e=n(e)),8&t)return e;if(4&t&&"object"==typeof e&&e&&e.__esModule)return e;var r=Object.create(null);if(n.r(r),Object.defineProperty(r,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var o in e)n.d(r,o,function(t){return e[t]}.bind(null,o));return r},n.n=function(e){var t=e&&e.__esModule?function(){return e.default}:function(){return e};return n.d(t,"a",t),t},n.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},n.p="/",n(n.s=8)}([function(e,t,n){var r=n(5);"string"==typeof r&&(r=[[e.i,r,""]]),r.locals&&(e.exports=r.locals);(0,n(3).default)("3235b137",r,!1,{})},function(e,t,n){var r=n(7);"string"==typeof r&&(r=[[e.i,r,""]]),r.locals&&(e.exports=r.locals);(0,n(3).default)("5e8a3d18",r,!1,{})},function(e,t,n){"use strict";e.exports=function(e){var t=[];return t.toString=function(){return this.map((function(t){var n=function(e,t){var n=e[1]||"",r=e[3];if(!r)return n;if(t&&"function"==typeof btoa){var o=(i=r,s=btoa(unescape(encodeURIComponent(JSON.stringify(i)))),l="sourceMappingURL=data:application/json;charset=utf-8;base64,".concat(s),"/*# ".concat(l," */")),a=r.sources.map((function(e){return"/*# sourceURL=".concat(r.sourceRoot||"").concat(e," */")}));return[n].concat(a).concat([o]).join("\n")}var i,s,l;return[n].join("\n")}(t,e);return t[2]?"@media ".concat(t[2]," {").concat(n,"}"):n})).join("")},t.i=function(e,n,r){"string"==typeof e&&(e=[[null,e,""]]);var o={};if(r)for(var a=0;a<this.length;a++){var i=this[a][0];null!=i&&(o[i]=!0)}for(var s=0;s<e.length;s++){var l=[].concat(e[s]);r&&o[l[0]]||(n&&(l[2]?l[2]="".concat(n," and ").concat(l[2]):l[2]=n),t.push(l))}},t}},function(e,t,n){"use strict";function r(e,t){for(var n=[],r={},o=0;o<t.length;o++){var a=t[o],i=a[0],s={id:e+":"+o,css:a[1],media:a[2],sourceMap:a[3]};r[i]?r[i].parts.push(s):n.push(r[i]={id:i,parts:[s]})}return n}n.r(t),n.d(t,"default",(function(){return g}));var o="undefined"!=typeof document;if("undefined"!=typeof DEBUG&&DEBUG&&!o)throw new Error("vue-style-loader cannot be used in a non-browser environment. Use { target: 'node' } in your Webpack config to indicate a server-rendering environment.");var a={},i=o&&(document.head||document.getElementsByTagName("head")[0]),s=null,l=0,u=!1,c=function(){},d=null,p="undefined"!=typeof navigator&&/msie [6-9]\b/.test(navigator.userAgent.toLowerCase());function g(e,t,n,o){u=n,d=o||{};var i=r(e,t);return f(i),function(t){for(var n=[],o=0;o<i.length;o++){var s=i[o];(l=a[s.id]).refs--,n.push(l)}t?f(i=r(e,t)):i=[];for(o=0;o<n.length;o++){var l;if(0===(l=n[o]).refs){for(var u=0;u<l.parts.length;u++)l.parts[u]();delete a[l.id]}}}}function f(e){for(var t=0;t<e.length;t++){var n=e[t],r=a[n.id];if(r){r.refs++;for(var o=0;o<r.parts.length;o++)r.parts[o](n.parts[o]);for(;o<n.parts.length;o++)r.parts.push(h(n.parts[o]));r.parts.length>n.parts.length&&(r.parts.length=n.parts.length)}else{var i=[];for(o=0;o<n.parts.length;o++)i.push(h(n.parts[o]));a[n.id]={id:n.id,refs:1,parts:i}}}}function v(){var e=document.createElement("style");return e.type="text/css",i.appendChild(e),e}function h(e){var t,n,r=document.querySelector('style[data-vue-ssr-id~="'+e.id+'"]');if(r){if(u)return c;r.parentNode.removeChild(r)}if(p){var o=l++;r=s||(s=v()),t=b.bind(null,r,o,!1),n=b.bind(null,r,o,!0)}else r=v(),t=x.bind(null,r),n=function(){r.parentNode.removeChild(r)};return t(e),function(r){if(r){if(r.css===e.css&&r.media===e.media&&r.sourceMap===e.sourceMap)return;t(e=r)}else n()}}var m,y=(m=[],function(e,t){return m[e]=t,m.filter(Boolean).join("\n")});function b(e,t,n,r){var o=n?"":r.css;if(e.styleSheet)e.styleSheet.cssText=y(t,o);else{var a=document.createTextNode(o),i=e.childNodes;i[t]&&e.removeChild(i[t]),i.length?e.insertBefore(a,i[t]):e.appendChild(a)}}function x(e,t){var n=t.css,r=t.media,o=t.sourceMap;if(r&&e.setAttribute("media",r),d.ssrId&&e.setAttribute("data-vue-ssr-id",t.id),o&&(n+="\n/*# sourceURL="+o.sources[0]+" */",n+="\n/*# sourceMappingURL=data:application/json;base64,"+btoa(unescape(encodeURIComponent(JSON.stringify(o))))+" */"),e.styleSheet)e.styleSheet.cssText=n;else{for(;e.firstChild;)e.removeChild(e.firstChild);e.appendChild(document.createTextNode(n))}}},function(e,t,n){"use strict";var r=n(0);n.n(r).a},function(e,t,n){(t=n(2)(!1)).push([e.i,"\n.blur[data-v-0d318d31] {\n    filter: blur(5px);\n}\n",""]),e.exports=t},function(e,t,n){"use strict";var r=n(1);n.n(r).a},function(e,t,n){(t=n(2)(!1)).push([e.i,"\n.l-modal[data-v-758f7bac] {\n    left: 0;\n    top: 0;\n    right: 0;\n    bottom: 0;\n    position: fixed;\n    background: rgba(0, 0, 0, 0.69);\n    z-index: 1050;\n    transition: all 0.4s ease-in-out;\n    -moz-transition: all 0.4s ease-in-out;\n    -webkit-transition: all 0.4s ease-in-out;\n    margin: 0;\n    padding: 0;\n    pointer-events: auto;\n}\n.l-modal-body[data-v-758f7bac] {\n    padding: 15px;\n    width: 400px;\n    margin: 30px auto;\n    border-radius: 5px;\n    background: #fff;\n    box-shadow: 0 3px 7px rgba(0, 0, 0, .25);\n    -moz-box-shadow: 0 3px 7px rgba(0, 0, 0, .25);\n    -webkit-box-shadow: 0 3px 7px rgba(0, 0, 0, .25);\n    box-sizing: border-box;\n    -moz-box-sizing: border-box;\n    -webkit-box-sizing: border-box;\n}\n",""]),e.exports=t},function(e,t,n){"use strict";n.r(t);var r=function(){var e=this.$createElement,t=this._self._c||e;return t("div",{class:{blur:null===this.tokenProvider.token}},[this._v("\n    Page Content\n    "),t("br"),this._v(" "),t("language-selector",{attrs:{"lang-name":this.lang.name},on:{"language-changed":this.setLang}})],1)};r._withStripped=!0;var o=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",[n("select",{directives:[{name:"model",rawName:"v-model",value:e.selectedLang,expression:"selectedLang"}],on:{change:[function(t){var n=Array.prototype.filter.call(t.target.options,(function(e){return e.selected})).map((function(e){return"_value"in e?e._value:e.value}));e.selectedLang=t.target.multiple?n:n[0]},function(t){return e.$emit("language-changed",e.selectedLang)}]}},[n("option",{attrs:{value:"en-US"}},[e._v("En")]),e._v(" "),n("option",{attrs:{value:"uk-UA"}},[e._v("Ua")])])])};function a(e,t,n,r,o,a,i,s){var l,u="function"==typeof e?e.options:e;if(t&&(u.render=t,u.staticRenderFns=n,u._compiled=!0),r&&(u.functional=!0),a&&(u._scopeId="data-v-"+a),i?(l=function(e){(e=e||this.$vnode&&this.$vnode.ssrContext||this.parent&&this.parent.$vnode&&this.parent.$vnode.ssrContext)||"undefined"==typeof __VUE_SSR_CONTEXT__||(e=__VUE_SSR_CONTEXT__),o&&o.call(this,e),e&&e._registeredComponents&&e._registeredComponents.add(i)},u._ssrRegister=l):o&&(l=s?function(){o.call(this,(u.functional?this.parent:this).$root.$options.shadowRoot)}:o),l)if(u.functional){u._injectStyles=l;var c=u.render;u.render=function(e,t){return l.call(t),c(e,t)}}else{var d=u.beforeCreate;u.beforeCreate=d?[].concat(d,l):[l]}return{exports:e,options:u}}o._withStripped=!0;var i=a({name:"languageSelector",props:{"lang-name":String},data:()=>({selectedLang:""}),watch:{langName(e){this.selectedLang=e}}},o,[],!1,null,"610f1eec",null);i.options.__file="src/main/Vue/components/languageSelector.vue";var s={name:"mainBlock",components:{languageSelector:i.exports},data(){return{tokenProvider:this.$root.$data.tokenProvider,lang:this.$root.$data.language}},methods:{setLang(e){document.cookie="lang="+e+"; expires=Fri, 31 Dec 9999 23:59:59 GMT; samesite=strict",this.$root.loadLanguage(e)}}},l=(n(4),a(s,r,[],!1,null,"0d318d31",null));l.options.__file="src/main/Vue/components/mainBlock.vue";var u=l.exports,c=function(){var e=this,t=e.$createElement,n=e._self._c||t;return null===e.tokenProvider.token?n("div",{staticClass:"l-modal"},[n("div",{staticClass:"l-modal-body"},[e.message?n("div",{staticClass:"alert alert-danger",on:{click:function(t){e.message=""}}},[e._v("\n            "+e._s(e.message)+"\n        ")]):e._e(),e._v(" "),n("div",{staticClass:"form-group"},[n("input",{directives:[{name:"model",rawName:"v-model",value:e.login,expression:"login"}],staticClass:"form-control",attrs:{type:"text",placeholder:e.lang.data.lf1},domProps:{value:e.login},on:{input:function(t){t.target.composing||(e.login=t.target.value)}}})]),e._v(" "),n("div",{staticClass:"form-group"},[n("input",{directives:[{name:"model",rawName:"v-model",value:e.password,expression:"password"}],staticClass:"form-control",attrs:{type:"password",placeholder:e.lang.data.lf2},domProps:{value:e.password},on:{keypress:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.doLogin(t)},input:function(t){t.target.composing||(e.password=t.target.value)}}})]),e._v(" "),n("div",{staticClass:"container"},[n("div",{staticClass:"row justify-content-md-center"},[n("button",{staticClass:"btn btn-primary",on:{click:e.doLogin}},[e._v(e._s(e.lang.data.lf3))])])])])]):e._e()};c._withStripped=!0;var d={name:"loginForm",data(){return{tokenProvider:this.$root.tokenProvider,lang:this.$root.$data.language,message:"",login:"",password:""}},methods:{doLogin(){this.login.length<1||(this.message="",this.tokenProvider.login(this.login,this.password,this.$data),this.password="")}}},p=(n(6),a(d,c,[],!1,null,"758f7bac",null));p.options.__file="src/main/Vue/components/loginForm.vue";var g=p.exports,f={token:null,setToken(e){this.token=e},login(e,t,n){let r={login:"",password:""};r.login=e,r.password=t;Vue.cryptoProvider.encrypt(r)}},v={serverKeyExpireDate:null,serverPublicKey:null,frontKeyPair:null,encrypt(e){this.checkKey(),e.publicKey={},e.publicKey.n=forge.util.encode64(this.frontKeyPair.publicKey.n.data.toString()),e.publicKey.e=forge.util.encode64(this.frontKeyPair.publicKey.e.data.toString());const t=forge.random.getBytesSync(32),n=forge.random.getBytesSync(16),r=forge.cipher.createCipher("AES-CBC",t);r.start({iv:n}),r.update(forge.util.createBuffer(JSON.stringify(e))),r.finish();forge.util.encode64(t),forge.util.encode64(n);const o=forge.pki.rsa.setPublicKey(this.serverPublicKey.m,this.serverPublicKey.e).encrypt("Hello World!!!");console.log(o.length),$.ajax({url:"/crypto/testAES",type:"post",data:{key:forge.util.encode64(o),data:forge.util.encode64(r.output.data)}})},checkKey(){(null===this.serverPublicKey||null===this.serverKeyExpireDate||this.serverKeyExpireDate<Date.now())&&this.getPublicKey()},getPublicKey(){const e=this;$.ajax({url:"/crypto/getKey",async:!1,success:t=>{const n=forge.util.bytesToHex(forge.util.decode64(t.modulus)),r=forge.util.bytesToHex(forge.util.decode64(t.exponent));e.serverPublicKey={m:new forge.jsbn.BigInteger(n,16),e:new forge.jsbn.BigInteger(r,16)},e.serverKeyExpireDate=t.keyPairExpireDate},error:e=>{console.log("Error getting public key "+e.responseJSON.status+" "+e.responseJSON.error+" "+e.responseJSON.message)}})},init(){forge.pki.rsa.generateKeyPair({bits:2048,e:65537,workers:2},(e,t)=>{this.frontKeyPair=t})}};Vue.use({install(e){e.cryptoProvider=v}}),Vue.cryptoProvider.init(),new Vue({el:"#app",components:{"main-block":u,"login-form":g},data:{tokenProvider:f,language:{name:"",data:{}},langLoadStatus:!1},methods:{getPreferredLang(){let e=(document.cookie.match("lang=(.+?)(;|$)")||[])[1]||"";return""===e?navigator.language:e},loadLanguage(e){const t=this;$.getJSON("/language/"+e+".json",e=>{t.language.name=e.name,t.language.data=e.data}).fail(e=>{if(404===e.status){if(t.langLoadStatus)return void console.log("Error loading language.");t.langLoadStatus=!0,t.loadLanguage("en-US")}})}},mounted(){this.loadLanguage(this.getPreferredLang())}})}]);