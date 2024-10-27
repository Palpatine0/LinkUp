<script>
import app from "./App.vue";
import $common from "./utils/common";
import $API from "./api/api";

export default {
    globalData: {
        data: {
            // Dev
            // requestUrl: "https://localhost:8090",
            // socketUrl: "wss://localhost:8090",
            // QA
            requestUrl: "https://looking.cool:8090",
            socketUrl: "wss://looking.cool:8090",

            userLoginKey: "userLoginKey",
            userInfoKey: "userInfoKey",
            systemInfoKey: "systemInfoKey",

            colors: {
                primary: '#2676f7',
                success: '#67C23A',
                warning: '#E6A23C',
                danger: '#F56C6C',
                eucalyptus: '#44E1A6',
                info: '#939393',
                antiFlashWhite: '#f3f2f6',
                navyBlue: '#192C77',
                oxfordBlue: '#0A2342',
            }
        },

        async signIn() {
            const getUserLoginCode = () => {
                return new Promise((resolve) =>
                    uni.login({
                        provider: 'weixin',
                        success: (res) => {
                            resolve(res.code);
                        },
                        fail: () => {
                            uni.hideLoading();
                            uni.showToast({title: this.$t('app.showToast.codeAuthFail'), icon: 'none'});
                        },
                    })
                );
            };
            const userLoginCode = await getUserLoginCode();
            const getUserConfigData = () => {
                return new Promise((resolve) => {
                    uni.request({
                        url: getApp().globalData.data.requestUrl + $API.user.userConfig,
                        method: 'POST',
                        data: {
                            code: userLoginCode,
                            role: 1,
                        },
                        success: (res) => {
                            if(res.data.status == 200) {
                                resolve(res.data.data);
                            } else {
                                uni.hideLoading();
                                uni.showToast({title: this.$t('app.showToast.authFail'), icon: 'none'});
                            }
                        },
                    });
                });
            };
            const userConfigData = await getUserConfigData();
            if(userConfigData.isNewUser == "0") {
                const syncToStorage = () => {
                    return new Promise((resolve) => {
                        uni.request({
                            url: getApp().globalData.data.requestUrl + $API.user.search,
                            method: 'POST',
                            data: {
                                id: userConfigData.id,
                            },
                            success: (res) => {
                                if(!$common.isEmpty(res.data.list)) {
                                    uni.setStorageSync(app.globalData.data.userInfoKey, res.data.list[0]);
                                    uni.setStorageSync(app.globalData.data.userLoginKey, true);
                                    resolve(res)
                                } else {
                                    uni.showToast({title: this.$t('app.showToast.loginFail'), icon: 'none'});
                                }
                            },
                        });
                    });
                };
                await syncToStorage()

            } else if(userConfigData.isNewUser == "1") {
                uni.navigateTo({
                    url: `/pages/register/register?userConfigData=${encodeURIComponent(JSON.stringify(userConfigData))}`,
                });
            }
        },
        async signOut() {
            uni.removeStorageSync(app.globalData.data.userInfoKey);
            uni.removeStorageSync(app.globalData.data.userLoginKey);
        },

    },
    onShareAppMessage() {
        var shareTitle = "Come check this app!!";
        var referrerId = uni.getStorageSync(getApp().globalData.data.userInfoKey).id
        var sharePath = '/pages/home/home?referrerId=' + referrerId;
        let shareImg = "https://i.imghippo.com/files/BGqLk1727503992.jpg";
        return {
            title: shareTitle,
            path: sharePath,
            imageUrl: shareImg,
            success: function (res) {
            },
            fail: function (res) {
            }
        }
    },
    onLaunch() {

        // auth
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
        uni.authorize({
            scope: 'scope.userInfo',
            success: function () {
            }
        })

        // handle sys info
        uni.setStorageSync(app.globalData.data.systemInfoKey, uni.getSystemInfoSync());

        // language settings
        const systemInfo = uni.getStorageSync(app.globalData.data.systemInfoKey)
        const lang = systemInfo.appLanguage || 'en';
        this.$i18n.locale = lang
        uni.setLocale(lang);
        uni.setStorageSync('language', lang)
    },
    methods: {}
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
