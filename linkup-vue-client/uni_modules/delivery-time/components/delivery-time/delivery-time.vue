<template>
<view v-if="days && days.length">
    <!-- Pop-up Layer -->
    <view :class="scrollClass">
        <view class="time-title" :style="{ borderRadius: getRadius }">
            <span @click.stop="close">取消</span>
            {{ title }}
            <text @click.stop="confirm" :style="selectedTimeIndex !== null ? 'color:#007aff;' : ''">确定</text>
        </view>
        <view class="time-picker" :style="{ height: height }">
            <scroll-view class="date-scroll" scroll-y>
                <view class="date-item" v-for="(item, index) in days" :key="index">
                    <view class="date" :class="{ active: selectedIndex === index }" @click="handleDateClick(index)">
                        {{ item.day }}
                    </view>
                </view>
            </scroll-view>
            <scroll-view class="time-scroll" scroll-y>
                <view class="time-item" v-for="(time, index) in selectedDay.timeList" :key="index">
                    <view class="time" :class="{ active: selectedTimeIndex === index }" @click="handleTimeClick(index)">
                        {{ time.time }}
                    </view>
                </view>
            </scroll-view>
        </view>
    </view>
    <view v-show="isShow" class="scroll-mask" @click="isMask ? close() : ''"></view>
</view>
</template>

<script>
export default {
    props: {
        // Title
        title: {
            type: String,
            default: '请选择预约时间',
        },
        // Box height
        height: {
            type: String,
            default: '500rpx',
        },
        // Display the number of upcoming days
        day: {
            type: Number,
            default: 7,
        },
        // Whether clicking the shadow closes the popup
        isMask: {
            type: Boolean,
            default: true,
        },
        // Whether to enable animation
        animation: {
            type: Boolean,
            default: true,
        },
        // Whether to enable the safe area
        safeArea: {
            type: Boolean,
            default: true,
        },
        // Border radius
        radius: {
            type: String,
            default: '24rpx',
        },
        // **New prop: Hour value for time intervals**
        hour: {
            type: Number,
            default: 2, // Default interval of 2 hours
        },
    },
    data() {
        return {
            isShow: false,
            selectedIndex: 0,
            selectedDay: {},
            selectedTimeIndex: null,
            days: [],
        };
    },
    mounted() {
        this.days = this.getFutureDays();
        this.selectedDay = this.days[0];
    },
    computed: {
        scrollClass() {
            const classes = ['scroll-popup'];
            if (this.isShow) classes.push('scroll-open');
            if (this.animation) classes.push('scroll-animation');
            if (this.safeArea) classes.push('scroll-temp');
            return classes;
        },
        getRadius() {
            return `${this.radius} ${this.radius} 0 0`;
        },
    },
    watch: {
        hour(newVal) {
            // Recalculate days when hour prop changes
            this.days = this.getFutureDays();
            this.selectedDay = this.days[0];
            this.selectedTimeIndex = null; // Reset selected time index
        },
    },

    methods: {
        // Format time to 'HH:MM'
        formatTime(date) {
            const hours = date.getHours().toString().padStart(2, '0');
            const minutes = date.getMinutes().toString().padStart(2, '0');
            return `${hours}:${minutes}`;
        },
        // Get the next half-hour mark after a given time
        getNextHalfHour(date) {
            let minutes = date.getMinutes();
            let additionalMinutes = 0;
            if (minutes === 0 || minutes === 30) {
                additionalMinutes = 0;
            } else if (minutes < 30) {
                additionalMinutes = 30 - minutes;
            } else {
                additionalMinutes = 60 - minutes;
            }
            let nextHalfHour = new Date(date.getTime() + additionalMinutes * 60 * 1000);
            return nextHalfHour;
        },
        // Generate slots from a start time, with specified interval and duration
        getStandardSlotsFromTime(startTime, intervalMinutes, durationHours) {
            const slots = [];
            const endOfDay = new Date(startTime);
            endOfDay.setHours(24, 0, 0, 0);

            let slotStart = new Date(startTime);
            while (slotStart < endOfDay) {
                const slotEnd = new Date(slotStart.getTime() + durationHours * 60 * 60 * 1000);
                if (slotEnd > endOfDay) {
                    break;
                }
                slots.push({
                    start: new Date(slotStart),
                    end: new Date(slotEnd),
                });
                slotStart = new Date(slotStart.getTime() + intervalMinutes * 60 * 1000);
            }
            return slots;
        },
        // Construct time data
        getFutureDays() {
            const days = [];
            for (let i = 0; i < this.day; i++) {
                const date = new Date();
                date.setDate(date.getDate() + i);
                const year = date.getFullYear();
                const month = (date.getMonth() + 1).toString().padStart(2, '0');
                const day = date.getDate().toString().padStart(2, '0');

                const timeList = [];

                if (i === 0) {
                    // Today
                    let currentTime = new Date();
                    // First slot starts from current time + 1 hour, rounded up to the next half-hour mark
                    let startTime = new Date(currentTime.getTime() + 1 * 60 * 60 * 1000);
                    startTime = this.getNextHalfHour(startTime);

                    let endTime = new Date(startTime.getTime() + this.hour * 60 * 60 * 1000);

                    // Adjust endTime if it goes past midnight
                    if (endTime.getDate() !== startTime.getDate()) {
                        endTime = new Date(startTime);
                        endTime.setHours(24, 0, 0, 0); // Midnight
                    }

                    timeList.push({
                        time: `${this.formatTime(startTime)}-${this.formatTime(endTime)}`,
                        start: this.formatTime(startTime),
                        end: this.formatTime(endTime),
                    });

                    // Generate subsequent slots at intervals equal to the service duration
                    let nextSlotStartTime = new Date(endTime);

                    const standardSlots = this.getStandardSlotsFromTime(nextSlotStartTime, this.hour * 60, this.hour);

                    for (const slot of standardSlots) {
                        timeList.push({
                            time: `${this.formatTime(slot.start)}-${this.formatTime(slot.end)}`,
                            start: this.formatTime(slot.start),
                            end: this.formatTime(slot.end),
                        });
                    }

                } else {
                    // Future days
                    const dateAtMidnight = new Date(date);
                    dateAtMidnight.setHours(0, 0, 0, 0);
                    const standardSlots = this.getStandardSlotsFromTime(dateAtMidnight, this.hour * 60, this.hour);

                    for (const slot of standardSlots) {
                        timeList.push({
                            time: `${this.formatTime(slot.start)}-${this.formatTime(slot.end)}`,
                            start: this.formatTime(slot.start),
                            end: this.formatTime(slot.end),
                        });
                    }
                }

                days.push({
                    day: `${year}-${month}-${day}`,
                    timeList: timeList,
                });
            }
            return days;
        },


        open() {
            this.isShow = true;
            this.init();
        },
        init() {
            this.selectedIndex = 0;
            this.selectedDay = this.days[0];
            this.selectedTimeIndex = null;
        },
        close() {
            this.isShow = false;
        },
        handleDateClick(index) {
            this.selectedIndex = index;
            this.selectedDay = this.days[index];
            this.selectedTimeIndex = null;
        },
        handleTimeClick(index) {
            this.selectedTimeIndex = index;
        },
        confirm() {
            if (this.selectedTimeIndex === null) {
                uni.showToast({
                    title: '请选择预约时间段',
                    icon: 'none',
                });
                return;
            }
            const time = this.selectedDay.timeList[this.selectedTimeIndex];
            this.close();
            this.$emit('change', {
                day: this.selectedDay.day,
                time: time.time,
                startHour: time.start,
                endHour: time.end,
                value: `${this.selectedDay.day} ${time.time}`,
            });
        },
    },
};
</script>

<style>
/deep/ ::-webkit-scrollbar {
    width: 0;
    height: 0;
    color: transparent;
    display: none;
}
</style>
<style scoped>
.scroll-popup {
    width: 100%;
    position: fixed;
    bottom: -100%;
    z-index: 999;
}

.scroll-open {
    bottom: 0px !important;
}

.scroll-animation {
    transition: all 0.25s linear;
}

.scroll-temp {
    padding-bottom: 0;
}

/* Mask layer style */
.scroll-mask {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    z-index: 998;
}

.time-picker {
    display: flex;
    align-items: center;
    color: #666666;
    background-color: #fff;
}

.time-title {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    font-size: 32rpx;
    font-weight: bold;
    color: #333333;
    background-color: #ffffff;
    text-align: center;
    position: relative;
    border-bottom: 1rpx solid #f6f6f6;
}

.time-title span {
    font-size: 28rpx;
    font-weight: 400;
    position: absolute;
    left: 32rpx;
    top: 0;
    bottom: 0;
    margin: auto 0;
    color: #666666;
}

.time-title text {
    font-size: 28rpx;
    font-weight: 400;
    position: absolute;
    right: 32rpx;
    top: 0;
    bottom: 0;
    margin: auto 0;
    color: #666666;
}

.date-scroll {
    flex: 1;
    height: 100%;
    border-right: 1rpx solid #f6f6f6;
    box-sizing: border-box;
}

.date-item {
    height: 88rpx;
    line-height: 88rpx;
    text-align: center;
    box-sizing: border-box;
    border-bottom: 1rpx solid #f6f6f6;
}

.date {
    font-size: 28rpx;
}

.date.active {
    font-size: 30rpx;
    font-weight: bold;
    color: #007aff;
}

.time-scroll {
    flex: 1;
    height: 100%;
}

.time-item {
    height: 88rpx;
    line-height: 88rpx;
    text-align: center;
    box-sizing: border-box;
    border-bottom: 1rpx solid #f6f6f6;
}

.time {
    font-size: 28rpx;
}

.time.active {
    font-size: 30rpx;
    font-weight: bold;
    color: #007aff;
}
</style>
