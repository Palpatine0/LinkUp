import App from './App'
import Vue from 'vue'
import VueI18n from 'vue-i18n'
import globalMixin from './utils/globalMixin'

import common from "./utils/common";
import API from "./api/api";
import messages from './locale/index.js'

import './style/common.css';
import './style/animat.css'

import AppTitle from './components/common/app-title.vue';
import AppInput from './components/common/app-input.vue';
import AppContainer from './components/common/app-container.vue';
import AppButton from './components/common/app-button.vue';
import UserList from './components/page/user-list/user-list.vue';


// common
Vue.component('app-title', AppTitle);
Vue.component('app-input', AppInput);
Vue.component('app-container', AppContainer);
Vue.component('app-button', AppButton);
// page
Vue.component('user-list', UserList);
Vue.mixin(globalMixin)

Vue.prototype.$common = common;
Vue.prototype.$API = API;
Vue.use(VueI18n)
Vue.config.productionTip = false

// i18n
let i18nConfig = {
    locale: uni.getLocale(),
    messages
}
const i18n = new VueI18n(i18nConfig)

const app = new Vue({
    ...App,
    i18n
})
App.mpType = 'app'
app.$mount()
