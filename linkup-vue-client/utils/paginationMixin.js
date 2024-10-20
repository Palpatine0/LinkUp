export default {
    name: "paginationMixin",
    data() {
        return {
            page: 1,
            pageSize: 10,
            global: false,
            hasMore: true,
            loading: false,
            isFetchingData: false
        };
    },
    methods: {
        resetPagination() {
            this.page = 1;
            this.pageSize = 10;
            this.hasMore = true;
            this.dataList = [];
        },
        onReachBottom() {
            this.getDataList();
        },
    },
};
