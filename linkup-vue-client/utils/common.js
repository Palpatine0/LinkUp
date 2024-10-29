import $API from "api/api"

if(!String.prototype.replaceAll) {
    String.prototype.replaceAll = function (s1, s2) {
        return this.replace(new RegExp(s1, "gm"), s2);
    }
}
var $common = {
    generateUniqueCode: function (pattern, numCodes) {
        const bases = [];
        let totalPermutations = 1;

        // Determine the base for each character in the pattern
        for (const char of pattern) {
            if(char === 'a') {
                bases.push('ABCDEFGHIJKLMNOPQRSTUVWXYZ'); // Base for letters
            } else if(char === '1') {
                bases.push('1234567890'); // Base for numbers
            }
        }

        // Calculate total permutations
        bases.forEach(base => {
            totalPermutations *= base.length;
        });

        // Check if the requested number of coupons exceeds the possible permutations
        if(numCodes > totalPermutations) {
            throw new Error('Requested number of coupons exceeds possible permutations.');
        }

        const coupons = new Set();
        while (coupons.size < numCodes) {
            const randomIndex = Math.floor(Math.random() * totalPermutations);
            let coupon = '';

            let index = randomIndex;
            for (const base of bases) {
                const baseLength = base.length;
                const charIndex = index % baseLength;
                coupon += base[charIndex];
                index = Math.floor(index / baseLength);
            }
            coupons.add(coupon);
        }
        return Array.from(coupons).join('');
    },
    balanceAdequateValidation: function(price, balance) {
        const selectedPrice = parseInt(price);
        const balanceValue = parseFloat(balance);

        if (
            this.isEmpty(selectedPrice) ||
            this.isEmpty(balanceValue) ||
            isNaN(selectedPrice) ||
            isNaN(balanceValue)
        ) {
            return false;
        }
        // Check if the user's balance is sufficient to cover the required amount
        return balanceValue >= selectedPrice;
    },
    getRandom: function (v) {
        v = v || 999999;
        return Math.floor(Math.random() * v);
    },
    timeToStamp: function (dateTime) {
        dateTime = dateTime || new Date();
        var date = new Date(dateTime);
        return date.getTime();
    },
    timeToStampRecord: function (dateTime) {
        dateTime = dateTime || new Date();
        var date = new Date(dateTime);

        // Get current date and time
        var now = new Date();

        // Check if the given date is today
        if (date.toDateString() === now.toDateString()) {
            return date.getHours().toString().padStart(2, '0') + ':' + date.getMinutes().toString().padStart(2, '0');
        }

        // Check if the given date is yesterday
        var yesterday = new Date(now);
        yesterday.setDate(now.getDate() - 1);
        if (date.toDateString() === yesterday.toDateString()) {
            return 'Yesterday';
        }

        // For all other dates, return only the month and day without time
        var month = (date.getMonth() + 1).toString().padStart(2, '0'); // Months are zero-indexed
        var day = date.getDate().toString().padStart(2, '0');

        return `${month}-${day}`;
    },

    /**
     * Converts a timestamp to a formatted time string.
     * @param {number} stamp - The timestamp to be converted.
     * @param {boolean} removeYear - If true, removes the year from the formatted string. Default is false.
     * @returns {string} - The formatted time string.
     */
    stampToTime: function (stamp, options = {}) {
        // Default options for exclusion (exclude nothing by default)
        const {yyyy = true, MM = true, dd = true, HH = true, mm = true, ss = true} = options;

        stamp = stamp || new Date().getTime();
        stamp = (stamp + "").length <= 10 ? Number(stamp) * 1000 : stamp;
        let date = new Date(stamp);

        // Get components
        let Y = date.getFullYear();
        let M = (date.getMonth() + 1).toString().padStart(2, '0'); // Month (01-12)
        let D = date.getDate().toString().padStart(2, '0'); // Day (01-31)
        let h = date.getHours().toString().padStart(2, '0'); // Hours (00-23)
        let m = date.getMinutes().toString().padStart(2, '0'); // Minutes (00-59)
        let s = date.getSeconds().toString().padStart(2, '0'); // Seconds (00-59)

        // Build the output string based on excluded components
        let parts = [];
        if(yyyy) parts.push(Y);
        if(MM) parts.push(M);
        if(dd) parts.push(D);

        let datePart = parts.join('-'); // Date portion

        // Time portion (only if none of these are excluded)
        parts = [];
        if(HH) parts.push(h);
        if(mm) parts.push(m);
        if(ss) parts.push(s);

        let timePart = parts.join(':'); // Time portion

        return timePart ? `${datePart} ${timePart}` : datePart;
    },

    calculateCountdown: function(startTime, endTime, callback){
        const currentTime = new Date().getTime();
        let remainingTime = 0;

        if (startTime && endTime) {
            // If both start and end times are provided
            const startTimestamp = new Date(startTime).getTime();
            const endTimestamp = new Date(endTime).getTime();
            remainingTime = endTimestamp - currentTime;
        } else if (startTime && !endTime) {
            // If only the start time is provided, assume a default 10-minute duration
            const startTimestamp = new Date(startTime).getTime();
            const countdownDurationInMilliseconds = 10 * 60 * 1000; // 10 minutes
            const endTimestamp = startTimestamp + countdownDurationInMilliseconds;
            remainingTime = endTimestamp - currentTime;
        } else if (!startTime && endTime) {
            // If only the end time is provided, calculate remaining time from current time to end time
            const endTimestamp = new Date(endTime).getTime();
            remainingTime = endTimestamp - currentTime;
        }

        // Check if countdown is still active
        if (remainingTime > 0) {
            let countdown = Math.floor(remainingTime / 1000); // Convert milliseconds to seconds

            const intervalId = setInterval(() => {
                if (countdown > 0) {
                    countdown--;
                    const formattedTime = this.secondsToCountdown(countdown);
                    callback(formattedTime); // Pass the formatted time to the callback
                } else {
                    clearInterval(intervalId);
                    callback("00:00:00"); // When countdown reaches 0
                }
            }, 1000); // Update every second
        } else {
            callback("00:00:00"); // If countdown is over, return "00:00:00"
        }
    },
    secondsToCountdown: function(seconds){
        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const secs = seconds % 60;
        return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    },

    validateFileType(filePath, type) {
        const fileExtension = filePath.split('.').pop().toLowerCase();

        // Define allowed extensions for each type
        const allowedExtensions = {
            img: ['jpg', 'jpeg', 'png', 'webp'],
            video: ['mp4', 'avi', 'mov', 'mkv']
        };

        if (!allowedExtensions[type].includes(fileExtension)) {
            return false; // Invalid type
        }
        return true; // Valid type
    },

    isEmojiContains: function (text) {
        const emojiRegex = /[\u{1F600}-\u{1F64F}]|[\u{1F300}-\u{1F5FF}]|[\u{1F680}-\u{1F6FF}]|[\u{1F700}-\u{1F77F}]|[\u{1F780}-\u{1F7FF}]|[\u{1F800}-\u{1F8FF}]|[\u{1F900}-\u{1F9FF}]|[\u{1FA00}-\u{1FA6F}]|[\u{1FA70}-\u{1FAFF}]|[\u{2600}-\u{26FF}]|[\u{2700}-\u{27BF}]|[\u{1F1E6}-\u{1F1FF}]|[\u{1F910}-\u{1F92F}]|[\u{1F980}-\u{1F9FF}]|[\u{1F600}-\u{1F64F}]/u;
        return emojiRegex.test(text);
    },

    unDefined(v) {
        return v === undefined ? true : false;
    },
    isDefined(v) {
        return v != undefined ? true : false;
    },
    propNoFalse(v) {
        return v !== undefined && v !== false ? true : false;
    },
    useDefined: function (...option) {
        for (let i = 0; i < option.length; i++) {
            if(
                option[i] === "undefined" ||
                option[i] === undefined ||
                option[i] === null
            ) {
            } else {
                return option[i];
            }
        }
        return undefined;
    },
    isEmpty: function (v) {
        if(v === "" || v === "undefined" || v === undefined || v === null) {
            return true;
        }
        if(
            JSON.stringify(v) == "{}" ||
            JSON.stringify(v) == "[]" ||
            JSON.stringify(v) == "[{}]"
        ) {
            return true;
        }
        return false;
    },
    removeSpace: function (v, filterKeyArr) {
        if(this.isString(v)) {
            return v.trim();
        } else if(this.isObject(v)) {
            let obj = this.copy(v);
            let arr = this.toArray(filterKeyArr);
            for (let key in obj) {
                if(this.isString(obj[key]) && arr.indexOf(key) == -1) {
                    obj[key] = obj[key].trim();
                    console.log("key", obj[key]);
                }
            }
            return obj;
        }
        return v;
    },
    isString: function (str) {
        return typeof str == "string" ? true : false;
    },
    toString: function (v) {
        try {
            v = typeof v === "string" ? v : JSON.stringify(v);
        } catch (error) {
        }
        return v;
    },
    isArray: function (obj) {
        if(this.useDefined(obj) === undefined) {
            return false;
        }
        return Array.isArray(obj);
    },
    isJson: function (obj) {
        if(this.useDefined(obj) === undefined) {
            return false;
        }
        return obj.constructor == Object;
    },
    isObject: function (obj) {
        if(this.useDefined(obj) === undefined) {
            return false;
        }
        return typeof obj == "object" ? true : false;
    },
    toArray: function (obj) {
        if(this.useDefined(obj) === undefined) {
            return [];
        }
        if(obj === true || obj === false) {
            return [obj];
        }
        if(this.isArray(obj) === true) {
            return obj;
        }
        if(this.isString(obj) === true) {
            return [obj];
        }
        return [];
    },
    toObject: function (v) {
        try {
            var newV = typeof v === "object" ? v : JSON.parse(v);
            typeof newV != "number" && (v = newV);
        } catch (error) {
            /** error */
        }
        return v;
    },
    setObject(object, item, value) {
        var obj = this.copy(object);
        if(this.isEmpty(item)) {
            for (let item in obj) {
                obj[item] = value;
            }
        } else {
            obj[item] = value;
        }
        return obj;
    },
    copy: function (val) {
        const tool = {
            deepCopy: (obj, newObj) => {
                if(typeof obj != "object" && typeof newObj == "undefined") {
                    return obj;
                }
                let copyJson = newObj || {};
                for (let key in obj) {
                    if(typeof obj[key] == "object") {
                        copyJson[key] = this.isEmpty(obj[key])
                            ? obj[key]
                            : Array.isArray(obj[key])
                                ? []
                                : {};
                        tool.deepCopy(obj[key], copyJson[key]);
                    } else {
                        copyJson[key] = obj[key];
                    }
                }
                return copyJson;
            }
        };
        let newVal = tool.deepCopy(val);
        return this.isObject(newVal)
            ? this.isArray(val)
                ? Object.values(newVal)
                : newVal
            : val;
    },
    addToClipboard(data) {
        uni.setClipboardData({
            data: data,
            showToast:false,
            success: function () {
                console.log('success');
            }
        });
    },
    distinctArrayObject(arryObject, prop) {
        let result = [];
        let obj = {};
        for (let item of arryObject) {
            let value = item[prop];
            if(!obj[value]) {
                result.push(item);
                obj[value] = 1;
            }
        }
        return result;
    },
    getFileSize: function (fileByte) {
        var fileSizeByte = Number(fileByte);
        if(fileSizeByte >= 0) {
            var fileSizeMsg = "";
            if(fileSizeByte < 1048576) {
                fileSizeMsg = (fileSizeByte / 1024).toFixed(2) + "KB";
            } else if(fileSizeByte == 1048576) {
                fileSizeMsg = "1MB";
            } else if(fileSizeByte > 1048576 && fileSizeByte < 1073741824) {
                fileSizeMsg = (fileSizeByte / (1024 * 1024)).toFixed(2) + "MB";
            } else if(fileSizeByte > 1048576 && fileSizeByte == 1073741824) {
                fileSizeMsg = "1GB";
            } else if(fileSizeByte > 1073741824 && fileSizeByte < 1099511627776) {
                fileSizeMsg = (fileSizeByte / (1024 * 1024 * 1024)).toFixed(2) + "GB";
            } else {
                fileSizeMsg = "文件超过1TB";
            }
            return fileSizeMsg;
        } else {
            return fileByte;
        }
    },
    toNumber(value, type = 'float') {
        if(value === undefined || value === null) return 0;

        switch (type.toLowerCase()) {
            case 'int':
            case 'integer':
                return parseInt(value, 10) || 0; // Converts to Integer
            case 'float':
                return parseFloat(value) || 0.0; // Converts to Float
            case 'bigdecimal':
                return parseFloat(value) || 0.0; // Simulate BigDecimal with float for JS
            default:
                return parseFloat(value) || 0.0; // Default to float if type not specified
        }
    },
}

// Date format
// new Date('2019/7/9').Format('yyyy-MM-dd'): 2019-07-09
// new Date('2019/7/9').Format('MM-dd'): 07-09
Date.prototype.Format = function (formatValue) {
    if(this == "Invalid Date") {
        return "Invalid Date";
    }
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        S: this.getMilliseconds()
    };
    if(/(y+)/.test(formatValue)) {
        formatValue = formatValue.replace(
            RegExp.$1,
            (this.getFullYear() + "").substr(4 - RegExp.$1.length)
        );
    }
    for (var k in o) {
        if(new RegExp("(" + k + ")").test(formatValue)) {
            formatValue = formatValue.replace(
                RegExp.$1,
                RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length)
            );
        }
    }
    return formatValue;
};
export default $common;
