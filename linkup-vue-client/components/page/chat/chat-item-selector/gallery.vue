<template>
<div>
    <div class="option" @click="selectFromGallery">
        <div align="center">
            <img style="width: 100px;height: 100px" src="/static/page/chat/chat-item-selector/gallery/folder.svg"/>
        </div>
        {{ $t('component>chat>chatItemSelector>gallery.choseFromAlbum') }}
    </div>
    <div class="option" @click="takePhoto">
        <div align="center">
            <img style="width: 100px;height: 100px" src="/static/page/chat/chat-item-selector/gallery/camera.svg"/>
        </div>
        {{ $t('component>chat>chatItemSelector>gallery.takePhoto') }}
    </div>
</div>
</template>

<script>
export default {
    name: "gallery",
    methods: {
        selectFromGallery() {
            uni.chooseImage({
                count: 12,
                sizeType: ['original', 'compressed'],
                sourceType: ['album'],
                success: (res) => {
                    console.log("AL res")
                    console.log(res)
                    Promise.all(
                        res.tempFilePaths.map(item => {
                            return new Promise((resolve, reject) => {
                                fs.readFile({
                                    filePath: item,
                                    encoding: 'base64',
                                    success: res => {
                                        var img = 'data:image/png;base64,' + res.data
                                        console.log(img)
                                        resolve(img)
                                    },
                                    fail: err => {
                                        reject(err)
                                    }
                                })
                            })
                        })
                    ).then(results => {
                        that.uploadLivePic(results)
                    })
                }
            })
        },
        takePhoto() {
            uni.chooseImage({
                count: 12,
                sizeType: ['original', 'compressed'],
                sourceType: ['camera'],
                success: (res) => {
                    console.log("CA res")
                    console.log(res)

                }
            })
        }
    }
};
</script>
<style scoped>
.option {
    background-color: #f3f2f6;
    color: #0A2342;
    font-size: 22px;
    font-weight: bold;
    padding: 20px;
    margin: 10px;
    width: 82vw;
    border-radius: 14px;
}
</style>