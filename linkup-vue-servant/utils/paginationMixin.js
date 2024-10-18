export default {
    name: "paginationMixin",
    data() {
        return {
            page: 1,
            pageSize: 10,
            global: false,
            hasMore: true,
            loading: false,
        };
    },
    methods: {
        resetPagination() {
            this.page = 1;
            this.pageSize = 10;
            this.hasMore = true;
        },
        onReachBottom() {
            this.getDataList();
        },
    },
};
