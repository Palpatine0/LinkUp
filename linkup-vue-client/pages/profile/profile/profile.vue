<template>
<div class="page">
    <div class="profile-header center-h mb-2">
        <div class="center-h" @click="mediaSelector()">
            <img :src="user.avatar" alt="Profile Photo" class="profile-photo"/>
        </div>
    </div>

    <app-title bold="true">{{ $t('profile>profile.nickname') }}</app-title>
    <app-input
        mode="text"
        :placeholder="$t('profile>profile.nicknamePlaceholder')"
        v-model="user.nickname"
    />

    <app-title bold="true">{{ $t('profile>profile.gender') }}</app-title>
    <div class="app-input">
        <picker
            class="button-register"
            mode="selector"
            :range="genderRange"
            :value="user.gender"
            @change="bindGenderPickerChange"
        >
            <span>
                <span class="button-register-text">
                    {{ selectedGenderText ? selectedGenderText : $t('register.step3Placeholder') }}
                </span>
            </span>
        </picker>
    </div>

    <app-title bold="true">{{ $t('profile>profile.age') }}</app-title>
    <div class="app-input">
        <picker
            class="button-register"
            mode="selector"
            :range="ageRange"
            :value="ageRangeIndex"
            @change="bindAgePickerChange"
        >
            <span>
                <span class="button-register-text">
                    {{ selectedAgeText ? selectedAgeText : $t('register.step3Placeholder') }}
                </span>
            </span>
        </picker>
    </div>
</div>
</template>

<script>
import app from "../../../App.vue";

export default {
    data() {
        return {
            user: {
                gender: 0,
                age: null
            },
            genderRange: [this.$t('pub.gender.m'), this.$t('pub.gender.f')],
            selectedGenderText: '',
            ageRange: Array.from({length: 83}, (_, i) => i + 18), // Age range from 18 to 100
            ageRangeIndex: 0, // Default selected index for age
            selectedAgeText: "",
        };
    },
    onLoad() {
        this.user = uni.getStorageSync(app.globalData.data.userInfoKey);
        this.selectedGenderText = this.genderRange[this.user.gender];
        this.ageRangeIndex = this.ageRange.indexOf(this.user.age);
        this.selectedAgeText = this.ageRange[this.ageRangeIndex];
    },
    methods: {
        mediaSelector() {
            uni.showActionSheet({
                itemList: [
                    this.$t('component>chat>chatItemSelector>gallery.choseFromAlbum'),
                    this.$t('component>chat>chatItemSelector>gallery.takePhoto'),
                ],
                success: (res) => {
                    if(res.tapIndex == 0) {
                        uni.chooseImage({
                            count: 12,
                            sizeType: ['original', 'compressed'],
                            sourceType: ['album'],
                            success: (res) => {
                                Promise.all(
                                    res.tempFilePaths.map(item => {
                                        return new Promise((resolve, reject) => {
                                            fs.readFile({
                                                filePath: item,
                                                encoding: 'base64',
                                                success: res => {
                                                    // TODO: update user avatar url
                                                    resolve('data:image/png;base64,' + res.data)
                                                },
                                            })
                                        })
                                    })
                                ).then(results => {
                                    that.uploadLivePic(results)
                                })
                            }
                        })
                    } else {
                        uni.chooseImage({
                            count: 12,
                            sizeType: ['original', 'compressed'],
                            sourceType: ['camera'],
                            success: (res) => {
                                // TODO: update user avatar url
                            }
                        })
                    }
                },
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
    }
};
</script>

<style>
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
</style>
