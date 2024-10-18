var $API = {
    // User
    user: {
        save: '/user/save',
        search: '/user/search',
        update: '/user/update',
        userConfig: '/user/user-config',
        searchServant: '/user/search-servant',
        referralCodeValidation: '/user/referral-code-validation'
    },

    // User Servant
    userServant: {
        search: '/user-servant/search',
    },

    // Service Type
    serviceType: {
        search: '/service-type/search',
    },

    // Conversation
    conversation: {
        search: '/conversation/search',
    },
    // Message
    message: {
        save: '/message/save',
        search: '/message/search',
        searchContacts: '/message/search-contacts',
        markAsRead: '/message/mark-read',
    },

    // Order
    order: {
        save: '/order/save',
        search: '/order/search',
        update: '/order/update',
        updateStatus: '/order/update-status',
        remainingFreePostingQuota: '/order/remaining-free-posting-quota',
        assignServant: '/order/assign-servant',
        rateServant: '/order/rate-servant',
    },

    // Order Candidate
    orderCandidate: {
        save: '/order-candidate/save',
        servants: '/order-candidate/servants',
    },

    // Transaction
    transaction: {
        search: '/transaction/search',
        save: '/transaction/save',
        updateLookingCoin: '/transaction/update-looking-coin',
    },

    // Address
    address: {
        save: '/address/save',
        search: '/address/search',
        update: '/address/update',
    },

    // Gift
    gift:{
        search: '/gift/search',
        purchase: '/gift/purchase',
    }
}
export default $API;