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
        searchContacts: '/conversation/search-contacts',
    },
    // Message
    message: {
        save: '/message/save',
        search: '/message/search',
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
        servantOrders: '/order/servant-orders',
        rateServant: '/order/rate-servant',
        reviewClient: '/order/review-client',
    },

    // Order Candidate
    orderCandidate: {
        save: '/order-candidate/save',
        search: '/order-candidate/search',
        update: '/order-candidate/update',
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
    },

    // Review
    review: {
        save: '/review/save',
        search: '/review/search',
        update: '/review/update',
    },

    // File
    file:{
        signature:"/file/signature"
    }
}
export default $API;