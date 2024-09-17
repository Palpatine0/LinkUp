<template>
<div class="page">
    <app-title type="h1" bold="true">附近</app-title>

    <!-- Category Filters (Optional, can be expanded) -->
    <div class="flex">
        <div class="app-container" style="width: 48%;margin: 2px;">
            导游
        </div>
        <div class="app-container" style="width: 48%;margin: 2px;">
            XX
        </div>
    </div>
    <div class="flex">
        <div class="app-container" style="width: 48%;margin: 2px;">
            XX
        </div>
        <div class="app-container" style="width: 48%;margin: 2px;">
            XX
        </div>
    </div>

    <!-- Scroll View for User List -->
    <scroll-view :scroll-top="0" scroll-y="true" style="height: 72vh" class="mt-4">
        <div class="contact-grid">
            <div
                v-for="(user, index) in userList"
                :key="user.id"
                :class="{'contact-item': true, 'last-item': index === userList.length - 1}"
                @click="userDetailRedirect(user.id)"
            >
                <img :src="user.avatar" alt="user.name" class="contact-avatar">
                <div class="contact-info">
                    <h2 class="contact-name">{{ user.nickname }}</h2>
                    <p class="contact-number">{{ user.currentLocation }}</p>
                </div>
            </div>
        </div>
    </scroll-view>
</div>
</template>

<script>
export default {
    name: "home",
    data() {
        return {
            userList: [],  // This will store the list of users
            page: 1,       // Page number for pagination
            size: 20,      // Number of users to fetch per request
            hasMore: true, // Flag to check if more users are available to load
            loading: false // To handle the loading state
        };
    },
    onLoad() {
        this.getUserList();
    },
    methods: {
        getUserList() {
            if (!this.hasMore || this.loading) return; // Prevent multiple requests if no more data or still loading

            this.loading = true;
            uni.request({
                url: getApp().globalData.requestUrl + '/user/search', // The endpoint for fetching users
                method: "POST",
                data: {
                    page: this.page,  // Pass pagination info to the backend
                    size: this.size,
                },
                success: (res) => {
                    const users = res.data.userList;  // Assuming the API returns userList in the response

                    if (this.page === 1) {
                        this.userList = []; // Reset user list on the first page
                    }

                    if (users.length < this.size) {
                        this.hasMore = false; // If fewer users are returned, no more pages are available
                    }

                    this.userList = this.userList.concat(users); // Append new users to the list
                    this.page += 1; // Increment page number for next request
                },
                complete: () => {
                    this.loading = false; // Reset the loading flag
                },
            });
        },

        // Redirect to user details
        userDetailRedirect(userId) {
            uni.navigateTo({
                url: '/pages/components/user/user-detail/user-detail?userId=' + userId,
            });
        }
    }
};
</script>

<style scoped>
.contact-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr); /* Two items per row */
    gap: 16px; /* Adjust spacing between the grid items */
}

.contact-item {
    background-color: white;
    padding: 10px;
    border-radius: 8px;
    text-align: center;
}

.contact-avatar {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 50%;
    margin-bottom: 10px;
}

.contact-info {
    text-align: center;
}

.contact-name {
    font-size: 18px;
    font-weight: bold;
}

.contact-number {
    font-size: 14px;
    color: #666;
}
</style>
