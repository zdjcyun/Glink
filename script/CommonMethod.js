// --- CRC16 校验方法 (toModbusCRC16) start---------------------
function toModbusCRC16(str, isReverse) {
    return toString(crc16(strToHex(str)), isReverse);
}

function crc16(data) {
    var len = data.length;
    if (len > 0) {
        var crc = 0xFFFF;
        for (var i = 0; i < len; i++) {
            crc = (crc ^ (data[i]));
            for (var j = 0; j < 8; j++) {
                crc = (crc & 1) != 0 ? ((crc >> 1) ^ 0xA001) : (crc >> 1);
            }
        }
        var hi = ((crc & 0xFF00) >> 8);  //高位置
        var lo = (crc & 0x00FF);         //低位置
        return [hi, lo];
    }
    return [0, 0];
}

function convertChinese(str) {
    var tmp = str.split(''), arr = [];
    for (var i = 0, c = tmp.length; i < c; i++) {
        var s = tmp[i].charCodeAt();
        if (s <= 0 || s >= 127) {
            arr.push(s.toString(16));
        }
        else {
            arr.push(tmp[i]);
        }
    }
    return arr;
}

function filterChinese(str) {
    var tmp = str.split(''), arr = [];
    for (var i = 0, c = tmp.length; i < c; i++) {
        var s = tmp[i].charCodeAt();
        if (s > 0 && s < 127) {
            arr.push(tmp[i]);
        }
    }
    return arr;
}

function strToHex(hex, isFilterChinese) {
    hex = isFilterChinese ? filterChinese(hex).join('') : convertChinese(hex).join('');
    //清除所有空格
    hex = hex.replace(/\s/g, "");
    //若字符个数为奇数，补一个0
    hex += hex.length % 2 != 0 ? "0" : "";
    var c = hex.length / 2, arr = [];
    for (var i = 0; i < c; i++) {
        arr.push(parseInt(hex.substr(i * 2, 2), 16));
    }
    return arr;
}

function toString(arr, isReverse) {
    if (typeof isReverse == 'undefined') {
        isReverse = true;
    }
    var hi = arr[0], lo = arr[1];
    return padLeft((isReverse ? hi + lo * 0x100 : hi * 0x100 + lo).toString(16).toUpperCase(), 4, '0');
}

function padLeft(s, w, pc) {
    if (pc == undefined) {
        pc = '0';
    }
    for (var i = 0, c = w - s.length; i < c; i++) {
        s = pc + s;
    }
    return s;
}
// --- CRC16 校验方法 (toModbusCRC16) end---------------------

// --- IEEE754 十六进制转浮点数方法 (hexToSingle) start---------------------
function hexToSingle(t) {
    t = t.replace(/\s+/g, "");
    if (t == "") {
        return "";
    }
    if (t == "00000000") {
        return "0";
    }
    if ((t.length > 8) || (isNaN(parseInt(t, 16)))) {
        return "Error";
    }
    if (t.length < 8) {
        t = fillString(t, "0", 8, true);
    }
    t = parseInt(t, 16).toString(2);
    t = fillString(t, "0", 32, true);
    var s = t.substring(0, 1);
    var e = t.substring(1, 9);
    var m = t.substring(9);
    e = parseInt(e, 2) - 127;
    m = "1" + m;
    if (e >= 0) {
        m = m.substr(0, e + 1) + "." + m.substring(e + 1)
    }
    else {
        m = "0." + fillString(m, "0", m.length - e - 1, true)
    }
    if (m.indexOf(".") == -1) {
        m = m + ".0";
    }
    var a = m.split(".");
    var mi = parseInt(a[0], 2);
    var mf = 0;
    for (var i = 0; i < a[1].length; i++) {
        mf += parseFloat(a[1].charAt(i)) * Math.pow(2, -(i + 1));
    }
    m = parseInt(mi) + parseFloat(mf);
    if (s == 1) {
        m = 0 - m;
    }
    return m;
}

function singleToHex(t) {
    if (t == "") {
        return "";
    }
    t = parseFloat(t);
    if (isNaN(t) == true) {
        return "Error";
    }
    if (t == 0) {
        return "00000000";
    }
    var s,
        e,
        m;
    if (t > 0) {
        s = 0;
    }
    else {
        s = 1;
        t = 0 - t;
    }
    m = t.toString(2);
    if (m >= 1) {
        if (m.indexOf(".") == -1) {
            m = m + ".0";
        }
        e = m.indexOf(".") - 1;
    }
    else {
        e = 1 - m.indexOf("1");
    }
    if (e >= 0) {
        m = m.replace(".", "");
    }
    else {
        m = m.substring(m.indexOf("1"));
    }
    if (m.length > 24) {
        m = m.substr(0, 24);
    }
    else {
        m = fillString(m, "0", 24, false)
    }
    m = m.substring(1);
    e = (e + 127).toString(2);
    e = fillString(e, "0", 8, true);
    var r = parseInt(s + e + m, 2).toString(16);
    r = fillString(r, "0", 8, true);
    return insertString(r, " ", 2).toUpperCase();
}

function insertString(t, c, n) {
    var r = new Array();
    for (var i = 0; i * 2 < t.length; i++) {
        r.push(t.substr(i * 2, n));
    }
    return r.join(c);
}

function fillString(t, c, n, b) {
    if ((t == "") || (c.length != 1) || (n <= t.length)) {
        return t;
    }
    var l = t.length;
    for (var i = 0; i < n - l; i++) {
        if (b == true) {
            t = c + t;
        }
        else {
            t += c;
        }
    }
    return t;
}

function formatHex(t, n, ie) {
    var r = new Array();
    var s = "";
    var c = 0;
    for (var i = 0; i < t.length; i++) {
        if (t.charAt(i) != " ") {
            s += t.charAt(i);
            c += 1;
            if (c == n) {
                r.push(s);
                s = "";
                c = 0;
            }
        }
        if (ie == false) {
            if ((i == t.length - 1) && (s != "")) {
                r.push(s);
            }
        }
    }
    return r.join("\n");
}

function formatHexBatch(t, n, ie) {
    var a = t.split("\n");
    var r = new Array();
    for (var i = 0; i < a.length; i++) {
        r[i] = formatHex(a[i], n, ie);
    }
    return r.join("\n");
}

function hexToSingleBatch(t) {
    var a = formatHexBatch(t, 8, true).split("\n");
    var r = new Array();
    for (var i = 0; i < a.length; i++) {
        r[i] = hexToSingle(a[i]);
    }
    return r.join("\r\n");
}

function singleToHexBatch(t) {
    var a = t.split("\n");
    var r = new Array();
    for (var i = 0; i < a.length; i++) {
        r[i] = singleToHex(a[i]);
    }
    return r.join("\r\n");
}
// --- IEEE754 十六进制转浮点数方法 (hexToSingle) end---------------------