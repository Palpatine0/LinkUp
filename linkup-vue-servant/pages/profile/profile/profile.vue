<template>
<div class="page">
    <div class="profile-header center-h mb-2">
        <div class="center-h" @click="mediaSelector()">
            <img :src="user.avatar" alt="Profile Photo" class="profile-photo"/>
        </div>
    </div>

    <!--Nickname-->
    <app-title bold="true">{{ $t('profile>profile.nickname') }}</app-title>
    <app-input
        mode="text"
        :placeholder="$t('profile>profile.nicknamePlaceholder')"
        v-model="user.nickname"
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
            :disabled="!allowEdit"
            :style="{ color: allowEdit ? '#000' : 'gray' }"
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
            :disabled="!allowEdit"
            :style="{ color: allowEdit ? '#000' : 'gray' }"
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

export default {
    data() {
        return {
            allowEdit: false,
            showSubmitButton: false, // Controls visibility of the submit button
            user: {
                gender: 0,
                age: null,
                nickname: '',
                isIdentifyCertified: "0", // Default value in case data hasn't loaded
            },
            originalUser: { // This will hold the original data to compare
                gender: 0,
                age: null,
                nickname: ''
            },
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
        this.originalUser = {...storedUser}; // Save a copy of the original user data

        // Once user data is loaded, set allowEdit
        this.allowEdit = this.user.isIdentifyCertified === "0";

        // Set other values based on user data
        this.selectedGenderText = this.genderRange[this.user.gender];
        this.ageRangeIndex = this.ageRange.indexOf(this.user.age);
        this.selectedAgeText = this.ageRange[this.ageRangeIndex];
    },
    watch: {
        // Watch for changes in user data to trigger submit button visibility
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
                    if(res.tapIndex === 0) {
                        uni.chooseImage({
                            count: 12,
                            sizeType: ['original', 'compressed'],
                            sourceType: ['album'],
                            success: (res) => {
                                Promise.all(
                                    res.tempFilePaths.map((item) => {
                                        return new Promise((resolve, reject) => {
                                            fs.readFile({
                                                filePath: item,
                                                encoding: 'base64',
                                                success: (res) => {
                                                    // TODO: update user avatar url
                                                    resolve('data:image/png;base64,' + res.data);
                                                }
                                            });
                                        });
                                    })
                                ).then((results) => {
                                    that.uploadLivePic(results);
                                });
                            }
                        });
                    } else {
                        uni.chooseImage({
                            count: 12,
                            sizeType: ['original', 'compressed'],
                            sourceType: ['camera'],
                            success: (res) => {
                                // TODO: update user avatar url
                            }
                        });
                    }
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
            // Compare the originalUser with the current user
            if(this.user.nickname !== this.originalUser.nickname ||
                this.user.gender !== this.originalUser.gender ||
                this.user.age !== this.originalUser.age) {
                this.showSubmitButton = true; // Show the submit button if there are changes
            } else {
                this.showSubmitButton = false; // Hide if no changes
            }
        },

        formSubmit() {
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
                    if(res.data.status === 200) {
                        uni.setStorageSync(app.globalData.data.userInfoKey, this.user);
                        uni.setStorageSync(app.globalData.data.userLoginKey, true);
                        uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                        this.showSubmitButton = false; // Hide the submit button after successful submission
                        this.originalUser = {...this.user}; // Update originalUser after successful submit
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
    align-items: center;
    justify-content: space-between;
}

.profile-photo {
    width: 150px;
    height: 150px;
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
