/**
 * Created by yuhaihui on 2017/12/5.
 */


let kit = {

};


kit.clone=function clone(obj) {
    // Handle the 3 simple types, and null or undefined
    if (null == obj || "object" != typeof obj) return obj;

    // Handle Date
    if (obj instanceof Date) {
        var copy = new Date();
        copy.setTime(obj.getTime());
        return copy;
    }

    // Handle Array
    if (obj instanceof Array) {
        var copy = [];
        for (var i = 0,len = obj.length; i < len; ++i) {
            copy[i] = clone(obj[i]);
        }
        return copy;
    }

    // Handle Object
    if (obj instanceof Object) {
        var copy = {};
        for (var attr in obj) {
            if (obj.hasOwnProperty(attr)) copy[attr] = clone(obj[attr]);
        }
        return copy;
    }

    throw new Error("Unable to copy obj! Its type isn't supported.");
}

kit.uuid=function uuid() {
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";

    var uuid = s.join("");
    return uuid;
}
kit.getColor=function getColor() {
    //定义字符串变量colorValue存放可以构成十六进制颜色值的值
    var colorValue="0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f";
    //以","为分隔符，将colorValue字符串分割为字符数组["0","1",...,"f"]
    var colorArray = colorValue.split(",");
    var color="#";//定义一个存放十六进制颜色值的字符串变量，先将#存放进去
    //使用for循环语句生成剩余的六位十六进制值
    for(var i=0;i<6;i++){
        //colorArray[Math.floor(Math.random()*16)]随机取出
        // 由16个元素组成的colorArray的某一个值，然后将其加在color中，
        //字符串相加后，得出的仍是字符串
        color+=colorArray[Math.floor(Math.random()*16)];
    }
    return color;
}

kit.arrayContains=function contains(arr, obj) {
    var i = arr.length;
    while (i--) {
        if (arr[i] === obj) {
            return true;
        }
    }
    return false;
}

kit.simulateChange=function simulateClick(elem) {
    if (document.createEvent) {
        let event = document.createEvent("HTMLEvents");
        event.initEvent("input", false, false);
        elem.dispatchEvent(event);
    }else if (el.fireEvent) { // IE
        el.fireEvent('input');
    }
}

export default kit
