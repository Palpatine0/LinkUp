<template>
<div class="share-container">
    <div class="mask" @click="close()"></div>
    <div class="widget-center coup-anim">
        <div class="share-header">{{$t('component>balance>referralQRCode.yourReferralQRCode')}}</div>
        <div class="qrcode-container">
            <img class="qrcode" :src="userInfo.referralQrCode" alt="QR Code">
            <div class="share-tip">{{$t('component>balance>referralQRCode.descA')}}</div>
            <div class="share-tip">{{$t('component>balance>referralQRCode.descB')}}</div>
        </div>
    </div>
</div>
</template>

<script>

export default {
    props: {
        userInfo: Object
    },
    data() {
        return {
        }
    },
    methods: {
        close() {
            this.$parent.qeShareToggle(false);
        },
    },
};
</script>

<style scoped>
.share-container {
    z-index: 1000;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
}


.share-header {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
}

.qrcode-container {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.qrcode {
    width: 200px;
    height: 200px;
    margin-bottom: 10px;
}

.share-tip {
    font-size: 14px;
    color: #555;
    margin-top: 10px;
}

.info {
    margin: 20px 0;
}

.username {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 10px;
}

.credit {
    font-size: 16px;
    color: #666;
}

.withdraw-button {
    width: 100%;
    padding: 10px 0;
    background-color: #812740;
    color: #fff;
    border: none;
    border-radius: 10px;
    font-size: 18px;
    cursor: pointer;
}

.withdraw-button:hover {
    background-color: #812740;
}
</style>