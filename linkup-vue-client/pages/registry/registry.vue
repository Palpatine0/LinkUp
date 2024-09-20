<template>
<div class="page" style="background-color: #f5f7fb">

    <!--step 1-->
    <div v-if="step==1" style="height: 100vh;width: 100%">
        <div class="center_h">
            <img style="width: 100px;height: 100px;margin-top: 80vh" src="/static/page/registry/logo-trans.png">
        </div>
    </div>

    <!--step 2-->
    <div v-if="step==2" class="page-registry">
        <div class="back" @click="back()">
            <img src="/static/page/registry/back.svg">
        </div>
        <div class="header-registry">
            <app-title type="h1" bold>👋 欢迎来到领客</app-title>
            <p>指定您的性别，以便更好地进行个性化互动</p>
        </div>
        <!-- Gender Selection Buttons -->
        <div class="button-wrapper">
            <div class="button-registry" @click="selectGender(0)">
                <span>
                    <span class="button-registry-icon">
                        👨‍💻
                    </span>
                    <span class="button-registry-text">男性</span>
                </span>
            </div>
            <div class="button-registry" @click="selectGender(1)">
                <span>
                    <span class="button-registry-icon">
                        👩‍💻
                    </span>
                    <span class="button-registry-text">女性</span>
                </span>
            </div>
        </div>
    </div>

    <!--step 3-->
    <div v-if="step==3" class="page-registry">
        <div class="back" @click="back()">
            <img src="/static/page/registry/back.svg">
        </div>
        <div class="header-registry">
            <app-title type="h1" bold>您的年龄？</app-title>
            <p>指定您的年龄，以便更好地进行个性化互动</p>
        </div>

        <!-- Single Button for Age Selection with Picker Integrated -->
        <div class="button-wrapper">
            <picker
                class="button-registry"
                mode="selector"
                :range="ageRange"
                :value="ageRangeIndex"
                @change="bindAgePickerChange"
            >
                <span>
                    <span class="button-registry-icon">
                        {{ selectedAgeIcon }}
                    </span>
                    <span class="button-registry-text">
                        {{ selectedAgeText ? `${selectedAgeText}` : "选择您的年龄" }}
                    </span>
                </span>
            </picker>
        </div>

        <div>

        </div>
        <app-button v-if="!common.isEmpty(userData.age)" shaped size="very-large" class="button-continuation-registry" @click="advance()" width="85vw">
            继续
        </app-button>
    </div>

    <!--step 4-->
    <div v-if="step==4" class="page-registry">
        <div class="back" @click="back()">
            <img src="/static/page/registry/back.svg">
        </div>
        <div class="header-registry">
            <app-title type="h1" bold>您的显示名称？</app-title>
            <p>请输入您的用户名，让我们更好地了解您</p>
        </div>

        <!-- Input Field Styled as Button -->
        <div class="button-wrapper">
            <input
                class="input-registry center_h"
                type="text"
                v-model="userData.nickname"
                placeholder="请输入您的用户名"
                @input="updateUsername"
            />
        </div>
        <app-button shaped size="very-large" class="button-continuation-registry" @click="advance()" width="85vw">
            继续
        </app-button>
    </div>

    <!--step 5-->
    <div v-if="step==5" class="page-registry">
        <div class="back" @click="back()">
            <img src="/static/page/registry/back.svg">
        </div>
        <div class="header-registry">
            <app-title type="h1" bold>您的头像？</app-title>
            <p>点击下方图片以切换不同的头像</p>
        </div>

        <div class="button-wrapper center_h">
            <img :src="avatar" class="avatar" @click="changeAvatar">
        </div>
        <app-button shaped size="very-large" class="button-continuation-registry" @click="setUserInfo()" width="85vw">
            注册成功!
        </app-button>
    </div>

</div>
</template>

<script>
import common from "../../utils/common";

export default {
    name: "auth",
    computed: {
        common() {
            return common
        }
    },
    data() {
        return {
            step: 1,
            userData: {},

            // step 3
            preciseAgeRanges: [
                {label: 'Under 18', icon: '🌑', range: [0, 17]},
                {label: '18-24', icon: '🌒', range: [18, 24]},
                {label: '25-34', icon: '🌓', range: [25, 34]},
                {label: '35-44', icon: '🌔', range: [35, 44]},
                {label: '45-54', icon: '🌕', range: [45, 54]},
                {label: '55-64', icon: '🌖', range: [55, 64]},
                {label: '65-74', icon: '🌗', range: [65, 74]},
                {label: '75 and over', icon: '🌘', range: [75, 100]}
            ],
            ageRange: Array.from({length: 83}, (_, i) => i + 18),
            ageRangeIndex: 0,
            selectedAgeText: "",
            selectedAgeIcon: "👤",

            // step 5
            avatarList: [
                "https://i.imghippo.com/files/9BX9D1726433062.webp",
                "https://i.imghippo.com/files/RHBfw1726433075.webp",
                "https://i.imghippo.com/files/4mxDr1726433090.jpg",
                "https://i.imghippo.com/files/gksLM1726433102.avif",
                "https://i.imghippo.com/files/KMncW1726433120.webp",
                "https://i.imghippo.com/files/nHLzE1726433130.webp",
                "https://i.imghippo.com/files/f3DeT1726433144.jpg",
                "https://i.imghippo.com/files/Uhflq1726433155.avif",
                "https://i.imghippo.com/files/wIn1U1726433165.webp",
                "https://i.imghippo.com/files/U369F1726433184.webp",
                "https://i.imghippo.com/files/THZqA1726433199.avif",
                "https://i.imghippo.com/files/MkOeo1726433215.avif"
            ],
            avatar: "",
        }
    },
    onLoad() {
        this.authRequest();
    },
    watch: {
        step(newStep) {
            if (newStep === 5) {
                this.changeAvatar();
            }
        }
    },
    methods: {
        back() {
            this.step--;
        },
        advance() {
            this.step++;
        },

        // step 1
        authRequest() {
            this.userProfileAvailable = false;
            uni.showModal({
                title: '授权',
                content: '请授权您的个人信息以使用完整服务',
                showCancel: true,
                confirmText: '授权',
                success: (res) => {
                    if (res.confirm) {
                        this.getUserInfo();
                    }
                },
            });
        },
        async getUserInfo(e) {
            uni.showLoading({title: '加载中'});

            // fetch user config data (openid)
            const getUserLoginCode = () => {
                return new Promise((resolve) =>
                    uni.login({
                        provider: 'weixin',
                        success: (res) => {
                            resolve(res.code);
                        },
                        fail: () => {
                            uni.showToast({title: '用户openid获取失败', icon: 'none'});
                        },
                    })
                );
            };
            const userLoginCode = await getUserLoginCode();
            const getUserConfigData = () => {
                return new Promise((resolve) => {
                    uni.request({
                        url: getApp().globalData.requestUrl + '/user/save-auth-info',
                        method: 'POST',
                        data: {
                            code: userLoginCode,
                        },
                        success: (res) => {
                            if (res.data.auth == null) {
                                uni.showToast({title: '授权失败', icon: 'none'});
                            } else {
                                resolve(res.data.auth);
                            }
                        },
                        fail: () => {
                            uni.showToast({title: '授权请求失败', icon: 'none'});
                        },
                    });
                });
            };
            const userConfigData = await getUserConfigData();
            this.userData = {
                ...this.userData,
                id: userConfigData.id,
                openid: userConfigData.openid,
                sessionKey: userConfigData.openid.sessionKey,
                unionid: userConfigData.unionid,
            }

            // fetch user basic data
            const getUserData = () => {
                return new Promise((resolve) => {
                    uni.authorize({
                        scope: 'scope.userInfo',
                        success() {
                            uni.getUserInfo({
                                success: function (res) {
                                    resolve(res.userInfo);
                                },
                            });
                        },
                        fail() {
                            uni.exitMiniProgram()
                        }
                    })
                });
            };
            const userData = await getUserData();
            this.userData = {
                ...this.userData,
                role: 1,
                nickname: userData.nickName,
                gender: userData.gender,
            }

            // fetch user location
            const that = this;
            uni.authorize({
                scope: 'scope.userFuzzyLocation',
                success() {
                    uni.getFuzzyLocation({
                        type: 'wgs84',
                        success(res) {
                            console.log("this.userData before loc")
                            console.log(that.userData)
                            that.userData = {
                                ...that.userData,
                                latitudeFuzzy: res.latitude,
                                longitudeFuzzy: res.longitude,
                            }
                            that.step++
                        },
                    });
                },
                fail() {
                    uni.exitMiniProgram()
                }
            })
            uni.hideLoading();
        },

        // step 2
        selectGender(gender) {
            this.$set(this.userData, 'gender', gender);
            this.step++;
        },

        // step 3
        bindAgePickerChange(e) {
            this.ageRangeIndex = e.detail.value;
            const selectedAge = this.ageRange[this.ageRangeIndex];
            this.selectedAgeText = `${selectedAge}`;
            this.selectedAgeIcon = this.getIconForAge(selectedAge);
            this.$set(this.userData, 'age', selectedAge);
        },
        getIconForAge(age) {
            const range = this.preciseAgeRanges.find(r => age >= r.range[0] && age <= r.range[1]);
            return range ? range.icon : "👤";
        },

        // step 4
        updateUsername(event) {
            this.$set(this.userData, 'nickname', event.target.value);
        },

        // step 5
        changeAvatar() {
            const randomIndex = Math.floor(Math.random() * this.avatarList.length);
            this.avatar = this.avatarList[randomIndex];
            this.$set(this.userData, 'avatar', this.avatar);
        },

        // done
        setUserInfo(e) {
            uni.request({
                url: getApp().globalData.requestUrl + '/user/update',
                method: 'POST',
                data: {
                    ...this.userData
                },
                success: () => {
                    // userAccountData
                    uni.setStorageSync('userId', this.userData.id);
                    uni.setStorageSync('openid', this.userData.openid);
                    uni.setStorageSync('sessionKey', this.userData.openid.session_key);
                    uni.setStorageSync('unionid', this.userData.unionid);
                    // userData
                    uni.setStorageSync('nickname', this.userData.nickName);
                    uni.setStorageSync('avatar', this.userData.avatar);
                    uni.setStorageSync('gender', this.userData.gender);
                    this.userProfileAvailable = true;
                    uni.hideLoading();
                    uni.showToast({title: '授权成功', icon: 'none'});

                },
                fail: () => {
                    uni.showToast({title: '授权失败', icon: 'none'});
                },
            });
            uni.switchTab({
                url: '/pages/home/home',
                fail(err) {
                    console.log("redierect err");
                    console.log(err);
                }
            })
            ;

        }
    }
}
</script>

<style scoped>
.page-registry {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100vh;
    background-color: #f5f7fb;
}

.back {
    background-color: #eceef4;
    border-radius: 50%;
    position: fixed;
    top: 58px;
    left: 10px;
    width: 35px;
    height: 35px;
}

.back img {
    padding: 5px;
    width: 25px;
    height: 25px;
}

.header-registry {
    position: absolute;
    top: 16vh;
    text-align: center;
}

.header-registry app-title {
    margin-bottom: 10px;
}

.header-registry p {
    font-size: 18px;
    color: #666;
    margin-top: 68px;
    width: 70vw;
}

.button-wrapper {
    display: flex;
    flex-direction: column;
    gap: 15px;
    width: 100%;
}

.button-registry {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    padding: 15px 20px;
    font-size: 16px;
    color: #333;
    background-color: #fff;
    border: 1px solid #ddd;
    border-radius: 30px;
    cursor: pointer;
    transition: box-shadow 0.3s ease, border-color 0.3s ease;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12);
}

.button-registry:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    border-color: #007bff;
}

.button-registry span {
    display: flex;
    align-items: center;
    gap: 10px;
}

.button-registry span .button-registry-icon {
    font-size: 24px;
    margin: 0 10px 0 10px;
}

.button-registry span .button-registry-text {
    font-weight: bold;
}

.age-picker-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    margin-top: 20px;
}

.picker-view {
    font-size: 18px;
    padding: 15px;
    color: #333;
    background-color: #fff;
    border: 1px solid #ddd;
    border-radius: 30px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12);
}

.button-continuation-registry {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: fixed;
    bottom: 80px;
}

.input-registry {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 15px 90px;
    font-size: 16px;
    color: #333;
    background-color: #fff;
    border: 1px solid #ddd;
    border-radius: 30px;
    width: 103%;
    text-align: center;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12);
    transition: box-shadow 0.3s ease, border-color 0.3s ease;
}

.input-registry:focus {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    border-color: #007bff;
    outline: none;
}

.avatar {
    width: 300px;
    height: 300px;
    object-fit: cover;
    border-radius: 50%;
    margin-bottom: 10px;
}
</style>