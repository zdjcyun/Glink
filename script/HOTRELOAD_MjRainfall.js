// 墨匠传感器解析协议
//生成采集指令
function HOTRELOAD_MjRainfall_generateInstruct(sensorAddr, sensorType) {
    //示例： AA 75     1A       00 0E        00       28 4C 3D 9A 03 00 00 60      17 03 02 23 21 56        23
    //说明： 命令头   命令字    数据块长度   保留备用           传感器地址                年月日时分秒BCD        校验字节
    var content = "AA751A000E00" + sensorAddr + HOTRELOAD_MjRainfall_curTimeStr();
    return (content + HOTRELOAD_MjRainfall_genBCC(content)).toUpperCase();
}

// 解析回复指令(默认已去除所有空格)
function HOTRELOAD_MjRainfall_parserInstruct(msg) {
    // 判断CRC16是否一致
    if (!HOTRELOAD_MjRainfall_vaildMsgLength(msg)) {
        return "ERROR01" //ERROR01 代表长度不够 异常
    }
    if (!HOTRELOAD_MjRainfall_vaildMsgBCC(msg)) {
        return "ERROR02" //ERROR02 代表CRC16校验不一致 异常
    }
    //    头2B+命令1B+数据长度2B+保留字1B+ (数据帧) + 数据校验1B
    //    数据帧 = 传感器编号8B + 雨量自编号16B + 雨量计型号8B + 年月日3B + 保留3B + 当日累计降雨2B + （每小时降雨2B * 24）
    //    55 7A | 18 | 00 58 | 00 | 28 F4 04 02 CC 04 C6 5B | FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF | 59 4C 4A 2D 31 48 20 20 | 21 02 27 | 00 00 00 | 00 00 | 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 EF
    //    当天累计 第45～46
    //    当天0点  第47～48 
    //    当天1点  第49～50
    //    类推
    var currentHour = new Date().getHours();
    //  每小时降雨量（x0.1mm）
    var currentHourRain = parseInt(msg.substr((47 - 1) * 2 + 4 * currentHour, 4), 16) * 0.1;
    var result = {
        x: currentHourRain
    }
    return JSON.stringify(result);
}

// 校验数据长度是否足够(默认已去除所有空格)pase
function HOTRELOAD_MjRainfall_vaildMsgLength(msg) {
    // 然后获取数据长度位(4-5字节) ： 从传感器编号开始到数据校验字节之前的字节数
    var lengthStr = msg.substring(6, 10);
    // 将十六进制转换成十进制
    var length = parseInt(lengthStr, 16);
    // 判断长度是不是符合要求
    // 头2B+命令1B+数据长度2B+保留字1B+ (数据帧) + 数据校验1B
    return msg.length === ((6 + length + 1) * 2);
}

// 校验数据BCC是否正确(默认已去除所有空格)
function HOTRELOAD_MjRainfall_vaildMsgBCC(msg) {
    // 分别获取数据和BCC(BCC是最后1个字节)
    var msgbody = msg.substring(0, msg.length - 2);
    var msgBCC = msg.substring(msg.length - 2);
    var bcc = HOTRELOAD_MjRainfall_genBCC(msgbody);
    // 判断CRC16是否一致
    return msgBCC.toUpperCase() === bcc.toUpperCase();
}

// 生成当前时间字符串：年月日时分秒
function HOTRELOAD_MjRainfall_curTimeStr() {
    var result = "";
    var current = new Date();
    // 年
    result += (current.getFullYear() + "").substring(2);
    // 月
    result += padLeft(current.getMonth() + 1 + "", 2, '0');
    // 日
    result += padLeft(current.getDate() + "", 2, '0');
    // 时
    result += padLeft(current.getHours() + "", 2, '0');
    // 分
    result += padLeft(current.getMinutes() + "", 2, '0');
    // 秒
    result += padLeft(current.getSeconds() + "", 2, '0');
    return result;
}

// 生成字节校验码 BCC校验
// 校验码前面所有字节的异或结果
function HOTRELOAD_MjRainfall_genBCC(content) {
    var res = 0x00;
    for (var i = 0; i < content.length; i += 2) {
        res ^= parseInt((content[i] + content[i + 1]), 16) & 0xFF;
    }
    return padLeft(res.toString(16), 2);
}
