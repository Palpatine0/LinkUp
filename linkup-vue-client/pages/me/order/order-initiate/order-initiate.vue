<template>
<div class="page">
    <app-title type="h1" bold="true">发布订单</app-title>

    <!-- Open Time Pickers -->
    <app-title bold="true">开放时间</app-title>
    <div style="display: flex; width: 100%; justify-content: space-between">
        <!-- Open Start Date and Time -->
        <div class="app-container" style="width: 48%;">
            <picker mode="date" :value="dropdownOptions.startDate" @change="bindStartDateChange">
                <view>{{ dropdownOptions.startDate }}</view>
            </picker>
            <picker mode="time" :value="dropdownOptions.startTime" @change="bindStartTimeChange">
                <view>{{ dropdownOptions.startTime }}</view>
            </picker>
        </div>
        <!-- Open End Date and Time -->
        <div class="app-container" style="width: 48%;">
            <picker mode="date" :value="dropdownOptions.endDate" @change="bindEndDateChange">
                <view>{{ dropdownOptions.endDate }}</view>
            </picker>
            <picker mode="time" :value="dropdownOptions.endTime" @change="bindEndTimeChange">
                <view>{{ dropdownOptions.endTime }}</view>
            </picker>
        </div>
    </div>

    <!-- Gender Picker -->
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

    <!-- Age Range Picker -->
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

    <!-- Service Duration Picker -->
    <app-title bold="true">服务时长</app-title>
    <view class="app-input">
        <picker
            @change="bindServiceDurationPickerChange"
            :value="serviceDurationIndex"
            :range="dropdownOptions.serviceDuration"
        >
            <view>{{ dropdownOptions.serviceDuration[serviceDurationIndex] }}</view>
        </picker>
    </view>

    <!-- Price Picker -->
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
</template>

<script>
import PaymentMethodSelection from "../../../../components/page/payment/payment-method-selection.vue";

export default {
    components: {PaymentMethodSelection},
    data() {
        return {
            title: "",
            serviceType: '',
            serviceName: '',
            genderIndex: 0,
            ageRangeIndex: [0, 0],
            serviceDurationIndex: 0,
            priceIndex: 0,
            priceOptions: [],
            dropdownOptions: {
                gender: ['不限', '男', '女'],
                age: [
                    ['不限'].concat(Array.from({length: 83}, (_, i) => i + 18)), // Ages 18 to 100
                    ['不限'].concat(Array.from({length: 83}, (_, i) => i + 18)), // Adjusted in methods
                ],
                serviceDuration: Array.from({length: 24}, (_, i) => `${i + 1}小时`),
                price: [],
                startDate: '',
                startTime: '',
                endDate: '',
                endTime: '',
            },

            user: {},
            paymentMethodSelectionVisible: false,
            balanceAdequate: false,
        };
    },
    onLoad(param) {
        this.serviceType = param.serviceType;
        this.serviceName = param.serviceName;
        const currentDate = this.getCurrentDate();
        const currentTime = this.getCurrentTime();
        // Initialize start and end date/time to current date and time
        this.dropdownOptions.startDate = currentDate;
        this.dropdownOptions.startTime = currentTime;
        this.dropdownOptions.endDate = currentDate;
        this.dropdownOptions.endTime = currentTime;
        this.updatePriceOptions(); // Initialize price options
        this.generateTitle();
    },
    methods: {
        // Get current date in 'YYYY-MM-DD' format
        getCurrentDate() {
            const date = new Date();
            const year = date.getFullYear();
            const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Months are 0-based
            const day = date.getDate().toString().padStart(2, '0');
            return `${year}-${month}-${day}`;
        },
        // Get current time in 'HH:mm' format
        getCurrentTime() {
            const date = new Date();
            const hours = date.getHours().toString().padStart(2, '0');
            const minutes = date.getMinutes().toString().padStart(2, '0');
            return `${hours}:${minutes}`;
        },
        // Combine date and time into a Date object
        combineDateTime(dateStr, timeStr) {
            return new Date(`${dateStr}T${timeStr}:00`);
        },
        // Handle start date change
        bindStartDateChange(e) {
            this.dropdownOptions.startDate = e.detail.value;
            this.adjustEndDateTime();
        },
        // Handle start time change
        bindStartTimeChange(e) {
            this.dropdownOptions.startTime = e.detail.value;
            this.adjustEndDateTime();
        },
        // Handle end date change
        bindEndDateChange(e) {
            this.dropdownOptions.endDate = e.detail.value;
            this.validateEndDateTime();
        },
        // Handle end time change
        bindEndTimeChange(e) {
            this.dropdownOptions.endTime = e.detail.value;
            this.validateEndDateTime();
        },
        // Adjust end date and time based on start date and time
        adjustEndDateTime() {
            const startDateTime = this.combineDateTime(
                this.dropdownOptions.startDate,
                this.dropdownOptions.startTime
            );
            const endDateTime = this.combineDateTime(
                this.dropdownOptions.endDate,
                this.dropdownOptions.endTime
            );

            // If end datetime is before start datetime, adjust end datetime
            if (endDateTime < startDateTime) {
                // Set end datetime to start datetime
                this.dropdownOptions.endDate = this.dropdownOptions.startDate;
                this.dropdownOptions.endTime = this.dropdownOptions.startTime;
            }
        },
        // Validate end date and time
        validateEndDateTime() {
            const startDateTime = this.combineDateTime(
                this.dropdownOptions.startDate,
                this.dropdownOptions.startTime
            );
            const endDateTime = this.combineDateTime(
                this.dropdownOptions.endDate,
                this.dropdownOptions.endTime
            );

            // If end datetime is before start datetime, show an error and adjust
            if (endDateTime < startDateTime) {
                uni.showToast({
                    title: '结束时间不能早于开始时间',
                    icon: 'none',
                });
                // Adjust end datetime to start datetime
                this.dropdownOptions.endDate = this.dropdownOptions.startDate;
                this.dropdownOptions.endTime = this.dropdownOptions.startTime;
            }
        },
        // Handle gender picker change
        bindGenderPickerChange(e) {
            this.genderIndex = e.detail.value;
        },
        // Handle age range picker change
        bindAgeRangePickerChange(e) {
            this.ageRangeIndex = e.detail.value;
            const fromAge = this.dropdownOptions.age[0][this.ageRangeIndex[0]];
            const toAge = this.dropdownOptions.age[1][this.ageRangeIndex[1]];

            console.log(`Age Range Selected: ${fromAge} - ${toAge}`);
        },
        // Handle age range column change
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
        // Handle service duration picker change
        bindServiceDurationPickerChange(e) {
            this.serviceDurationIndex = e.detail.value;
            const selectedDuration = this.dropdownOptions.serviceDuration[this.serviceDurationIndex];
            console.log(`Selected service duration: ${selectedDuration}`);
            this.updatePriceOptions(); // Update price options based on the selected duration
        },
        // Update price options based on selected duration
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
        // Handle price picker change
        bindPricePickerChange(e) {
            this.priceIndex = e.detail.value;
            console.log(`Selected price: ${this.priceOptions[this.priceIndex]}`);
        },
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


        paymentMethodSelectionToggle() {
            this.getUser().then(() => {
                // Then toggle the visibility after user data has been fetched
                this.paymentMethodSelectionVisible = !this.paymentMethodSelectionVisible;
            });
        },

        getUser() {
            return new Promise((resolve, reject) => {
                uni.request({
                    url: getApp().globalData.requestUrl + '/user/search',
                    method: 'POST',
                    data: {
                        id: uni.getStorageSync("userId")
                    },
                    success: (res) => {
                        this.user = res.data.userList[0]
                        this.balanceAdequateValidation();
                        resolve();
                    },
                    fail: (err) => {
                        reject(err);
                    }
                });
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
                effectiveAt: this.common.timeToStamp(`${this.dropdownOptions.startDate} ${this.dropdownOptions.startTime}`),
                expireAt: this.common.timeToStamp(`${this.dropdownOptions.endDate} ${this.dropdownOptions.endTime}`),
                requiredServantType: this.serviceType,
                requiredGender: requiredGenderValue,
                requiredAgeMin: requiredAgeMinValue,
                requiredAgeMax: requiredAgeMaxValue,
                serviceDuration: selectedDuration,
                price: selectedPrice,
                status: 0,
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
