<template>
<div>
    <div class="page">
        <app-title type="h1" bold="true">发布订单</app-title>

        <!-- Gender -->
        <app-title bold="true">服务者性别</app-title>
        <view class="app-input">
            <picker
                @change="bindGenderPickerChange"
                :value="genderIndex"
                :range="dropdownOptions.gender"
            >
                <view>{{ dropdownOptions.gender[genderIndex] }}</view>
            </picker>
        </view>

        <!-- Age Range -->
        <app-title bold="true">服务者年龄</app-title>
        <view class="app-input">
            <picker
                :key="dropdownOptions.age[0][ageRangeIndex[0]] + '-' + dropdownOptions.age[1][0]"
                mode="multiSelector"
                :range="dropdownOptions.age"
                :value="ageRangeIndex"
                @change="bindAgeRangePickerChange"
                @columnchange="bindAgeRangeColumnChange"
            >
                <view class="uni-input">
                    <template
                        v-if="
              dropdownOptions.age[0][ageRangeIndex[0]] === '不限' &&
              dropdownOptions.age[1][ageRangeIndex[1]] === '不限'
            "
                    >
                        不限
                    </template>
                    <template v-else-if="dropdownOptions.age[0][ageRangeIndex[0]] === '不限'">
                        {{ '不限 - ' + dropdownOptions.age[1][ageRangeIndex[1]] }}
                    </template>
                    <template v-else-if="dropdownOptions.age[1][ageRangeIndex[1]] === '不限'">
                        {{ dropdownOptions.age[0][ageRangeIndex[0]] + '岁以上' }}
                    </template>
                    <template v-else>
                        {{
                            dropdownOptions.age[0][ageRangeIndex[0]] +
                            ' - ' +
                            dropdownOptions.age[1][ageRangeIndex[1]]
                        }}
                    </template>
                </view>
            </picker>
        </view>

        <!-- location -->
        <app-title bold="true">服务地点</app-title>
        <view class="app-input">
            <div v-if="location.address" @tap="authVerification">
                {{ location.name }}
            </div>
            <div v-else @tap="authVerification">
                请选择服务地点
            </div>
        </view>

        <!-- Service Duration -->
        <app-title bold="true">服务时长</app-title>
        <div class="app-input">
            <picker
                @change="bindServiceDurationPickerChange"
                :value="serviceDurationIndex"
                :range="dropdownOptions.serviceDuration"
            >
                <view>{{ dropdownOptions.serviceDuration[serviceDurationIndex] }}</view>
            </picker>
        </div>
        <app-title bold="true">服务时间</app-title>
        <div class="app-input">
            <div v-if="common.isEmpty(serviceTime)" @click="open">请选择服务时间</div>
            <div v-else @click="open">{{ serviceTime }}</div>
        </div>
        <!-- Price -->
        <app-title type="h2" bold="true">价格</app-title>
        <view class="app-input">
            <picker
                @change="bindPricePickerChange"
                :value="priceIndex"
                :range="priceOptions"
            >
                <view>{{ priceOptions[priceIndex] }}</view>
            </picker>
        </view>

        <!-- Submit Button -->
        <div name="submit form" class="center_h">
            <div class="app-button" @click="paymentMethodSelectionToggle">发布</div>
        </div>

        <PaymentMethodSelection v-if="paymentMethodSelectionVisible" :user="user" :balanceAdequate="balanceAdequate"></PaymentMethodSelection>


    </div>
    <delivery-time @change="bindServiceTimeChange" ref="chooseTime" title="请选择预约时间" isMask :hour="parseInt(this.dropdownOptions.serviceDuration[this.serviceDurationIndex])"></delivery-time>
</div>
</template>

<script>
import PaymentMethodSelection from "../../../../components/page/payment/payment-method-selection.vue";
import common from "../../../../utils/common";

export default {
    computed: {
        common() {
            return common
        }
    },
    components: {PaymentMethodSelection},
    data() {
        return {
            // basic info
            title: "",
            serviceType: '',
            serviceName: '',

            // gender
            genderIndex: 0,

            // age
            ageRangeIndex: [0, 0],

            // service duration
            serviceDurationIndex: 0,

            // price
            priceIndex: 0,
            priceOptions: [],

            // location
            location: {},

            // service time
            serviceTime: "",

            dropdownOptions: {
                gender: ['不限', '男', '女'],
                age: [
                    ['不限'].concat(Array.from({length: 83}, (_, i) => i + 18)), // Ages 18 to 100
                    ['不限'].concat(Array.from({length: 83}, (_, i) => i + 18)), // Adjusted in methods
                ],
                serviceDuration: Array.from({length: 24}, (_, i) => `${i + 1}小时`),
                price: [],
            },

            // payment
            user: {},
            paymentMethodSelectionVisible: false,
            balanceAdequate: false,
        };
    },
    onLoad(param) {
        this.serviceType = param.serviceType;
        this.serviceName = param.serviceName;
        this.updatePriceOptions();
        this.generateTitle();
    },
    methods: {
        // basic info
        getUser() {
            return new Promise((resolve, reject) => {
                uni.request({
                    url: getApp().globalData.requestUrl + '/user/search',
                    method: 'POST',
                    data: {
                        id: uni.getStorageSync("userId")
                    },
                    success: (res) => {
                        this.user = res.data.list[0]
                        this.balanceAdequateValidation();
                        resolve();
                    },
                    fail: (err) => {
                        reject(err);
                    }
                });
            });
        },

        // gender
        bindGenderPickerChange(e) {
            this.genderIndex = e.detail.value;
        },

        // age
        bindAgeRangePickerChange(e) {
            this.ageRangeIndex = e.detail.value;
            const fromAge = this.dropdownOptions.age[0][this.ageRangeIndex[0]];
            const toAge = this.dropdownOptions.age[1][this.ageRangeIndex[1]];

            console.log(`Age Range Selected: ${fromAge} - ${toAge}`);
        },
        bindAgeRangeColumnChange(e) {
            const column = e.detail.column;
            const value = e.detail.value;

            if (column === 0) {
                this.ageRangeIndex[0] = value; // Update first column index
                const selectedFromAge = this.dropdownOptions.age[0][value];

                let newSecondColumn;

                if (selectedFromAge === '不限') {
                    // If "不限" is selected, the second column includes "不限" and all ages
                    newSecondColumn = ['不限'].concat(
                        Array.from({length: 83}, (_, i) => i + 18)
                    );
                } else {
                    // Second column options start from selectedFromAge to 100
                    const fromAgeNumber = parseInt(selectedFromAge);
                    newSecondColumn = this.dropdownOptions.age[0].filter((age) => {
                        return age !== '不限' && parseInt(age) >= fromAgeNumber;
                    });
                    // Do not include '不限' in the second column
                }

                // Replace the entire age array to ensure reactivity
                this.dropdownOptions.age = [this.dropdownOptions.age[0], newSecondColumn];
                this.ageRangeIndex[1] = 0; // Reset second column index
            } else if (column === 1) {
                this.ageRangeIndex[1] = value; // Update second column index
            }
        },

        // service duration
        bindServiceDurationPickerChange(e) {
            this.serviceDurationIndex = e.detail.value;
            const selectedDuration = this.dropdownOptions.serviceDuration[this.serviceDurationIndex];
            console.log(`Selected service duration: ${selectedDuration}`);
            this.updatePriceOptions(); // Update price options based on the selected duration
            this.serviceTime = null
        },

        // price
        updatePriceOptions() {
            const selectedDuration = this.dropdownOptions.serviceDuration[this.serviceDurationIndex];
            const durationNumber = parseInt(selectedDuration); // Extract numeric value
            let prices = [];

            // Define price options based on the selected duration
            if (durationNumber === 1) {
                prices = [200, 300, 400, 500];
            } else if (durationNumber === 2) {
                prices = [300, 400, 500, 600];
            } else {
                // For other durations, define your pricing logic
                const basePrice = 200 + (durationNumber - 1) * 100;
                prices = [basePrice, basePrice + 100, basePrice + 200, basePrice + 300];
            }

            // Update price options with "元" appended for display
            this.priceOptions = prices.map((price) => `${price}元`);
            this.priceIndex = 0; // Reset price index
        },
        bindPricePickerChange(e) {
            this.priceIndex = e.detail.value;
            console.log(`Selected price: ${this.priceOptions[this.priceIndex]}`);
        },

        // location
        authVerification() {
            uni.getSetting({
                success: (res) => {
                    if (res.authSetting['scope.userLocation']) {
                        this.handleChooseLocation()
                    } else if (res.authSetting['scope.userLocation'] === undefined) {
                        this.handleOpenSetting()
                    } else {
                        this.handleOpenSetting()
                    }
                },
            })
        },
        handleChooseLocation() {
            uni.chooseLocation({
                latitude: uni.getStorageSync('latitudeFuzzy') || '',
                longitude: uni.getStorageSync('longitudeFuzzy') || '',
                success: (res) => {
                    uni.setStorageSync('currentLocation', res)
                    this.location = res
                },
                fail: function (err) {
                    console.log('取消按钮', err)
                }
            })
        },
        handleOpenSetting() {
            wx.openSetting({
                success: (res) => {
                    if (res.authSetting["scope.userLocation"]) {
                        this.handleChooseLocation()
                    }
                }
            })
        },

        // service time
        open() {
            this.$refs.chooseTime.open()
        },
        bindServiceTimeChange(e) {
            this.serviceTime = e.value
        },

        // payment
        paymentMethodSelectionToggle() {
            this.getUser().then(() => {
                // Then toggle the visibility after user data has been fetched
                this.paymentMethodSelectionVisible = !this.paymentMethodSelectionVisible;
            });
        },
        balanceAdequateValidation() {
            // Convert the amount and balance to floats to ensure accurate comparison
            const selectedPrice = parseInt(this.priceOptions[this.priceIndex]);
            const balance = parseFloat(this.user.balance);

            // Ensure both amount and balance are valid numbers (not NaN)
            if (this.common.isEmpty(selectedPrice) || this.common.isEmpty(balance)) {
                this.balanceAdequate = false;
                return;
            }
            // Check if the user's balance is sufficient to cover the required amount
            if (balance >= selectedPrice) {
                this.balanceAdequate = true;
            } else {
                this.balanceAdequate = false;
            }
        },

        // submit
        generateTitle() {
            // Destructure values for easier access
            const {gender, age, serviceDuration} = this.dropdownOptions;
            const {genderIndex, ageRangeIndex, serviceDurationIndex} = this;

            // Gender text
            const genderText = gender[genderIndex] === '不限' ? '不限性别' : gender[genderIndex] === '男' ? '男' : '女';

            // Age range text
            const ageMin = age[0][ageRangeIndex[0]] === '不限' ? '不限' : `${age[0][ageRangeIndex[0]]}岁以上`;
            const ageMax = age[1][ageRangeIndex[1]] === '不限' ? '不限' : `${age[1][ageRangeIndex[1]]}岁以下`;
            const ageText = ageMin === '不限' && ageMax === '不限' ? '不限年龄' : `${ageMin} - ${ageMax}`;

            // Service duration text
            const durationText = serviceDuration[serviceDurationIndex];

            // Price
            const price = this.priceOptions[this.priceIndex];

            // State and city text
            const location = this.dropdownOptions.state ? `${this.dropdownOptions.state}${this.dropdownOptions.city}` : '不限地区';

            // Update the title with the concatenated values
            this.title = `${this.serviceName}服务: ${genderText} / ${ageText} / ${durationText} / ${location} / ¥${price}`;
        },
        formSubmit(paymentMethod) {
            this.generateTitle();
            // Collect form data
            const selectedDuration = parseInt(
                this.dropdownOptions.serviceDuration[this.serviceDurationIndex]
            );
            const selectedPrice = parseInt(this.priceOptions[this.priceIndex]);

            // Process requiredGender
            let requiredGenderValue = null;
            if (this.dropdownOptions.gender[this.genderIndex] === '男') {
                requiredGenderValue = 1;
            } else if (this.dropdownOptions.gender[this.genderIndex] === '女') {
                requiredGenderValue = 2;
            }

            // Process requiredAgeMin and requiredAgeMax
            const ageMinStr = this.dropdownOptions.age[0][this.ageRangeIndex[0]];
            const ageMaxStr = this.dropdownOptions.age[1][this.ageRangeIndex[1]];
            const requiredAgeMinValue = ageMinStr === '不限' ? null : parseInt(ageMinStr);
            const requiredAgeMaxValue = ageMaxStr === '不限' ? null : parseInt(ageMaxStr);

            const formData = {
                title: this.title,
                clientId: uni.getStorageSync('userId'),
                requiredServantType: this.serviceType,
                requiredGender: requiredGenderValue,
                requiredAgeMin: requiredAgeMinValue,
                requiredAgeMax: requiredAgeMaxValue,
                serviceDuration: selectedDuration,
                price: selectedPrice,
                status: 0,
                location: this.location.address,
                locationName: this.location.name,
                latitude: this.location.latitude,
                longitude: this.location.longitude,
                paymentMethod: paymentMethod
            };

            console.log('Form Data:', formData);
            // Submit the form data as needed
            uni.request({
                url: getApp().globalData.requestUrl + '/order/save',
                method: 'POST',
                data: formData,
                success: (res) => {
                    uni.showToast({title: '添加成功', icon: 'none'});
                    uni.navigateTo({
                        url: '/pages/me/order/order'
                    });
                },
                fail: (err) => {
                    // Handle error response
                }
            });
        },

    },
};
</script>
