<template>
<div>
    <div class="mask" @click="close()"></div>
    <div class="widget-popup coup-anim">
        <div style="display: flex;margin: 18px 15px 4px 14px;justify-content: space-between">
            <div style="font-size: 30rpx;font-weight: bold;">{{ $t('componentPage>chat>chatWindow.insufficientTimeModal.title') }}</div>
            <div style="color: grey;font-size: 14px" @click="close()">{{ $t('componentPage>chat>chatWindow.cancelChatting') }}</div>
        </div>
        <scroll-view :scroll-top="0" scroll-y="true">
            <div class="grid">
                <div
                    v-for="(gift, index) in giftList"
                    :key="gift.id"
                    class="option"
                    @click="selectGift(gift)"
                >
                    <img style="width: 100px;height: 100px;object-fit: cover;" src="/static/page/chat/chat-item-selector/gift-active.svg"/>
                    <div class="info">
                        <h2 class="name">{{ gift.name }}</h2>
                        <p class="chat-duration">{{ gift.chatDuration + " " + $t('pub.unit.minutes') }}</p>
                        <p class="price">{{ gift.price + " " + $t('component>chat>chatItemSelector>gift.lc') }}</p>
                    </div>
                </div>
            </div>
        </scroll-view>
    </div>
</div>
</template>

<script>
export default {
    name: "gift",
    data() {
        return {
            giftList: []
        }
    },
    mounted() {
        this.getDataList();
    },
    methods: {
        getDataList() {
            if(!this.hasMore || this.loading) return;
            this.loading = true;
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.gift.search,
                method: "POST",
                data: {
                    page: this.page,
                    size: this.pageSize,
                },
                success: (res) => {
                    let gifts = res.data.list;

                    if(this.page === 1) {
                        this.giftList = [];
                    }

                    if(gifts.length < this.pageSize) {
                        this.hasMore = false;
                    }

                    this.giftList = this.giftList.concat(gifts);
                    this.page += 1;
                },
                complete: () => {
                    this.loading = false;
                },
            });
        },
        selectGift(gift) {
            this.$parent.processGiftPurchase(gift);
            this.$parent.isEligibleSendMsg();
        },
        close() {
            this.$parent.giftToggle(false);
        },
    }
};
</script>
<style scoped>

.selection-container {
    z-index: 1000;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    justify-content: flex-end;
    height: 100vh;
    flex-direction: column;
}

.selection-panel {
    background-color: #fff;
    border-radius: 30px 30px 0 0;
    width: 100%;
    text-align: center;
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
    z-index: 1100;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    height: 500px; /* Fixed height for the panel */
    position: fixed;
    bottom: 0;
}

.grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
}

.option {
    background-color: #f3f2f6;
    color: #0A2342;
    font-size: 22px;
    font-weight: bold;
    padding: 20px;
    margin: 10px;
    border-radius: 14px;
}

.cover {
    width: 100px;
    height: 100px;
    object-fit: cover;
    margin-bottom: 10px;
}

.info {
    text-align: center;
}

.name {
    font-size: 18px;
    font-weight: bold;
    color: #2676f7;
}

.price {
    font-size: 14px;
    color: #666;
}

.chat-duration {
    font-size: 16px;
}

</style>