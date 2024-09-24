<template>
<div>
    <div class="page">
        <app-title type="h1" bold="true">发布订单</app-title>

        <!-- Gender -->
        <app-title bold="true">服务者性别</app-title>
        <div class="app-input">
            <picker
                @change="bindGenderPickerChange"
                :value="genderIndex"
                :range="dropdownOptions.gender"
            >
                <div>{{ dropdownOptions.gender[genderIndex] }}</div>
            </picker>
        </div>

        <!-- Age Range -->
        <app-title bold="true">服务者年龄</app-title>
        <div class="app-input">
            <picker
                :key="dropdownOptions.age[0][ageRangeIndex[0]] + '-' + dropdownOptions.age[1][0]"
                mode="multiSelector"
                :range="dropdownOptions.age"
                :value="ageRangeIndex"
                @change="bindAgeRangePickerChange"
                @columnchange="bindAgeRangeColumnChange"
            >
                <div class="uni-input">
                    <template v-if="dropdownOptions.age[0][ageRangeIndex[0]] === '不限' &&dropdownOptions.age[1][ageRangeIndex[1]] === '不限'">
                        不限
                    </template>
                    <template v-else-if="dropdownOptions.age[0][ageRangeIndex[0]] === '不限'">
                        {{ '不限 - ' + dropdownOptions.age[1][ageRangeIndex[1]] }}
                    </template>
                    <template v-else-if="dropdownOptions.age[1][ageRangeIndex[1]] === '不限'">
                        {{ dropdownOptions.age[0][ageRangeIndex[0]] + '岁以上' }}
                    </template>
                    <template v-else>
                        {{ dropdownOptions.age[0][ageRangeIndex[0]] + ' - ' + dropdownOptions.age[1][ageRangeIndex[1]] }}
                    </template>
                </div>
            </picker>
        </div>

        <!-- location -->
        <app-title bold="true">服务地点</app-title>
        <div class="app-input" @click="addressPickerToggle">
            <div v-if="addressSelected" class="address-content">
                <div style="width: 100%;">
                    <div style="display: flex; align-items: center; justify-content: space-between;">
                        <app-title bold="true" type="h3" style="width: 330px;">{{ address.consignee }}</app-title>
                        <span class="phone-number">{{ address.phoneNumber }}</span>
                    </div>
                    <div class="address-info">
                        <div class="address-details">
                            {{ address.addressName }}
                        </div>
                        <span style="font-size: 14px; color: gray;">{{ address.detail }}</span>
                    </div>
                </div>
            </div>
            <div v-else>
                请选择服务地点
            </div>
        </div>

        <!-- Service Duration -->
        <app-title bold="true">服务时长</app-title>
        <div class="app-input">
            <picker
                @change="bindServiceDurationPickerChange"
                :value="serviceDurationIndex"
                :range="dropdownOptions.serviceDuration"
            >
                <div>{{ dropdownOptions.serviceDuration[serviceDurationIndex] }}</div>
            </picker>
        </div>
        <app-title bold="true">服务时间</app-title>
        <div class="app-input">
            <div v-if="common.isEmpty(this.formData.serviceScheduleStart) || common.isEmpty(this.formData.serviceScheduleEnd)" @click="open">请选择服务时间</div>
            <div v-else @click="open">
                {{
                    common.stampToTime(this.formData.serviceScheduleStart, {yyyy: false, ss: false})
                }}
                -
                {{
                    common.stampToTime(this.formData.serviceScheduleEnd, {yyyy: false, ss: false, MM: false, dd: false})
                }}
            </div>
        </div>

        <!-- Price -->
        <app-title type="h2" bold="true">价格</app-title>
        <div class="app-input">
            <picker
                @change="bindPricePickerChange"
                :value="priceIndex"
                :range="priceOptions"
            >
                <div>{{ priceOptions[priceIndex] }}</div>
            </picker>
        </div>

        <!-- Submit Button -->
        <div name="submit form" class="center_h">
            <div class="app-button" @click="paymentMethodSelectionToggle">发布</div>
        </div>

        <PaymentMethodSelection v-if="paymentMethodSelectionVisible" :user="user" :balanceAdequate="balanceAdequate"></PaymentMethodSelection>


    </div>
    <ServiceSchedule @change="bindServiceTimeChange" ref="chooseTime" title="请选择预约时间" isMask :hour="parseInt(this.dropdownOptions.serviceDuration[this.serviceDurationIndex])"></ServiceSchedule>
    <AddressSelector v-if="addressPickerVisible" @close="addressPickerToggle" @selectAddress="handleAddressSelect"/>
</div>
</template>

<script>
import PaymentMethodSelection from "../../../../components/page/payment/payment-method-selection.vue";
import ServiceSchedule from "../../../../components/page/order/service-schedule.vue";
import AddressSelector from "../../../../components/page/address/address-selector.vue";
import common from "../../../../utils/common";

export default {
    computed: {
        common() {
            return common
        }
    },
    components: {PaymentMethodSelection, ServiceSchedule, AddressSelector},
    data() {
        return {
            formData: {
                title: "",
                clientId: uni.getStorageSync('userId'),
                requiredServantType: null,
                requiredGender: null,
                requiredAgeMin: null,
                requiredAgeMax: null,
                serviceDuration: 1,
                price: 200,
                status: 0,
                paymentMethod: "",
                serviceScheduleStart: null,
                serviceScheduleEnd: null,
                addressId: 0,
            },

            // basic info
            title: "",

            // gender
            genderIndex: 0,

            // age
            ageRangeIndex: [0, 0],

            // service duration
            serviceDurationIndex: 0,

            // price
            priceIndex: 0,
            priceOptions: [],

            // address
            addressPickerVisible: false, // Pop-up visibility state
            addressSelected: false,
            address: {},

            // dropdown options
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
        this.formData.requiredServantType = param.serviceType;
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
            if (this.dropdownOptions.gender[this.genderIndex] === '男') {
                this.formData.requiredGender = 1;
            } else if (this.dropdownOptions.gender[this.genderIndex] === '女') {
                this.formData.requiredGender = 2;
            }
        },

        // age
        bindAgeRangePickerChange(e) {
            this.ageRangeIndex = e.detail.value;
            const ageMinStr = this.dropdownOptions.age[0][this.ageRangeIndex[0]];
            const ageMaxStr = this.dropdownOptions.age[1][this.ageRangeIndex[1]];
            this.formData.requiredAgeMax = ageMinStr === '不限' ? null : parseInt(ageMinStr);
            this.formData.requiredAgeMin = ageMaxStr === '不限' ? null : parseInt(ageMaxStr);
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
            this.formData.serviceDuration = parseInt(this.dropdownOptions.serviceDuration[this.serviceDurationIndex]);
            this.updatePriceOptions();
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

            this.priceOptions = prices.map((price) => `${price}元`);
            this.priceIndex = 0;

        },
        bindPricePickerChange(e) {
            this.priceIndex = e.detail.value;
            this.formData.price = parseInt(this.priceOptions[this.priceIndex])
        },

        // location
        addressPickerToggle() {
            this.addressPickerVisible = !this.addressPickerVisible;
        },
        handleAddressSelect(address) {
            this.address = address;
            this.addressSelected = true;
            this.formData.addressId = address.id
            this.addressPickerToggle();
        },

        // service time
        open() {
            this.$refs.chooseTime.open()
        },
        bindServiceTimeChange(e) {
            this.serviceScheduleStart = this.$common.timeToStamp(`${e.day} ${e.startHour}`);
            this.serviceScheduleEnd = this.$common.timeToStamp(`${e.day} ${e.endHour}`);
            this.formData.serviceScheduleStart = this.$common.timeToStamp(this.serviceScheduleStart);
            this.formData.serviceScheduleEnd = this.$common.timeToStamp(this.serviceScheduleEnd);
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
            if (this.$common.isEmpty(selectedPrice) || this.$common.isEmpty(balance)) {
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

            const genderText = gender[genderIndex] === '不限' ? '不限性别' : gender[genderIndex] === '男' ? '男' : '女';
            const ageMin = age[0][ageRangeIndex[0]] === '不限' ? '不限' : `${age[0][ageRangeIndex[0]]}岁以上`;
            const ageMax = age[1][ageRangeIndex[1]] === '不限' ? '不限' : `${age[1][ageRangeIndex[1]]}岁以下`;
            const ageText = ageMin === '不限' && ageMax === '不限' ? '不限年龄' : `${ageMin} - ${ageMax}`;
            const durationText = serviceDuration[serviceDurationIndex];
            const price = this.priceOptions[this.priceIndex];
            const location = !this.common.isEmpty(this.address) ? this.address.addressName : '不限地区';

            // Update the title with the concatenated values
            this.title = `${this.serviceName}服务: ${genderText} / ${ageText} / ${durationText} / ${location} / ¥${price}`;
        },
        formSubmit(paymentMethod) {
            this.generateTitle();
            const param = {
                ...this.formData,
                title: this.title,
                paymentMethod: paymentMethod,
            };
            uni.request({
                url: getApp().globalData.requestUrl + '/order/save',
                method: 'POST',
                data: param,
                success: (res) => {
                    uni.showToast({title: '添加成功', icon: 'none'});
                    uni.navigateTo({
                        url: '/pages/profile/order/order'
                    });
                },
                fail: (err) => {
                    // Handle error response
                }
            });
        }


    },
};
</script>
<style scoped>
// address
.address-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.address-info {
    margin-top: 5px;
}

.address-details {
    color: #555;
}

.phone-number {
    font-size: 14px;
    color: gray;
    margin-left: 10px;
}

</style>