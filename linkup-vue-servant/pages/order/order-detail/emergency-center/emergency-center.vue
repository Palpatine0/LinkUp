<template>
<div class="page" style="background-color: #ed3a3a">
    <!-- Alert Card -->
    <div class="app-container" style="background-color: white;">
        <div class="location">
            <img style="width: 20px;height: 20px" src="/static/page/order/emergency-center/location-dot.svg">
            <div style="font-weight: bold;font-size: 20px;margin: 0px 6px;">Warsaw, Poland</div>
        </div>
        <div class="alert-title">{{ $t('profile>order>orderDetail>emergencyCenter.title') }}</div>
        <p class="alert-description">
            {{ $t('profile>order>orderDetail>emergencyCenter.description') }}
        </p>
    </div>

    <app-title type="h3" bold="true" style="color: white">{{ $t('profile>order>orderDetail>emergencyCenter.popularServices') }}</app-title>

    <div class="app-container flex" style="background-color: white;">
        <div class="service-icon">
            <img style="width: 35px;height: 35px" src="/static/page/order/emergency-center/siren-on.png">
        </div>
        <div>
            <div style="font-weight: bold;color:#ed3a3a;font-size: 18px">{{ $t('profile>order>orderDetail>emergencyCenter.emergencyAssist') }}</div>
            <div style="font-size: 14px">{{ $t('profile>order>orderDetail>emergencyCenter.callToLocalAuthority') }}</div>
        </div>
    </div>

    <div class="app-container flex" style="background-color: white;">
        <div class="service-icon">
            <img style="width: 26px;height: 40px;margin: 0 4px;" src="/static/page/order/emergency-center/phone-volume-solid.svg">
        </div>
        <div>
            <div style="font-weight: bold;font-size: 18px">{{ $t('profile>order>orderDetail>emergencyCenter.shareDetails') }}</div>
            <div style="font-size: 14px">{{ $t('profile>order>orderDetail>emergencyCenter.letFriendTrack') }}</div>
        </div>
    </div>

</div>
</template>

<script>
export default {
    name: "emergency-center",
};
</script>

<style scoped>

.location {
    display: flex;
    align-items: center;
    font-size: 14px;
    margin-bottom: 10px;
}

.location-icon {
    margin-right: 8px;
}

.alert-title {
    color: #ed3a3a;
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 10px;
}

.alert-description {
    font-size: 14px;
    margin-bottom: 20px;
}

.service-icon {
    margin: 0 18px 0 6px;
    font-size: 24px;
    margin-bottom: 10px;
}

</style>
