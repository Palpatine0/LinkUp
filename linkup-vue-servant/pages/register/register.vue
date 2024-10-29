<template>
<div class="page" style="background-color: #f5f7fb">

    <div v-if="step==0" class="page-register">
        <div class="back" @click="backToProfile()">
            <img src="/static/page/register/back.svg">
        </div>
        <div class="header-register">
            <app-title type="h1" bold>{{ $t('register.step0Title') }}</app-title>
            <p class="center-h">{{ $t('register.step0Desc') }}</p>
        </div>

        <!-- Input Field Styled as Button -->
        <div class="button-wrapper">
            <input
                class="input-register center-h"
                type="text"
                v-model="referralCode"
                :placeholder="$t('register.step0Placeholder')"
                @input="setReferralCode"
            />
        </div>
        <app-button shaped size="very-large" class="button-continuation-register" @click="referralCodeValidation()" width="85vw">
            {{ $t('pub.button.confirm') }}
        </app-button>
    </div>

    <!--step 1-->
    <div v-if="step==1" style="height: 100vh;width: 100%">
        <div class="back" @click="backToProfile()">
            <img src="/static/page/register/back.svg">
        </div>
        <div class="center-h">
            <img style="width: 100px;height: 100px;margin-top: 80vh" src="/static/page/register/logo-trans.png">
        </div>
    </div>

    <!--step 2-->
    <div v-if="step==2" class="page-register">
        <div class="back" @click="back()">
            <img src="/static/page/register/back.svg">
        </div>
        <div class="header-register">
            <app-title type="h1" bold>👋{{ $t('register.step2Title') }}</app-title>
            <p class="center-h">{{ $t('register.step2Desc') }}</p>
        </div>
        <!-- Gender Selection Buttons -->
        <div class="button-wrapper" style="margin-top: 15vh;">
            <div class="button-register" @click="selectGender(0)">
                <span>
                    <span class="button-register-icon">
                        👨‍💻
                    </span>
                    <span class="button-register-text">{{ $t('pub.gender.m') }}</span>
                </span>
            </div>
            <div class="button-register" @click="selectGender(1)">
                <span>
                    <span class="button-register-icon">
                        👩‍💻
                    </span>
                    <span class="button-register-text">{{ $t('pub.gender.f') }}</span>
                </span>
            </div>
        </div>
    </div>

    <!--step 3-->
    <div v-if="step==3" class="page-register">
        <div class="back" @click="back()">
            <img src="/static/page/register/back.svg">
        </div>
        <div class="header-register">
            <app-title type="h1" bold>{{ $t('register.step3Title') }}</app-title>
            <p class="center-h">{{ $t('register.step3Desc') }}</p>
        </div>

        <!-- Single Button for Age Selection with Picker Integrated -->
        <div class="button-wrapper">
            <picker
                class="button-register"
                mode="selector"
                :range="ageRange"
                :value="ageRangeIndex"
                @change="bindAgePickerChange"
            >
                <span>
                    <span class="button-register-icon">
                        {{ selectedAgeIcon }}
                    </span>
                    <span class="button-register-text">
                        {{ selectedAgeText ? `${selectedAgeText}` : $t('register.step3Placeholder') }}
                    </span>
                </span>
            </picker>
        </div>
        <app-button v-if="!common.isEmpty(userData.age)" shaped size="very-large" class="button-continuation-register" @click="advance()" width="85vw">
            {{ $t('pub.modal.button.continue') }}
        </app-button>
    </div>

    <!--step 4-->
    <div v-if="step==4" class="page-register">
        <div class="back" @click="back()">
            <img src="/static/page/register/back.svg">
        </div>
        <div class="header-register">
            <app-title type="h1" bold>{{ $t('register.step4Title') }}</app-title>
            <p class="center-h">{{ $t('register.step4Desc') }}</p>
        </div>

        <!-- Input Field Styled as Button -->
        <div class="button-wrapper">
            <input
                class="input-register center-h"
                type="text"
                v-model="userData.nickname"
                :placeholder="$t('register.step4Placeholder')"
                @input="updateUsername"
            />
        </div>
        <app-button shaped size="very-large" class="button-continuation-register" @click="advance()" width="85vw">
            {{ $t('pub.modal.button.continue') }}
        </app-button>
    </div>

    <!--step 5-->
    <div v-if="step==5" class="page-register">
        <div class="back" @click="back()">
            <img src="/static/page/register/back.svg">
        </div>
        <div class="header-register">
            <app-title type="h1" bold>{{ $t('register.step5Title') }}</app-title>
            <p class="center-h">{{ $t('register.step5Desc') }}</p>
        </div>

        <div class="button-wrapper center-h" style="margin-top: 10vh;">
            <img :src="avatar" class="avatar" @click="changeAvatar">
            <div style="width: 100%;display: flex;flex-direction: row-reverse;">
                <div class="upload center">
                    <img src="/static/page/register/up-to-bracket.svg" @click="mediaSelector()">
                </div>
            </div>
        </div>
        <app-button shaped size="very-large" class="button-continuation-register" @click="setUserInfo()" width="85vw">
            {{ $t('register.successSignUp') }}
        </app-button>
    </div>

</div>
</template>

<script>
import common from "../../utils/common";
import $API from "../../api/api";

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
            userConfigData: {},
            referralCode: "",
            referrerId: "",

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
                "https://project-looking.oss-cn-beijing.aliyuncs.com/public/image/default-avatar/AGI2.webp",
                "https://project-looking.oss-cn-beijing.aliyuncs.com/public/image/default-avatar/Building-an-early-warning-system-for-LLM-aided-biological-threat-creation.webp",
                "https://project-looking.oss-cn-beijing.aliyuncs.com/public/image/default-avatar/Business.jpg",
                "https://project-looking.oss-cn-beijing.aliyuncs.com/public/image/default-avatar/CustomBlogCover.avif",
                "https://project-looking.oss-cn-beijing.aliyuncs.com/public/image/default-avatar/DALL_E.jpg",
                "https://project-looking.oss-cn-beijing.aliyuncs.com/public/image/default-avatar/Explore_what_s_possible_with_the_Cookbook.webp",
                "https://project-looking.oss-cn-beijing.aliyuncs.com/public/image/default-avatar/Mac_App_Hero.jpg",
                "https://project-looking.oss-cn-beijing.aliyuncs.com/public/image/default-avatar/apple-art-2a-3x4.webp",
                "https://project-looking.oss-cn-beijing.aliyuncs.com/public/image/default-avatar/introducing_the_gpt_store.webp",
                "https://project-looking.oss-cn-beijing.aliyuncs.com/public/image/default-avatar/practices-for-governing-agentic-ai-systems.avif",
                "https://project-looking.oss-cn-beijing.aliyuncs.com/public/image/default-avatar/start-building-and-api-call.webp",
                "https://project-looking.oss-cn-beijing.aliyuncs.com/public/image/default-avatar/the-latest-milestone-in-openai-s-effort-in-scalling-up-deep-learning.webp",
                "https://project-looking.oss-cn-beijing.aliyuncs.com/public/image/default-avatar/weak-to-strong-generalization.avif",
            ],
            avatar: "",
        }
    },
    onLoad(param) {
        this.userConfigData = JSON.parse(decodeURIComponent(param.userConfigData));
        if(this.$common.isEmpty(uni.getStorageSync('referrerId'))) {
            this.step = 0
        } else {
            this.referrerId = uni.getStorageSync('referrerId');
            this.authRequest();
        }

    },
    watch: {
        step(newStep) {
            if(newStep === 5) {
                this.changeAvatar();
            }
        }
    },
    methods: {
        referralCodeValidation() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.user.referralCodeValidation,
                method: 'POST',
                data: {
                    referralCode: this.referralCode,
                    role: 2,
                },
                success: (res) => {
                    if(res.data.data.validRC == "1") {
                        this.referrerId = res.data.data.referrerId;
                        this.advance()
                        this.authRequest();
                    } else {
                        uni.showToast({title: this.$t('register.showToast.incorrectRC'), icon: 'none'});
                    }
                },
                fail: (res) => {
                    uni.showToast({title: this.$t('register.showToast.incorrectRC'), icon: 'none'});
                }
            });
        },

        back() {
            this.step--;
        },
        advance() {
            this.step++;
        },
        backToProfile() {
            uni.switchTab({
                url: '/pages/profile/profile',
            });
        },

        // step 0
        setReferralCode(event) {
            this.$set(this.referralCode, 'referralCode', event.target.value);
        },

        // step 1
        authRequest() {
            uni.showModal({
                title: this.$t('pub.modal.title.auth'),
                content: this.$t('pub.modal.content.auth'),
                showCancel: true,
                confirmText: this.$t('pub.modal.button.confirm'),
                cancelText: this.$t('pub.modal.button.cancel'),
                success: (res) => {
                    if(res.confirm) {
                        this.getUserInfo();
                    }
                },
                fail: () => {
                    this.backToProfile()
                }
            });
        },
        async getUserInfo(e) {
            uni.showLoading({title: this.$t('pub.showLoading.loading')});

            this.$set(this.userData, 'identifier', this.$common.generateUniqueCode('1', 8));

            // fetch user config data (openid)
            this.userData = {
                ...this.userData,
                openid: this.userConfigData.openid,
                sessionKey: this.userConfigData.openid.sessionKey,
                unionid: this.userConfigData.unionid,
            }

            // fetch user basic data
            const getUserData = () => {
                return new Promise((resolve) => {
                    uni.getSetting({
                        success(res) {
                            if(res.authSetting['scope.userInfo'] != undefined && res.authSetting['scope.userInfo'] == true) {
                                uni.getUserInfo({
                                    success: function(res) {
                                        resolve(res.userInfo);
                                    },
                                });
                            } else {
                                uni.authorize({
                                    scope: 'scope.userInfo',
                                    success() {
                                        uni.getUserInfo({
                                            success: function(res) {
                                                resolve(res.userInfo);
                                            },
                                        });
                                    },
                                    fail(err) {
                                        uni.showToast({title: this.$t('register.showToast.authFailed'), icon: 'none'});
                                        this.backToProfile()
                                    }
                                })
                            }

                        }
                    })
                });
            };
            const userData = await getUserData();
            this.userData = {
                ...this.userData,
                nickname: userData.nickName,
                gender: userData.gender,
            }

            // fetch user location
            const that = this;
            uni.authorize({
                scope: 'scope.userFuzzyLocation',
                success() {
                    // TODO: apply this API, then uncomment this code block
                    /*uni.getFuzzyLocation({
                        type: 'wgs84',
                        success(res) {
                            that.userData = {
                                ...that.userData,
                                latitudeFuzzy: res.latitude,
                                longitudeFuzzy: res.longitude
                            }
                            uni.setStorageSync('latitudeFuzzy', res.latitude);
                            uni.setStorageSync('longitudeFuzzy', res.longitude);
                            that.step++
                        },
                    });*/
                    that.step++
                },
                fail() {
                    uni.showToast({title: this.$t('register.showToast.authFailed'), icon: 'none'});
                    this.backToProfile()
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
            if (this.$common.isEmojiContains(event.target.value)) {
                uni.showToast({
                    title: this.$t('pub.showToast.emojiNotAllowed'),
                    icon: 'none'
                });
                return;
            }
            this.$set(this.userData, 'nickname', event.target.value);
        },

        // step 5
        changeAvatar() {
            const randomIndex = Math.floor(Math.random() * this.avatarList.length);
            this.avatar = this.avatarList[randomIndex];
            this.$set(this.userData, 'avatar', this.avatar);
        },
        mediaSelector() {
            uni.showActionSheet({
                itemList: [
                    this.$t('component>chat>chatItemSelector>gallery.choseFromAlbum'),
                    this.$t('component>chat>chatItemSelector>gallery.takePhoto')
                ],
                success: (res) => {
                    let sourceType = res.tapIndex === 0 ? ['album'] : ['camera'];
                    uni.chooseImage({
                        count: 1,
                        sizeType: ['original', 'compressed'],
                        sourceType: sourceType,
                        success: (chooseResult) => {
                            const filePath = chooseResult.tempFilePaths[0];
                            if (!this.$common.validateFileType(filePath, "img")) {
                                uni.hideLoading();
                                uni.showToast({
                                    title: this.$t('pub.showToast.imgInvalidFileType'), // Image-specific message
                                    icon: 'none'
                                });
                                return;
                            }

                            uni.showLoading({title: this.$t('pub.showLoading.loading')});
                            uni.request({
                                url: getApp().globalData.data.requestUrl + $API.file.signature,
                                method: 'GET',
                                data: {
                                    dir: 'public/user/' + this.userData.identifier + '/avatar/'
                                },
                                success: (policyRes) => {
                                    if(policyRes.statusCode === 200 && policyRes.data.status === 200) {
                                        const policyData = policyRes.data.data;
                                        const host = policyData.host;
                                        const dir = policyData.dir;

                                        // Generate a unique filename
                                        const filename = dir + Date.now() + '_' + Math.floor(Math.random() * 10000);

                                        // Prepare formData
                                        let formData = {
                                            'key': filename,
                                            'policy': policyData.policy,
                                            'OSSAccessKeyId': policyData.accessid,
                                            'signature': policyData.signature,
                                            'success_action_status': '200',
                                        };

                                        // Upload file directly to OSS
                                        uni.uploadFile({
                                            url: host,
                                            filePath: filePath,
                                            name: 'file',
                                            formData: formData,
                                            success: (uploadFileRes) => {
                                                uni.hideLoading();
                                                if(uploadFileRes.statusCode === 200) {
                                                    const imageUrl = host + '/' + filename;
                                                    this.userData.avatar = imageUrl;
                                                    this.avatar = imageUrl;
                                                    this.$set(this.userData, 'avatar', imageUrl);
                                                } else {
                                                    uni.showToast({title: 'Upload failed', icon: 'none'});
                                                }
                                            },
                                            fail: () => {
                                                uni.hideLoading();
                                                uni.showToast({title: 'Upload failed', icon: 'none'});
                                            }
                                        });
                                    } else {
                                        uni.hideLoading();
                                        uni.showToast({title: 'Failed to get policy', icon: 'none'});
                                    }
                                },
                                fail: () => {
                                    uni.hideLoading();
                                    uni.showToast({title: 'Failed to get policy', icon: 'none'});
                                }
                            });
                        }
                    });
                }
            });
        },

        // done
        setUserInfo(e) {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.user.save,
                method: 'POST',
                data: {
                    referralCode: this.$common.generateUniqueCode('a1a', 2),
                    role: 2,
                    referrerId: this.referrerId,
                    ...this.userData
                },
                success: (res) => {
                    this.userData.id = res.data.data.id;
                    uni.setStorageSync(getApp().globalData.data.userLoginKey, true);
                    uni.setStorageSync(getApp().globalData.data.userInfoKey, this.userData);
                    this.$webSocket.connectWebSocket(this.userData.id);
                    uni.showToast({title: this.$t('register.showToast.signUpSuccess'), icon: 'none'});
                },
                fail: () => {
                    uni.showToast({title: this.$t('register.showToast.authFailed'), icon: 'none'});
                },
            });
            this.backToProfile()
        }
    }
}
</script>

<style scoped>
.page-register {
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

.header-register {
    width: 90vw;
    position: absolute;
    top: 16vh;
    text-align: center;
}

.header-register app-title {
    margin-bottom: 10px;
}

.header-register p {
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

.button-register {
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

.button-register:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    border-color: #007bff;
}

.button-register span {
    display: flex;
    align-items: center;
    gap: 10px;
}

.button-register span .button-register-icon {
    font-size: 24px;
    margin: 0 10px 0 10px;
}

.button-register span .button-register-text {
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

.button-continuation-register {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: fixed;
    bottom: 80px;
}

.input-register {
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

.input-register:focus {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    border-color: #007bff;
    outline: none;
}

.avatar {
    width: 230px;
    height: 230px;
    object-fit: cover;
    border-radius: 50%;
}

.upload {
    margin-top: -70px;
    background-color: #b2c8f8;
    border-radius: 50%;
    width: 60px;
    height: 60px;
}

.upload img {
    width: 26px;
    height: 26px;
}
</style>