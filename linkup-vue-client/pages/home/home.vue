<template>
<div class="page">
    <app-title type="h1" bold="true">{{ $t('home.nearby') }}</app-title>

    <!-- Category Filters (Optional, can be expanded) -->
    <div class="app-container service-info" :style="{backgroundImage: `url(${serviceTypeList[0].coverImg})`, backgroundRepeat: 'no-repeat',backgroundPosition: 'center center',backgroundSize: '135%'}" @click="serviceDetailRedirect(serviceTypeList[0].id)">
        <div class="gradient-overlay" style="border-bottom-right-radius: 15px;border-bottom-left-radius: 15px"></div>
        <div class="service-info-text">
            {{ $t('home.tourGuideServices') }}
        </div>
    </div>
    <div style="display: flex">
        <picker mode="multiSelector" :range="[startAges, endAges]" :value="[startAgeIndex, endAgeIndex]" @change="onAgeRangeChange">
            <app-button size="small" shaped bold color="#f6f6f6" font-color="#0A2342" width="100px">
                {{ selectedAgeText ? selectedAgeText : $t('order>orderInitiate.options.allAge') }}
                <img class="selector-icon" src="/static/common/down-arrow.svg">
            </app-button>
        </picker>
        <app-button size="small" bold color="#FFF" :font-color="genderType==0 ? '#0A2342':'#CCC'" width="60px" @click="setGenderType(0)">{{ $t('pub.gender.m') }}</app-button>
        <app-button size="small" bold color="#FFF" :font-color="genderType==1 ? '#0A2342':'#CCC'" width="60px" @click="setGenderType(1)">{{ $t('pub.gender.f') }}</app-button>
    </div>
    <!-- Scroll View for User List -->
    <user-list :userList="userList" height="60vh" @user-click="userDetailRedirect"/>
</div>
</template>


<script>
export default {
    name: "home",
    data() {
        return {
            userList: [],
            serviceTypeList: [],
            genderType: '',
            startAges: Array.from({length: 83}, (_, i) => i + 18), // Ages from 18 to 100
            endAges: Array.from({length: 83}, (_, i) => i + 18), // Ages from 18 to 100
            startAgeIndex: 0,
            endAgeIndex: 82, // Default to max age
            selectedAgeText: "",
        };
    },
    onShow(param) {
        this.reload();
        if(uni.getStorageSync(getApp().globalData.data.userInfoKey).gender == null) {
            this.genderType = -1
        } else {
            uni.getStorageSync(getApp().globalData.data.userInfoKey).gender == 0 ? this.genderType = 1 : this.genderType = 0
        }

        this.ageRangeIndex = 0;
        this.selectedAgeText = "";
        this.selectedAgeIcon = "👤";

        if(!this.$common.isEmpty(param) && !this.$common.isEmpty(param.referrerId)) {
            uni.setStorageSync('referrerId', param.referrerId);
        }
    },
    computed: {
        ageRangeDisplay() {
            return [this.$t('order>orderInitiate.options.allAge'), ...Array.from({length: 83}, (_, i) => (i + 18).toString())];
        }
    },
    methods: {
        reload() {
            this.resetPagination();
            this.getDataList();
        },
        buildApiParams() {
            let url = getApp().globalData.data.requestUrl + this.$API.user.search;
            let method = 'POST';
            let baseData = {
                role: 2,
                gender: this.genderType,
                page: this.page,
                size: this.pageSize,
            };

            // Include age range if selected
            if(this.selectedAgeText) {
                baseData.ageMin = this.startAges[this.startAgeIndex];
                baseData.ageMax = this.endAges[this.endAgeIndex];
            }

            let data = this.searchKeyword && this.searchKeyword.trim() !== ''
                ? {...baseData, keyword: this.searchKeyword}
                : {...baseData};

            return {url, method, data};
        },


        getDataList() {
            if(!this.hasMore || this.loading) return;
            this.loading = true;

            const {url, method, data} = this.buildApiParams();

            uni.request({
                url: url,
                method: method,
                data: data,
                success: (res) => {
                    let users = res.data.list;
                    users = users.filter(user => user.id !== uni.getStorageSync(getApp().globalData.data.userInfoKey).id,);

                    if(this.page === 1) {
                        this.userList = [];
                    }

                    if(users.length < this.pageSize) {
                        this.hasMore = false;
                    }

                    this.userList = this.userList.concat(users);
                    this.page += 1;
                },
                complete: () => {
                    this.loading = false;
                },
            });
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.serviceType.search,
                method: "POST",
                data: {},
                success: (res) => {
                    this.serviceTypeList = [];
                    this.serviceTypeList = res.data.list;
                },
            });
        },

        onAgeRangeChange(e) {
            this.startAgeIndex = e.detail.value[0];
            this.endAgeIndex = e.detail.value[1];

            const startAge = this.startAges[this.startAgeIndex];
            const endAge = this.endAges[this.endAgeIndex];

            if(startAge > endAge) {
                uni.showToast({
                    title: this.$t('pub.showToast.invalidAgeRange'),
                    icon: 'none'
                });
                return;
            }

            this.selectedAgeText = `${startAge} - ${endAge}`;
            this.reload();
        },

        setGenderType(type) {
            if(type == this.genderType) {
                this.genderType = -1
            } else {
                this.genderType = type
            }
            this.reload()
        },

        // redirects
        userDetailRedirect(userId) {
            uni.navigateTo({
                url: '/pages/components/user/user-detail/user-detail?userId=' + userId,
            });
        },
        serviceDetailRedirect(serviceTypeId) {
            uni.navigateTo({
                url: '/pages/components/service/service-detail/service-detail?serviceTypeId=' + serviceTypeId,
            });
        }
    }
};
</script>

<style scoped>
.selector-icon {
    width: 16px;
    height: 16px;
    position: relative;
    top: 2px;
    left: 4px;
}
.service-info {
    height: 160px;
    position: relative;
}

.service-info-text {
    position: absolute;
    bottom: 40px;
    width: 100%;
    z-index: 100;
    height: 20px;
    color: white;
    font-size: 35px;
    font-weight: bold;
}
</style>
