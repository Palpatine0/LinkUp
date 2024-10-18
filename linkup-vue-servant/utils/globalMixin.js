export default {
    name: "globalMixin",
    data() {
        return {
            language: uni.getStorageSync("language"),
        }
    },
    onShareAppMessage() {
        let shareTitle = "Come check this app!!";
        let referrerId = uni.getStorageSync(getApp().globalData.data.userInfoKey)?.id || 'default_id';
        let sharePath = `/pages/home/home?referrerId=${referrerId}`;
        let shareImg = "https://i.imghippo.com/files/BGqLk1727503992.jpg";

        return {
            title: shareTitle,
            path: sharePath,
            imageUrl: shareImg,
            success: function (res) {
                console.log("Share success");
            },
            fail: function (res) {
                console.log("Share failed");
            }
        }
    }
}
