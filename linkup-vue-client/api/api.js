var $API = {
    // Address
    address: {
        save: '/address/save',
        search: '/address/search',
        update: '/address/update',
    },
    // Message
    message: {
        save: '/message/save',
        search: '/message/search',
        searchContacts: '/message/search-contacts',
    },
    // Service Type
    serviceType: {
        search: '/service-type/search',
    },
    // Order
    order: {
        save: '/order/save',
        search: '/order/search',
        update: '/order/update',
        updateStatus: '/order/update-status',
        remainingFreePostingQuota: '/order/remaining-free-posting-quota',
    },
    // Order Candidate
    orderCandidate: {
        servants: '/order-candidate/servants',
    },
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
    // Transaction
    transaction: {
        search: '/transaction/search',
        save: '/transaction/save',
    }
}
export default $API;