import App from './App'
import Vue from 'vue'
import './style/animat.css'
import common from "./utils/common";

import AppTitle from './components/base/app-title.vue';
import AppInput from './components/base/app-input.vue';
import AppContainer from './components/base/app-container.vue';
import AppButton from './components/base/app-button.vue';
import './style/common.css';

Vue.prototype.$common = common;
Vue.component('app-title', AppTitle);
Vue.component('app-input', AppInput);
Vue.component('app-container', AppContainer);
Vue.component('app-button', AppButton);

Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
    ...App
})
app.$mount()
