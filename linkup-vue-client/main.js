import App from './App'
import Vue from 'vue'
import './style/common.css';
import './style/shadow.css';
import './style/animat.css'

import common from "./utils/common";
import AppTitle from './components/common/app-title.vue';
import AppInput from './components/common/app-input.vue';
import AppContainer from './components/common/app-container.vue';
import AppButton from './components/common/app-button.vue';
import UserList from './components/page/user-list/user-list.vue';

Vue.prototype.$common = common;
// common
Vue.component('app-title', AppTitle);
Vue.component('app-input', AppInput);
Vue.component('app-container', AppContainer);
Vue.component('app-button', AppButton);
// page
Vue.component('user-list', UserList);


Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
    ...App
})
app.$mount()
