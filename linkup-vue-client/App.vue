<script>
import app from "./App.vue";
import common from "./utils/common";
import $common from "./utils/common";

export default {
    globalData: {
        data: {
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

        async signIn() {
            const getUserLoginCode = () => {
                return new Promise((resolve) =>
                    uni.login({
                        provider: 'weixin',
                        success: (res) => {
                            console.log("getUserLoginCode")
                            resolve(res.code);
                        },
                        fail: () => {
                            uni.showToast({title: '用户code获取失败', icon: 'none'});
                        },
                    })
                );
            };
            const userLoginCode = await getUserLoginCode();
            const getUserConfigData = () => {
                return new Promise((resolve) => {
                    uni.authorize({
                        scope: 'scope.userInfo',
                        success: function () {
                            uni.request({
                                url: getApp().globalData.data.requestUrl + '/user/user-config',
                                method: 'POST',
                                data: {
                                    code: userLoginCode,
                                    role: 1,
                                },
                                success: (res) => {
                                    if (res.data.code == 0) {
                                        resolve(res.data.data);
                                    } else {
                                        uni.showToast({title: '授权失败', icon: 'none'});
                                    }
                                },
                            });
                        },
                        fail: () => {
                            uni.showToast({title: '授权请求失败', icon: 'none'});
                        },
                    })

                });
            };
            const userConfigData = await getUserConfigData();
            console.log("userConfigData")
            console.log(userConfigData)
            if (userConfigData.isNewUser == "0") {
                const syncToStorage = () => {
                    return new Promise((resolve) => {
                        uni.request({
                            url: getApp().globalData.data.requestUrl + '/user/search',
                            method: 'POST',
                            data: {
                                openid: userConfigData.openid
                            },
                            success: (res) => {
                                uni.setStorageSync(app.globalData.data.userInfoKey, res.data.list[0]);
                                uni.setStorageSync(app.globalData.data.userLoginKey, true);
                                resolve(res)
                            },
                        });
                    });
                };
                await syncToStorage()

            } else if (userConfigData.isNewUser == "1") {
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
        // this.checkUserInfo();

        // auth
        /*uni.authorize({
            scope: 'scope.userLocation',
            success: function () {
            }
        })*/

        // handle sys info
        uni.setStorageSync(app.globalData.data.systemInfoKey, uni.getSystemInfoSync());

        // language settings
        const systemInfo = uni.getStorageSync(app.globalData.data.systemInfoKey)
        const lang = systemInfo.appLanguage || 'en';
        this.$i18n.locale = lang
        uni.setLocale(lang);
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
