// eventBus.js
let Bus = function () {
};
var objBus = [], arrbus = [];
Bus.prototype = {
    obj: {
        $set: function (key, action) {
            if (key && action) {
                var map = {};
                map.k = key;
                map.v = action;
                for (var i = 0, busLength = objBus.length; i < busLength; i++) {
                    var tempMap = objBus[i];
                    if (tempMap.k == key) {
                        objBus.splice(i, 1);
                    }
                }
                objBus.push(map);
            }
        },
        $get: function (key) {
            if (key) {
                for (var i = 0, busLength = objBus.length; i < busLength; i++) {
                    var map = objBus[i];
                    if (map.k == key) {
                        return map.v();
                    }
                }
            }
        }
    },
    $emit: function (key, data) {
        if (key) {
            for (var i = 0, busLength = arrbus.length; i < busLength; i++) {
                var map = arrbus[i];
                if (map.k == key) {
                    return map.v(data);
                }
            }
        }
        return new Promise((resolve) => { resolve() })
    },
    $on: function (key, action) {
        if (key && action) {
            var map = {};
            map.k = key;
            map.v = action;
            arrbus.push(map);
        }
    },
    arr: {
        push: function (key, action) {
            if (key && action) {
                var map = {};
                map.k = key;
                map.v = action;
                arrbus.push(map);
            }
        },
        pop: function (key) {
            if (key) {
                for (var i = 0, busLength = arrbus.length; i < busLength; i++) {
                    var map = arrbus[i];
                    if (map.k == key) {
                        map.v();
                    }
                }
            }
        }
    }
}
var bus = new Bus();
export default bus; // Export the eventBus instance
