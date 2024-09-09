import App from './App'
import Vue from 'vue'
import './style/animat.css'



Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
    ...App
})
app.$mount()
