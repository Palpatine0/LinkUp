# cc-selectDity


##uniapp专属精品组件页面模板（由前端组件开发出品）精品组件页面模板

###●组件模板规划：
由前端组件开发出品的精品组件页面模板，将陆续发布，预计高达约几百种供您使用，是快速快发项目、创业的必备精品。

合集地址： uni-app模板合集地址：(https://ext.dcloud.net.cn/publisher?id=274945) 如查看全部页面模板，请前往上述uniapp插件市场合集地址；

###●组件模板效果图：
可下载项目后预览，效果图见右侧图片；

###●组件模板费用：
学习：免费下载，进行学习，无费用；

使用/商用：本页面地址赞赏10元后，可终身商用；

###●组件模板使用版权/商用：
本组件模板免费下载可供学习，如需使用及商用，请在本组件页面模板进行赞赏10元

（仅需10元获取精品页面模板代码-物有所值，1个组件页面市场价100元 ）

赞赏10后（当前项目产生赞赏订单可追溯）即可终身商用当前本地址下载的页面模版代码，不同下载地址需进行不同的赞赏。（不赞赏就进行商用使用者，面临侵权！保留追究知识产权法律责任！后果自负！）


### 我的技术公众号(私信可加前端技术交流群)

群内气氛挺不错的，应该或许可能大概，算是为数不多的，专搞技术的前端群，偶尔聊天摸鱼

![图片](https://i.postimg.cc/RZ0sjnYP/front-End-Component.jpg)



#### 使用方法 

```使用方法
<!-- 省市区选择 province city area初始省市区设置 show:是否显示  @changeClick：更改省市区事件 @sureSelectArea：确认事件 @hideShow：隐藏事件-->
<cc-selectDity :province="province" :city="city" :area="area" :show="show" @changeClick="changeClick" @sureSelectArea="onsetCity"
	@hideShow="onhideShow"></cc-selectDity>

```

#### HTML代码实现部分
```html
<template>
	<view>
		<view class="editaddress">
			<view class="content">
				<view class="row">
					<view class="nominal">收货人</view>
					<view class="input"><input placeholder="请输入收货人姓名" v-model="addressData.name" type="text" />
					</view>
				</view>
				<view class="row">
					<view class="nominal">电话号码</view>
					<view class="input"><input placeholder="请输入收货人电话号码" v-model="addressData.phone" type="number"
							maxlength="11" /></view>
				</view>
				<view class="row">
					<view class="nominal">所在地区</view>
					<view class="input selectcity" @tap="openPicker">
						<input placeholder="请选择省市区" disabled type="text" v-model="addressData.address" />
						<image src="/static/right.png" class="rights"></image>
					</view>
				</view>
				<view class="row">
					<view class="nominal">详细地址</view>
					<view class="input"><textarea style="font-size: 28upx;" v-model="addressData.moreAddres"
							auto-height="true" placeholder="输入详细地址" v-if="show == false"></textarea></view>
				</view>
				<view class="row">
					<view class="nominal" style="color: 666;margin-top: 10upx;">设为默认地址</view>
					<view class="input switch">
						<switch :color="colors" style="transform:scale(0.8)" @change="switchChange"
							:checked="addressData.isdefult == 1"></switch>
					</view>
				</view>
			</view>
			<view class="save">
				<view class="btn" :style="'background:' + colors">保存地址</view>
			</view>

			<!-- 省市区选择 province city area初始省市区设置 show:是否显示  @sureSelectArea：确认事件 @hideShow：隐藏事件-->
			<cc-selectDity :province="province" :city="city" :area="area" :show="show" @changeClick="changeClick"
				@sureSelectArea="onsetCity" @hideShow="onhideShow"></cc-selectDity>

		</view>

	</view>
</template>

<script>
	export default {
		data() {
			return {
				colors: '#f23a3a',
				show: false,
				province: "广东省",
				city: "广州市",
				area: "天河区",
				areaCode: "440106",
				addressData: {
					name: '',
					phone: '',
					address: '',
					moreAddres: '',
					isdefult: 0
				},
				isShow: true
			};
		},
		methods: {

			openPicker() {
				console.log('执行打开地址选择器')
				this.show = true
			},

			changeClick(value, value2, value3, value4) {

				console.log('地址选择器 = ' + value + value2 + value3 + value4);

				this.province = value;
				this.city = value2;
				this.area = value3;
				this.areaCode = value4;
			},
			onhideShow() {
				this.show = false
				console.log('执行了关闭地址选择器')
			},
			//选中省市区
			onsetCity(e) {
				let data = e.detail.target.dataset;
				let address = data.province + data.city + data.area;
				this.show = false
				this.addressData.address = address
			},
			// 
			switchChange(e) {

			}

		}
	};
</script>
<style lang="scss" scoped>
	.save {
		position: fixed;
		bottom: 0;
		width: 100%;
		height: 120upx;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.save view {
		display: flex;
	}

	.save .btn {
		box-shadow: 0upx 5upx 10upx rgba(0, 0, 0, 0.4);
		width: 70%;
		height: 80upx;
		border-radius: 80upx;
		background-color: #f23a3a;
		color: #fff;
		justify-content: center;
		align-items: center;
		font-size: 30upx;
		margin-bottom: 60px;
	}

	.save .btn .icon {
		height: 80upx;
		color: #fff;
		font-size: 30upx;
		justify-content: center;
		align-items: center;
	}

	.content {
		display: flex;
		flex-wrap: wrap;
		margin-top: 40upx;
	}

	.content view {
		display: flex;
	}

	.content .row {
		width: 92%;
		margin: 0 4%;
		border-bottom: solid 1upx #eee;
	}

	.content .row .nominal {
		width: 30%;
		height: 80upx;
		font-size: 28upx;
		font-family: Droid Sans Fallback;
		font-weight: 400;
		color: rgba(51, 51, 51, 1);
		align-items: center;
	}

	.content .row .input {
		width: 70%;
		padding: 20upx 0;
		align-items: center;
		font-size: 28upx;

	}

	.content .row .input input {
		font-size: 28upx;
		color: #333333;
	}

	.content .row .switch {
		justify-content: flex-end;
	}

	.content .row .input textarea {
		min-height: 40upx;
		line-height: 40upx;
	}

	.content .del_box {
		width: 100%;
		display: block;
		overflow: hidden;
		margin-right: 30upx;
	}

	.content .del {
		width: 240upx;
		height: 80upx;
		float: right;
		justify-content: center;
		align-items: center;
		font-size: 28upx;
		color: #F23A3A;
		margin: 0 auto;
		margin-top: 50upx;
		border-radius: 38upx;
		background-color: rgba(255, 0, 0, 0.05);
		border-bottom: solid 1upx #eee;
	}

	.selectcity input {
		width: 90%;
	}

	.selectcity image {
		width: 40upx;
		height: 40upx;
		float: right;
	}
</style>
```
