<template>
<div>
    <div class="mask" @click="close()"></div>
    <div class="widget-popup coup-anim">
        <div class="file-selection-header">
            {{ panelHeader }}
        </div>

        <!-- Conditional rendering for each panel with limited height -->
        <div class="sub-panel-wrapper">
            <Gallery v-show="selectedChatItem === 'gallery'" class="sub-panel" @select="handleSelection"/>
            <Gift v-show="selectedChatItem === 'gift'" class="sub-panel" @select="handleSelection"/>
        </div>

        <!-- Chat item selectors at the bottom -->
        <div class="chat-item-selectors">
            <div
                v-for="item in chatItems"
                :key="item.id"
                class="chat-item-selector"
                :style="{ color: item.id === selectedChatItem ? '#2676f7' : '#939393' }"
                @click="openSubPanel(item.id)"
            >
                <img
                    :src="item.id === selectedChatItem ? item.iconActive : item.icon"
                    class="chat-item-icon"
                    :alt="item.label"
                />
                <span>{{ item.label }}</span>
            </div>
        </div>
    </div>
</div>
</template>

<script>
import Gallery from "./chat-item-selector/gallery.vue";
import Gift from "./chat-item-selector/gift.vue";

export default {
    name: "chat-item-selector",
    components: {Gallery, Gift},
    data() {
        return {
            selectedChatItem: 'gallery',
            chatItems: [
                {
                    id: 'gallery',
                    label: this.$t('component>chat>chatItemSelector>gallery.panelHeader'),
                    icon: '/static/page/chat/chat-item-selector/photo-film.svg',
                    iconActive: '/static/page/chat/chat-item-selector/photo-film-active.svg'
                },
                {
                    id: 'gift',
                    label: this.$t('component>chat>chatItemSelector>gift.panelHeader'),
                    icon: '/static/page/chat/chat-item-selector/gift.svg',
                    iconActive: '/static/page/chat/chat-item-selector/gift-active.svg'
                },
            ],
        };
    },
    computed: {
        panelHeader() {
            switch (this.selectedChatItem) {
                case 'gallery':
                    return this.$t('component>chat>chatItemSelector>gallery.panelHeader');
                case 'gift':
                    return this.$t('component>chat>chatItemSelector>gift.panelHeader');
                default:
                    return 'Select Option';
            }
        }
    },
    methods: {
        close() {
            this.$parent.chatItemSelectorToggle(false);
        },
        openSubPanel(item) {
            this.selectedChatItem = item;
        },
        handleSelection(selectedGift) {
            this.$emit('giftSelected', selectedGift);
        },
    }
};
</script>

<style scoped>
.sub-panel-wrapper {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
    height: calc(100% - 80px);
}

/* Header */
.file-selection-header {
    font-size: 30rpx;
    font-weight: bold;
    margin: 20rpx 0;
}

/* Chat item selectors always at the bottom */
.chat-item-selectors {
    display: flex;
    justify-content: space-around;
    align-items: center;
    background-color: #fff;
    padding: 10rpx;
    border-radius: 0 0 20rpx 20rpx;
    box-shadow: 0 -2px 6px rgba(0, 0, 0, 0.1);
    width: 100%;
    height: 60px;
    position: relative;
}

/* Each chat item selector */
.chat-item-selector {
    display: flex;
    flex-direction: column;
    align-items: center;
    cursor: pointer;
}

.chat-item-icon {
    width: 40rpx;
    height: 40rpx;
    margin-bottom: 5rpx;
}

.chat-item-selector span {
    font-size: 24rpx;
}
</style>
