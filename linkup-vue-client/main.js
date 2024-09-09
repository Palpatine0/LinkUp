import App from './App'
import Vue from 'vue'
import './style/animat.css'

import AppText from './components/base/app-text.vue';
import AppInput from './components/base/app-input.vue';
import AppContainer from './components/base/app-container.vue';
import AppButton from './components/base/app-button.vue';
Vue.component('app-text', AppText);
Vue.component('app-input', AppInput);
Vue.component('app-container', AppContainer);
Vue.component('app-button', AppButton);


Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
    ...App
})
app.$mount()
