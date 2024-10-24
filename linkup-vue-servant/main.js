import App from './App'
import Vue from 'vue'
import VueI18n from 'vue-i18n'
import globalMixin from './utils/globalMixin'
import paginationMixin from './utils/paginationMixin';

import common from "./utils/common";
import messages from './locale/index.js'
import API from "./api/api";
import bus from "./bus/bus";

import './style/common.css';
import './style/animat.css'

import AppTitle from './components/common/base/app-title.vue';
import AppInput from './components/common/base/app-input.vue';
import AppContainer from './components/common/base/app-container.vue';
import AppButton from './components/common/base/app-button.vue';
import UserList from './components/common/user-list/user-list.vue';

// common
Vue.component('app-title', AppTitle);
Vue.component('app-input', AppInput);
Vue.component('app-container', AppContainer);
Vue.component('app-button', AppButton);
Vue.component('user-list', UserList);

Vue.mixin(globalMixin)
Vue.mixin(paginationMixin)

Vue.prototype.$common = common;
Vue.use(VueI18n)
Vue.prototype.$API = API;
Vue.prototype.$bus = bus;
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
