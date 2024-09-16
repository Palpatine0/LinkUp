<template>
<div class="page">
    <app-title type="h1" bold="true">附近</app-title>
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

    <scroll-view :scroll-top="0" scroll-y="true" style="height: 72vh" class="mt-4">
        <div class="contact-grid">
            <div
                v-for="(contact, index) in contacts"
                :key="contact.id"
                :class="{'contact-item': true, 'last-item': index === contacts.length - 1}"
                @click="contactRedirect(contact.id)"
            >
                <img :src="contact.avatar" alt="contact.name" class="contact-avatar">
                <div class="contact-info">
                    <h2 class="contact-name">{{ contact.name }}</h2>
                    <p class="contact-number">{{ contact.number }}</p>
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
            contacts: [
                {id: 1, name: 'John Doe', number: '123-456-7890', avatar: 'https://i.imghippo.com/files/4mxDr1726433090.jpg'},
                {id: 2, name: 'Jane Smith', number: '234-567-8901', avatar: 'https://i.imghippo.com/files/KMncW1726433120.webp'},
                {id: 2, name: 'Jane Smith', number: '234-567-8901', avatar: 'https://i.imghippo.com/files/Uhflq1726433155.avif'},
                {id: 2, name: 'Jane Smith', number: '234-567-8901', avatar: 'https://i.imghippo.com/files/MkOeo1726433215.avif'},
                {id: 2, name: 'Jane Smith', number: '234-567-8901', avatar: 'https://i.imghippo.com/files/U369F1726433184.webp'},
                {id: 2, name: 'Jane Smith', number: '234-567-8901', avatar: 'https://i.imghippo.com/files/Uhflq1726433155.avif'},
                {id: 2, name: 'Jane Smith', number: '234-567-8901', avatar: 'https://i.imghippo.com/files/Uhflq1726433155.avif'},
                {id: 2, name: 'Jane Smith', number: '234-567-8901', avatar: 'https://i.imghippo.com/files/Uhflq1726433155.avif'},
                {id: 2, name: 'Jane Smith', number: '234-567-8901', avatar: 'https://i.imghippo.com/files/Uhflq1726433155.avif'},
                {id: 2, name: 'Jane Smith', number: '234-567-8901', avatar: 'https://i.imghippo.com/files/Uhflq1726433155.avif'},
                {id: 2, name: 'Jane Smith', number: '234-567-8901', avatar: 'https://i.imghippo.com/files/Uhflq1726433155.avif'},
            ]
        }
    }
}
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