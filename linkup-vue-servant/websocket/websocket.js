let socketOpen = false;
let userId = null;
let messageHandlers = [];

export function connectWebSocket(newUserId) {
    if (socketOpen) {
        closeWebSocket();
    }

    userId = newUserId;

    const socketUrl = getApp().globalData.data.socketUrl + '/chat?userId=' + userId;
    uni.connectSocket({ url: socketUrl });

    uni.onSocketOpen(() => {
        socketOpen = true;
    });

    uni.onSocketMessage((res) => {
        const data = JSON.parse(res.data);
        messageHandlers.forEach(handler => handler(data));
    });

    uni.onSocketError((err) => {
        socketOpen = false;
    });

    uni.onSocketClose(() => {
        socketOpen = false;
    });
}

export function closeWebSocket() {
    if (socketOpen) {
        uni.closeSocket({
            success: () => {
                socketOpen = false;
            },
            fail: (err) => {
                console.error('Error closing WebSocket:', err);
            }
        });
    }
}

export function sendWebSocketMessage(message) {
    if (socketOpen) {
        uni.sendSocketMessage({ data: JSON.stringify(message) });
    } else {
        console.warn('WebSocket is not open. Cannot send message.');
    }
}

export function addMessageHandler(handler) {
    if (typeof handler === 'function') {
        messageHandlers.push(handler);
    }
}

export function removeMessageHandler(handler) {
    messageHandlers = messageHandlers.filter(h => h !== handler);
}
