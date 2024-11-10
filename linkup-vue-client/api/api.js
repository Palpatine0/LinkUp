var $API = {
    // User
    user: {
        save: '/user/save',
        search: '/user/search',
        update: '/user/update',
        userConfig: '/user/user-config',
        searchServant: '/user/search-servant',
        referralCodeValidation: '/user/referral-code-validation',
        identityValidation: '/user/identity-validation',
        smsValidation: '/user/sms-validation'
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

    // Withdrawal request
    withdrawalRequest: {
        save: '/withdrawal-request/save',
        search: '/withdrawal-request/search',
        update: '/withdrawal-request/update',
    },

    // Address
    address: {
        save: '/address/save',
        search: '/address/search',
        update: '/address/update',
        delete: '/address/delete',
    },

    // Gift
    gift: {
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
    file: {
        signature: "/file/signature"
    },

    // Bankcard
    bankCard: {
        save: '/bank-card/save',
        search: '/bank-card/search',
        searchBank: '/bank-card/search-bank',
        update: '/bank-card/update',
        delete: '/bank-card/delete',
        validation: '/bank-card/validation',
    },

    // Ailpay account
    ailpayAccount: {
        save: '/alipay-account/save',
        search: '/alipay-account/search',
        update: '/alipay-account/update',
        delete: '/alipay-account/delete',
    }
}
export default $API;