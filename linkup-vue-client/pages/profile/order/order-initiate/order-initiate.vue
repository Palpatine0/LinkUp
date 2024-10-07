<template>
<div>
    <div class="page">
        <app-title type="h1" bold="true">
            {{ orderId ? $t('profile>order>orderInitiate.repostOrder') : $t('profile>order>orderInitiate.postOrder') }}
        </app-title>

        <!-- Gender -->
        <app-title bold="true">{{ $t('profile>order>orderInitiate.serviceGender') }}</app-title>
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
        <app-title bold="true">{{ $t('profile>order>orderInitiate.requiredAge') }}</app-title>
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
                    <template v-if="dropdownOptions.age[0][ageRangeIndex[0]] === $t('profile>order>orderInitiate.unlimited') && dropdownOptions.age[1][ageRangeIndex[1]] === $t('profile>order>orderInitiate.unlimited')">
                        {{ $t('profile>order>orderInitiate.unlimited') }}
                    </template>
                    <template v-else-if="dropdownOptions.age[0][ageRangeIndex[0]] === $t('profile>order>orderInitiate.unlimited')">
                        {{ $t('profile>order>orderInitiate.unlimited') + ' - ' + dropdownOptions.age[1][ageRangeIndex[1]] }}
                    </template>
                    <template v-else-if="dropdownOptions.age[1][ageRangeIndex[1]] === $t('profile>order>orderInitiate.unlimited')">
                        {{ dropdownOptions.age[0][ageRangeIndex[0]] + '岁以上' }}
                    </template>
                    <template v-else>
                        {{ dropdownOptions.age[0][ageRangeIndex[0]] + ' - ' + dropdownOptions.age[1][ageRangeIndex[1]] }}
                    </template>
                </div>
            </picker>
        </div>

        <!-- Service Location -->
        <app-title bold="true">{{ $t('profile>order>orderInitiate.selectLocation') }}</app-title>
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
                {{ $t('profile>order>orderInitiate.selectLocation') }}
            </div>
        </div>

        <!-- Service Duration -->
        <app-title bold="true">{{ $t('profile>order>orderInitiate.serviceDuration') }}</app-title>
        <div class="app-input">
            <picker
                @change="bindServiceDurationPickerChange"
                :value="serviceDurationIndex"
                :range="dropdownOptions.serviceDuration"
            >
                <div>{{ dropdownOptions.serviceDuration[serviceDurationIndex] }}</div>
            </picker>
        </div>

        <!-- Service Schedule -->
        <app-title bold="true">{{ $t('profile>order>orderInitiate.serviceSchedule') }}</app-title>
        <div class="app-input">
            <div v-if="common.isEmpty(this.formData.serviceScheduleStart) || common.isEmpty(this.formData.serviceScheduleEnd)" @click="serviceScheduleToggle">
                {{ $t('profile>order>orderInitiate.selectServiceSchedule') }}
            </div>
            <div v-else @click="serviceScheduleToggle">
                {{ common.stampToTime(this.formData.serviceScheduleStart, {yyyy: false, ss: false}) }}
                -
                {{ common.stampToTime(this.formData.serviceScheduleEnd, {yyyy: false, ss: false, MM: false, dd: false}) }}
            </div>
        </div>

        <!-- Price -->
        <app-title type="h2" bold="true">{{ $t('profile>order>orderInitiate.price') }}</app-title>
        <div class="app-input">
            <picker
                @change="bindPricePickerChange"
                :value="priceIndex"
                :range="priceOptions"
            >
                <div>{{ '¥' + priceOptions[priceIndex] }}</div>
            </picker>
        </div>

        <!-- Submit Button -->
        <div name="submit form" class="center-h">
            <div class="app-button" @click="paymentMethodSelectionToggle">
                {{ orderId ? $t('profile>order>orderInitiate.orderAndPay') : $t('profile>order>orderInitiate.orderAndPay') }}
            </div>
        </div>

    </div>

    <PaymentMethodSelection v-if="paymentMethodSelectionVisible" :user="user" :balanceAdequate="balanceAdequate"></PaymentMethodSelection>
    <ServiceSchedule @change="bindServiceTimeChange" ref="chooseTime" isMask :hour="parseInt(this.dropdownOptions.serviceDuration[this.serviceDurationIndex])"></ServiceSchedule>
    <AddressSelector v-if="addressPickerVisible" @close="addressPickerToggle" @selectAddress="handleAddressSelect"/>
</div>
</template>

<script>
import PaymentMethodSelection from "../../../../components/page/payment/payment-method-selection.vue";
import ServiceSchedule from "../../../../components/page/order/service-schedule.vue";
import AddressSelector from "../../../../components/page/address/address-selector.vue";
import common from "../../../../utils/common";
import $common from "../../../../utils/common";
import app from "../../../../App.vue";

export default {
    computed: {
        common() {
            return common;
        },
    },
    components: {
        PaymentMethodSelection,
        ServiceSchedule,
        AddressSelector,
    },
    data() {
        return {
            orderId: null,
            postOrder: this.$t('profile>order>orderInitiate.postOrder'),
            repostOrder: this.$t('profile>order>orderInitiate.repostOrder'),
            formData: {
                title: "",
                titleCn: "",
                clientId: uni.getStorageSync(getApp().globalData.data.userInfoKey).id,
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
            titleCn: "",

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
                gender: [this.$t('profile>order>orderInitiate.options.all'), this.$t('pub.gender.m'), this.$t('pub.gender.f')],
                age: [
                    [this.$t('profile>order>orderInitiate.options.all')].concat(Array.from({length: 83}, (_, i) => i + 18)), // Ages 18 to 100
                    [this.$t('profile>order>orderInitiate.options.all')].concat(Array.from({length: 83}, (_, i) => i + 18)), // Adjusted in methods
                ],
                serviceDuration: Array.from({length: 24}, (_, i) => `${i + 1}h`),
                price: [],
            },

            // payment
            user: {},
            balanceAdequate: false,
            paymentMethodSelectionVisible: false,
        };
    },
    onLoad(param) {
        console.log("param");
        console.log(param);
        this.formData.requiredServantType = param.serviceType;
        this.serviceName = param.serviceName;
        this.serviceNameCn = param.serviceNameCn;
        if (param.orderId) {
            this.orderId = param.orderId;
            this.getOrderDetail(this.orderId);
        } else {
            this.updatePriceOptions();
            this.generateTitle();
        }
    },
    methods: {
        // Open as repost order
        getOrderDetail(orderId) {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.order.search,
                method: 'POST',
                data: { id: orderId },
                success: (res) => {
                    const order = res.data.list[0];

                    // Update formData with the fetched order details
                    this.formData = {
                        title: order.title,
                        clientId: order.clientId,
                        requiredServantType: order.requiredServantType,
                        requiredGender: order.requiredGender,
                        requiredAgeMin: order.requiredAgeMin,
                        requiredAgeMax: order.requiredAgeMax,
                        serviceDuration: order.serviceDuration,
                        price: order.price,
                        status: order.status,
                        paymentMethod: order.paymentMethod,
                        serviceScheduleStart: null,
                        serviceScheduleEnd: null,
                        addressId: order.addressId,
                    };

                    // Update dropdown indices and other variables based on order data
                    this.setFormIndices(order);

                    // Fetch the address details if needed
                    if (order.addressId) {
                        this.getAddressDetail(order.addressId);
                    }

                    // Fetch user data to validate balance
                    this.getUser().then(() => {
                        this.balanceAdequate = this.$common.balanceAdequateValidation(
                            this.priceOptions[this.priceIndex],
                            this.user.balance
                        );
                    });

                    this.updatePriceOptions();
                },
                fail: (err) => {
                    // Handle error
                    uni.showToast({title: 'Failed to load order details', icon: 'none'});
                }
            });
        },
        setFormIndices(order) {
            // Set genderIndex
            if (this.formData.requiredGender === null) {
                this.genderIndex = 0;
            } else if (order.requiredGender === "1") {
                this.genderIndex = 1;
            } else if (order.requiredGender === "2") {
                this.genderIndex = 2;
            }

            // Set ageRangeIndex
            this.ageRangeIndex = [
                this.dropdownOptions.age[0].indexOf(order.requiredAgeMin !== null ? order.requiredAgeMin : this.$t('profile>order>orderInitiate.options.all')),
                this.dropdownOptions.age[1].indexOf(order.requiredAgeMax !== null ? order.requiredAgeMax : this.$t('profile>order>orderInitiate.options.all'))
            ];

            // Set serviceDurationIndex
            this.serviceDurationIndex = this.dropdownOptions.serviceDuration.indexOf(`${order.serviceDuration}h`);

            // Set service schedule
            this.serviceScheduleStart = null;
            this.serviceScheduleEnd = null;

            // Generate title
            this.generateTitle();
        },
        getAddressDetail(addressId) {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.address.search,
                method: 'POST',
                data: { id: addressId },
                success: (res) => {
                    this.address = res.data.list[0];
                    this.addressSelected = true;
                },
                fail: (err) => {
                    // Handle error
                    uni.showToast({ title: 'Failed to load address', icon: 'none' });
                },
            });
        },

        // Basic info
        getUser() {
            return new Promise((resolve, reject) => {
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.user.search,
                    method: 'POST',
                    data: {
                        id: uni.getStorageSync(app.globalData.data.userInfoKey).id,
                    },
                    success: (res) => {
                        this.user = res.data.list[0];
                        this.balanceAdequate = this.$common.balanceAdequateValidation(
                            this.priceOptions[this.priceIndex],
                            this.user.balance
                        );
                        resolve();
                    },
                    fail: (err) => {
                        reject(err);
                    },
                });
            });
        },

        // Gender
        bindGenderPickerChange(e) {
            this.genderIndex = e.detail.value;
            const selectedGender = this.dropdownOptions.gender[this.genderIndex];
            if (selectedGender === this.$t('profile>order>orderInitiate.options.all')) {
                this.formData.requiredGender = null;
            } else if (selectedGender === this.$t('pub.gender.m')) {
                this.formData.requiredGender = 1;
            } else if (selectedGender === this.$t('pub.gender.f')) {
                this.formData.requiredGender = 2;
            }
            this.generateTitle();
        },

        // Age
        bindAgeRangePickerChange(e) {
            this.ageRangeIndex = e.detail.value;
            const ageMinStr = this.dropdownOptions.age[0][this.ageRangeIndex[0]];
            const ageMaxStr = this.dropdownOptions.age[1][this.ageRangeIndex[1]];
            this.formData.requiredAgeMin = ageMinStr === this.$t('profile>order>orderInitiate.options.all') ? null : parseInt(ageMinStr);
            this.formData.requiredAgeMax = ageMaxStr === this.$t('profile>order>orderInitiate.options.all') ? null : parseInt(ageMaxStr);
            this.generateTitle();
        },
        bindAgeRangeColumnChange(e) {
            const column = e.detail.column;
            const value = e.detail.value;

            if (column === 0) {
                this.ageRangeIndex[0] = value; // Update first column index
                const selectedFromAge = this.dropdownOptions.age[0][value];

                let newSecondColumn;

                if (selectedFromAge === this.$t('profile>order>orderInitiate.options.all')) {
                    // If "不限" is selected, the second column includes "不限" and all ages
                    newSecondColumn = [this.$t('profile>order>orderInitiate.options.all')].concat(
                        Array.from({ length: 83 }, (_, i) => i + 18)
                    );
                } else {
                    // Second column options start from selectedFromAge to 100
                    const fromAgeNumber = parseInt(selectedFromAge);
                    newSecondColumn = this.dropdownOptions.age[0].filter((age) => {
                        return age !== this.$t('profile>order>orderInitiate.options.all') && parseInt(age) >= fromAgeNumber;
                    });
                    // Do not include this.$t('profile>order>orderInitiate.options.all') in the second column
                }

                // Replace the entire age array to ensure reactivity
                this.dropdownOptions.age = [this.dropdownOptions.age[0], newSecondColumn];
                this.ageRangeIndex[1] = 0; // Reset second column index
            } else if (column === 1) {
                this.ageRangeIndex[1] = value; // Update second column index
            }
        },
        bindServiceDurationPickerChange(e) {
            this.serviceDurationIndex = e.detail.value;
            this.formData.serviceDuration = parseInt(this.dropdownOptions.serviceDuration[this.serviceDurationIndex]);
            this.updatePriceOptions();
            this.generateTitle();
        },

        // Price
        updatePriceOptions(orderPrice = null) {
            var selectedDuration = 0;
            if (!$common.isEmpty(this.dropdownOptions.serviceDuration[this.serviceDurationIndex])) {
                selectedDuration = this.dropdownOptions.serviceDuration[this.serviceDurationIndex];
            } else {
                selectedDuration = this.formData.serviceDuration;
            }

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

            // Include orderPrice if it's not already in the prices array
            if (orderPrice !== null && !prices.includes(orderPrice)) {
                prices.push(orderPrice);
                prices.sort((a, b) => a - b); // Sort the prices in ascending order
            }

            // No '¥' in the data, only for display
            this.priceOptions = prices;
            // Set priceIndex to 0 only when creating a new order
            if (!this.orderId) {
                this.priceIndex = 0;
            }
            this.formData.price = this.priceOptions[this.priceIndex];

            // Update balanceAdequate
            this.balanceAdequate = this.$common.balanceAdequateValidation(
                this.priceOptions[this.priceIndex],
                this.user.balance
            );
        },
        bindPricePickerChange(e) {
            this.priceIndex = e.detail.value;
            this.formData.price = this.priceOptions[this.priceIndex];
            this.balanceAdequate = this.$common.balanceAdequateValidation(
                this.priceOptions[this.priceIndex],
                this.user.balance
            );
            this.generateTitle();
        },

        // Address
        addressPickerToggle() {
            this.addressPickerVisible = !this.addressPickerVisible;
        },
        handleAddressSelect(address) {
            this.address = address;
            this.addressSelected = true;
            this.formData.addressId = address.id;
            this.addressPickerToggle();
        },

        // Service time
        serviceScheduleToggle() {
            this.$refs.chooseTime.open();
        },
        bindServiceTimeChange(e) {
            console.log("bindServiceTimeChange(e) {bindServiceTimeChange(e) {")
            console.log(e)
            this.serviceScheduleStart = this.$common.timeToStamp(`${e.day} ${e.startHour}`);
            this.serviceScheduleEnd = this.$common.timeToStamp(`${e.day} ${e.endHour}`);
            this.formData.serviceScheduleStart = this.serviceScheduleStart;
            this.formData.serviceScheduleEnd = this.serviceScheduleEnd;
            this.generateTitle();
        },

        // Payment
        paymentMethodSelectionToggle() {
            if(this.formData.addressId === 0) {
                uni.showToast({
                    title: this.$t('profile>order>orderInitiate.showToast.selectAddr'),
                    icon: 'none',
                });
                return;
            }
            if(this.$common.isEmpty(this.formData.serviceScheduleStart) || this.$common.isEmpty(this.formData.serviceScheduleEnd)) {
                uni.showToast({
                    title: this.$t('profile>order>orderInitiate.showToast.selectSchedule'),
                    icon: 'none',
                });
                return;
            }
            this.getUser().then(() => {
                this.paymentMethodSelectionVisible = !this.paymentMethodSelectionVisible;
            });
        },

        // Submit
        generateTitle() {
            const {gender, age, serviceDuration} = this.dropdownOptions;
            const {genderIndex, ageRangeIndex, serviceDurationIndex} = this;
            const schedule = this.$common.stampToTime(this.formData.serviceScheduleStart, {yyyy: false, ss: false}) + " -" + this.$common.stampToTime(this.formData.serviceScheduleEnd, {yyyy: false, ss: false, MM: false, dd: false})
            // International version
            const genderText = gender[genderIndex] === this.$t('profile>order>orderInitiate.options.all') ? "All gender" : gender[genderIndex] === this.$t('pub.gender.m') ? "Male" : "Female";
            const ageMin = age[0][ageRangeIndex[0]] === this.$t('profile>order>orderInitiate.options.all') ? "All" : `${age[0][ageRangeIndex[0]]} and Above`;
            const ageMax = age[1][ageRangeIndex[1]] === this.$t('profile>order>orderInitiate.options.all') ? "All" : `${age[1][ageRangeIndex[1]]} and Below`;
            var ageText = ""
            if (age[0][ageRangeIndex[0]] === age[1][ageRangeIndex[1]]) {
                ageText = `Age ${age[0][ageRangeIndex[0]]}`;
            } else {
                ageText = ageMin === "All" && ageMax === "All" ? "All age" : `${ageMin} - ${ageMax}`;
            }
            const durationText = serviceDuration[serviceDurationIndex];
            const price = this.priceOptions[this.priceIndex];
            const location = !this.$common.isEmpty(this.address) ? this.address.addressName : 'All Location';
            this.title = `${this.serviceName}: ${genderText} / ${ageText} / ${durationText} / ${location} / ${schedule} / ¥${price}`;

            // CN version
            const genderTextCn = gender[genderIndex] === this.$t('profile>order>orderInitiate.options.all') ? "不限性别" : gender[genderIndex] === this.$t('pub.gender.m') ? "男性" : "女性";
            const ageMinCn = age[0][ageRangeIndex[0]] === this.$t('profile>order>orderInitiate.options.all') ? "不限" : `${age[0][ageRangeIndex[0]]}岁以上`;
            const ageMaxCn = age[1][ageRangeIndex[1]] === this.$t('profile>order>orderInitiate.options.all') ? "不限" : `${age[1][ageRangeIndex[1]]}岁以下`;
            var ageTextCn = ""
            if (age[0][ageRangeIndex[0]] === age[1][ageRangeIndex[1]]) {
                ageTextCn = `${age[0][ageRangeIndex[0]]}岁`;
            } else {
                ageTextCn =  ageMinCn === "不限" && ageMaxCn === "不限" ? "不限年龄" : `${ageMinCn} - ${ageMaxCn}`;
            }
            const durationTextCn = serviceDuration[serviceDurationIndex];
            const priceCn = this.priceOptions[this.priceIndex];
            const locationCn = !this.$common.isEmpty(this.address) ? this.address.addressName : '不限地区';
            this.titleCn = `${this.serviceNameCn}服务: ${genderTextCn} / ${ageTextCn} / ${durationTextCn} / ${locationCn} / ${schedule} / ¥${priceCn}`;
        },
        formSubmit(paymentMethod) {
            this.generateTitle();
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.order.save,
                method: 'POST',
                data: {
                    ...this.formData,
                    title: this.title,
                    titleCn: this.titleCn,
                    paymentMethod: paymentMethod,
                },
                success: (res) => {
                    uni.redirectTo({
                        url: '/pages/profile/order/order',
                    });
                },
            });
        },
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