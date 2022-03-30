// 墨匠传感器解析协议
//生成采集指令
function HOTRELOAD_MjCommon_generateInstruct(sensorAddr, sensorType) {
    //示例： AA 75     10       00 0E        00       28 4C 3D 9A 03 00 00 60      17 03 02 23 21 56        23
    //说明： 命令头   命令字    数据块长度   保留备用           传感器地址                年月日时分秒BCD        校验字节
    var content = "AA7510000E00" + sensorAddr + HOTRELOAD_MjCommon_curTimeStr();
    return (content + HOTRELOAD_MjCommon_genBCC(content)).toUpperCase();
}

// 解析回复指令(默认已去除所有空格)
function HOTRELOAD_MjCommon_parserInstruct(msg) {
    // 判断CRC16是否一致
    if (!HOTRELOAD_MjCommon_vaildMsgLength(msg)) {
        return "ERROR01" //ERROR01 代表长度不够 异常
    }
    if (!HOTRELOAD_MjCommon_vaildMsgBCC(msg)) {
        return "ERROR02" //ERROR02 代表CRC16校验不一致 异常
    }
    // 第23-24字节表示测量值
    var dataBody = msg.substring(44, 48);
    // 取固定小数位，第35个字节的前四位表示固定小数位
    // e.g. 55 7A 10 00 35 00 28 4C 3D 9A 03 00 00 60 17 03 02 23 21 56 00 C7 7F A4 00 00 C2 32 46 B0 A1 E3 20 20 2A FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 59 48 30 30 32 30 20 20 89
    // 固定小数位
    var fixed = parseInt(msg.substring(68, 69), 16);
    var resultX = parseInt(dataBody, 16);
    if (resultX > 32767) {
        //最高位为1，是负数的补码，墨匠建议舍弃负值，取0值
        resultX = 0;
    }
    var result = {
        x: resultX / Math.pow(10, fixed)
    }
    return JSON.stringify(result);
}

// 校验数据长度是否足够(默认已去除所有空格)pase
function HOTRELOAD_MjCommon_vaildMsgLength(msg) {
    // 然后获取数据长度位(4-5字节) ： 从传感器编号开始到数据校验字节之前的字节数
    var lengthStr = msg.substring(6, 10);
    // 将十六进制转换成十进制
    var length = parseInt(lengthStr, 16);
    // 判断长度是不是符合要求
    // 头2B+命令1B+数据长度2B+保留字1B+ (传感器编号8B+时间6B+温度2B+测量值2B+偏差值2B+测试保留4B+单位ASCII 4B+测试保留1B+ 自编号16B+ 型号8B) + 数据校验1B
    return msg.length === ((6 + length + 1) * 2);
}

// 校验数据BCC是否正确(默认已去除所有空格)
function HOTRELOAD_MjCommon_vaildMsgBCC(msg) {
    // 分别获取数据和BCC(BCC是最后1个字节)
    var msgbody = msg.substring(0, msg.length - 2);
    var msgBCC = msg.substring(msg.length - 2);
    var bcc = HOTRELOAD_MjCommon_genBCC(msgbody);
    // 判断CRC16是否一致
    return msgBCC.toUpperCase() === bcc.toUpperCase();
}

// 生成当前时间字符串：年月日时分秒
function HOTRELOAD_MjCommon_curTimeStr() {
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
function HOTRELOAD_MjCommon_genBCC(content) {
    var res = 0x00;
    for (var i = 0; i < content.length; i += 2) {
        res ^= parseInt((content[i] + content[i + 1]), 16) & 0xFF;
    }
    return padLeft(res.toString(16), 2);
}