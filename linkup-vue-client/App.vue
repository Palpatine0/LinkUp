<script>
import app from "./App.vue";

export default {
    globalData: {
        // dev
        requestUrl: "http://localhost:8090",
        // prd
        // request_url:"http://111.231.19.137:8090"

        userLoginKey: "userLoginKey",
        userInfoKey: "userInfoKey",
        systemInfoKey: "systemInfoKey",

        colors: {
            primary: '#2676f7',
            success: '#67C23A',
            warning: '#E6A23C',
            danger: '#F56C6C',
            info: '#909399',
        }
    },
    onLaunch() {
        this.checkUserInfo();

        // auth
        uni.authorize({
            scope: 'scope.userInfo',
            success() {
            }
        })
        uni.authorize({
            scope: 'scope.userLocation',
            success: function () {
            }
        })
        uni.authorize({
            scope: 'scope.chooseLocation',
            success: function () {
            }
        })

        // handle sys info
        uni.setStorageSync(app.globalData.systemInfoKey, uni.getSystemInfoSync());

        // language settings
        const systemInfo = uni.getStorageSync(app.globalData.systemInfoKey)
        const lang = systemInfo.appLanguage || 'en';
        this.$i18n.locale = lang
        uni.setLocale(lang);
    },
    methods: {
        checkUserInfo() {
            const openid = uni.getStorageSync('openid');
            if (this.$common.isEmpty(openid)) {
                uni.navigateTo({
                    url: '/pages/registry/registry',
                });
            }
        },
    }
}
</script>

<style>
.uni-page-head .uni-page-head__title {
    font-size: 10rpx !important;
}

.uni-tabbar .uni-tabbar__icon {
    width: 25rpx !important;
    height: 25rpx !important;
}
</style>
