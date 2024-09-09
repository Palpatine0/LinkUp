if (!String.prototype.replaceAll) {
    String.prototype.replaceAll = function (s1, s2) {
        return this.replace(new RegExp(s1, "gm"), s2);
    }
}
var $common = {
    getRandom: function (v) {
        v = v || 999999;
        return Math.floor(Math.random() * v);
    },
    timeToStamp: function (dateTime) {
        dateTime = dateTime || new Date();
        var date = new Date(dateTime);
        return date.getTime();
    },
    stampToTime: function (stamp) {
        stamp = stamp || new Date().getTime();
        stamp = (stamp + "").length <= 10 ? Number(stamp) * 1000 : stamp;
        let date = new Date(stamp);
        let Y = date.getFullYear();
        let M =
            date.getMonth() + 1 < 10
                ? "0" + (date.getMonth() + 1)
                : date.getMonth() + 1;
        let D = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
        let h = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
        let m =
            date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
        let s =
            date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
        return `${Y}-${M}-${D} ${h}:${m}:${s}`;
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
            if (
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
        if (v === "" || v === "undefined" || v === undefined || v === null) {
            return true;
        }
        if (
            JSON.stringify(v) == "{}" ||
            JSON.stringify(v) == "[]" ||
            JSON.stringify(v) == "[{}]"
        ) {
            return true;
        }
        return false;
    },
    removeSpace: function (v, filterKeyArr) {
        if (this.isString(v)) {
            return v.trim();
        } else if (this.isObject(v)) {
            let obj = this.copy(v);
            let arr = this.toArray(filterKeyArr);
            for (let key in obj) {
                if (this.isString(obj[key]) && arr.indexOf(key) == -1) {
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
        if (this.useDefined(obj) === undefined) {
            return false;
        }
        return Array.isArray(obj);
    },
    isJson: function (obj) {
        if (this.useDefined(obj) === undefined) {
            return false;
        }
        return obj.constructor == Object;
    },
    isObject: function (obj) {
        if (this.useDefined(obj) === undefined) {
            return false;
        }
        return typeof obj == "object" ? true : false;
    },
    toArray: function (obj) {
        if (this.useDefined(obj) === undefined) {
            return [];
        }
        if (obj === true || obj === false) {
            return [obj];
        }
        if (this.isArray(obj) === true) {
            return obj;
        }
        if (this.isString(obj) === true) {
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
        if (this.isEmpty(item)) {
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
                if (typeof obj != "object" && typeof newObj == "undefined") {
                    return obj;
                }
                let copyJson = newObj || {};
                for (let key in obj) {
                    if (typeof obj[key] == "object") {
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
    addToClipboard(data, callback) {
        let oInput = document.createElement("textarea");
        oInput.value = data;
        document.body.appendChild(oInput);
        let txt = oInput.select();
        document.execCommand("Copy");
        oInput.remove();
        callback && callback(txt);
    },
    distinctArrayObject(arryObject, prop) {
        let result = [];
        let obj = {};
        for (let item of arryObject) {
            let value = item[prop];
            if (!obj[value]) {
                result.push(item);
                obj[value] = 1;
            }
        }
        return result;
    },
    getFileSize: function(fileByte) {
        var fileSizeByte = Number(fileByte);
        if (fileSizeByte >= 0) {
            var fileSizeMsg = "";
            if (fileSizeByte < 1048576) {
                fileSizeMsg = (fileSizeByte / 1024).toFixed(2) + "KB";
            } else if (fileSizeByte == 1048576) {
                fileSizeMsg = "1MB";
            } else if (fileSizeByte > 1048576 && fileSizeByte < 1073741824) {
                fileSizeMsg = (fileSizeByte / (1024 * 1024)).toFixed(2) + "MB";
            } else if (fileSizeByte > 1048576 && fileSizeByte == 1073741824) {
                fileSizeMsg = "1GB";
            } else if (fileSizeByte > 1073741824 && fileSizeByte < 1099511627776) {
                fileSizeMsg = (fileSizeByte / (1024 * 1024 * 1024)).toFixed(2) + "GB";
            } else {
                fileSizeMsg = "文件超过1TB";
            }
            return fileSizeMsg;
        } else {
            return fileByte;
        }
    },
}

// Date format
// new Date('2019/7/9').Format('yyyy-MM-dd'): 2019-07-09
// new Date('2019/7/9').Format('MM-dd'): 07-09
Date.prototype.Format = function (formatValue) {
    if (this == "Invalid Date") {
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
    if (/(y+)/.test(formatValue)) {
        formatValue = formatValue.replace(
            RegExp.$1,
            (this.getFullYear() + "").substr(4 - RegExp.$1.length)
        );
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(formatValue)) {
            formatValue = formatValue.replace(
                RegExp.$1,
                RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length)
            );
        }
    }
    return formatValue;
};
export default $common;
