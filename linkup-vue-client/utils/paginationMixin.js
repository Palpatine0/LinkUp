export default {
    data() {
        return {
            page: 1,
            size: 10,
            global: false,
            hasMore: true,
            loading: false,
        };
    },
    methods: {
        resetPagination() {
            this.page = 1;
            this.size = 10;
            this.hasMore = true;
        },
        onReachBottom() {
            this.getOrderList();
        },
    },
};
