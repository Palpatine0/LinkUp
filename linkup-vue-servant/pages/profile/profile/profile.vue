<template>
<div class="page">
    <div class="profile-header center-h mb-2">
        <div class="center-h">
            <img :src="user.avatar" alt="Profile Photo" class="profile-photo" @click="mediaSelector()"/>
            <h3 style="color: #8B8B8B" @click="$common.addToClipboard(user.identifier)">ID: {{ user.identifier }}</h3>
        </div>
    </div>

    <!--Nickname-->
    <app-title bold="true">{{ $t('profile>profile.nickname') }}</app-title>
    <app-input
        mode="text"
        :placeholder="$t('profile>profile.nicknamePlaceholder')"
        v-model="user.nickname"
        :disabled="disabled"
    />

    <!--Gender-->
    <app-title bold="true">{{ $t('profile>profile.gender') }}</app-title>
    <div class="app-input">
        <picker
            class="button-register"
            mode="selector"
            :range="genderRange"
            :value="user.gender"
            @change="bindGenderPickerChange"
            :disabled="disabled"
            :style="{ color: !disabled ? '#000' : 'gray' }"
        >
            <span>
                <span class="button-register-text">
                    {{ selectedGenderText ? selectedGenderText : $t('register.step3Placeholder') }}
                </span>
            </span>
        </picker>
    </div>

    <!--Age-->
    <app-title bold="true">{{ $t('profile>profile.age') }}</app-title>
    <div class="app-input">
        <picker
            class="button-register"
            mode="selector"
            :range="ageRange"
            :value="ageRangeIndex"
            @change="bindAgePickerChange"
            :disabled="disabled"
            :style="{ color: !disabled ? '#000' : 'gray' }"
        >
            <span>
                <span class="button-register-text">
                    {{ selectedAgeText ? selectedAgeText : $t('register.step3Placeholder') }}
                </span>
            </span>
        </picker>
    </div>

    <!-- Submit button (conditionally shown) -->
    <div class="center-h" v-if="showSubmitButton">
        <app-button shaped @click="formSubmit">{{ $t('pub.button.submit') }}</app-button>
    </div>
</div>
</template>

<script>
import app from "../../../App.vue";
import $API from "../../../api/api";
import $common from "../../../utils/common";

export default {
    computed: {
        $common() {
            return $common
        }
    },
    data() {
        return {
            disabled: false,
            showSubmitButton: false,
            user: {},
            originalUser: {},
            genderRange: [this.$t('pub.gender.m'), this.$t('pub.gender.f')],
            selectedGenderText: '',
            ageRange: Array.from({length: 83}, (_, i) => i + 18),
            ageRangeIndex: 0,
            selectedAgeText: ""
        };
    },
    onLoad() {
        // Load user data first
        const storedUser = uni.getStorageSync(app.globalData.data.userInfoKey);
        this.user = {...storedUser};
        this.originalUser = {...storedUser};

        this.disabled = this.$common.isUserVerified()

        // Set other values based on user data
        this.selectedGenderText = this.genderRange[this.user.gender];
        this.ageRangeIndex = this.ageRange.indexOf(this.user.age);
        this.selectedAgeText = this.ageRange[this.ageRangeIndex];
    },
    watch: {
        'user.avatar': 'checkForChanges',
        'user.nickname': 'checkForChanges',
        'user.gender': 'checkForChanges',
        'user.age': 'checkForChanges'
    },
    methods: {
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
                            uni.showLoading({title: this.$t('pub.showLoading.loading')});

                            const filePath = chooseResult.tempFilePaths[0];
                            if(!this.$common.validateFileType(filePath, "img")) {
                                uni.hideLoading();
                                uni.showToast({
                                    title: this.$t('pub.showToast.imgInvalidFileType'), // Image-specific message
                                    icon: 'none'
                                });
                                return;
                            }

                            uni.request({
                                url: getApp().globalData.data.requestUrl + $API.file.signature,
                                method: 'GET',
                                data: {
                                    dir: "public/user/" + this.user.identifier + "/avatar/"
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
                                                    // Update user avatar URL
                                                    const imageUrl = host + '/' + filename;
                                                    this.user.avatar = imageUrl;
                                                    console.log("imageUrl")
                                                    console.log(imageUrl)
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
                                        uni.showToast({title: 'Failed to get policy', icon: 'none'});
                                    }
                                },
                                fail: () => {
                                    uni.showToast({title: 'Failed to get policy', icon: 'none'});
                                }
                            });
                        }
                    });
                }
            });
        },

        bindGenderPickerChange(e) {
            const selectedGenderIndex = e.detail.value;
            this.user.gender = selectedGenderIndex;
            this.selectedGenderText = this.genderRange[selectedGenderIndex];
            this.$set(this.user, 'gender', selectedGenderIndex);
        },

        bindAgePickerChange(e) {
            this.ageRangeIndex = e.detail.value;
            const selectedAge = this.ageRange[this.ageRangeIndex];
            this.selectedAgeText = `${selectedAge}`;
            this.$set(this.user, 'age', selectedAge);
        },

        checkForChanges() {
            if(
                this.user.avatar !== this.originalUser.avatar ||
                this.user.nickname !== this.originalUser.nickname ||
                this.user.gender !== this.originalUser.gender ||
                this.user.age !== this.originalUser.age) {
                this.showSubmitButton = true;
            } else {
                this.showSubmitButton = false;
            }
        },

        formSubmit() {
            uni.showLoading({title: this.$t('pub.showLoading.loading')});
            if(this.$common.isEmojiContains(this.user.nickname)) {
                uni.hideLoading();
                uni.showToast({
                    title: this.$t('pub.showToast.emojiNotAllowed'),
                    icon: 'none'
                });
                return;
            }
            uni.request({
                url: getApp().globalData.data.requestUrl + $API.user.update,
                method: 'POST',
                data: {
                    id: this.user.id,
                    nickname: this.user.nickname,
                    gender: this.user.gender,
                    age: this.user.age,
                    avatar: this.user.avatar,
                },
                success: (res) => {
                    uni.hideLoading();
                    if(res.data.status === 200) {
                        uni.setStorageSync(app.globalData.data.userInfoKey, this.user);
                        uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                        this.showSubmitButton = false;
                        this.originalUser = {...this.user};
                    } else {
                        uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                    }
                },
            });
        }
    }
};
</script>

<style scoped>
.profile-header {
    text-align: center;
    justify-content: space-between;
}

.profile-photo {
    width: 180px;
    height: 180px;
    border-radius: 50%;
}

.button-register-text {
    font-size: 16px;
}

.disabled {
    pointer-events: none;
    opacity: 0.5;
    color: grey;
}
</style>
