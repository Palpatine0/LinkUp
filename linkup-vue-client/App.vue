<script>
export default {
    globalData: {
        needsTopPadding: null,
        // dev
        requestUrl: "http://localhost:8090",
        // prd
        // request_url:"http://111.231.19.137:8090"

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
        uni.authorize({
            scope: 'scope.userInfo',
            success() {
            }
        })
        wx.authorize({
            scope: 'scope.userLocation',
            success: function () {
            }
        })
        wx.authorize({
            scope: 'scope.chooseLocation',
            success: function () {
            }
        })
        wx.getSystemInfo({
            success: (res) => {
                if (res.safeArea.top > 40) {
                    this.globalData.needsTopPadding = true
                } else {
                    this.globalData.needsTopPadding = false
                }
            }
        })
    },
    onLoad() {
        const lang = uni.getStorageSync('language') || 'en';//获取缓存的语言设置
        this.$i18n.locale = lang
        uni.setLocale(lang);
        this.$store.commit('changeLanguage', lang);
        uni.onLocaleChange(e => {
            this.$store.commit('changeLanguage', e.locale);
        });
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
