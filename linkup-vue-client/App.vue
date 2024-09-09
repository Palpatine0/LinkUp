<script>
export default {
    globalData: {
        needsTopPadding: null,
        // dev
        requestUrl: "http://localhost:8089",
        // prd
        // request_url:"http://111.231.19.137:8089"
    },
    onLaunch() {
        wx.getSystemInfo({
            success: (res) => {
                if (res.safeArea.top > 40) {
                    this.globalData.needsTopPadding = true
                } else {
                    this.globalData.needsTopPadding = false
                }
            }
        })
    },
}
</script>

<style>
</style>
