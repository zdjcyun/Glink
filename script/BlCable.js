//济南博林拉线位移计解析
//生成采集指令
function BlCable_generateInstruct(sensorAddr, sensorType) {
    var partInstruct = sensorAddr + '0300000001';
    var crc16 = toModbusCRC16(partInstruct, true); //true代表低位在前，false代表高位在前。
    return partInstruct + crc16;
}

// 解析回复指令(默认已去除所有空格)
function BlCable_parserInstruct(msg) {
    // 判断CRC16是否一致
    if (!BlCable_vaildMsgLength(msg)) {
        return "ERROR01" //ERROR01 代表长度不够 异常
    }
    if (!BlCable_vaildMsgCrc16(msg)) {
        return "ERROR02" //ERROR02 代表CRC16校验不一致 异常
    }
    var sensorAddr = msg.substring(0, 2);
    // 通用数据格式需除去地址位，功能码，长度位一起3个字节
    var dataBody = msg.substring(6, msg.length - 4);
    var resultX = parseInt(dataBody, 16) / 100;

    var result = {
        x : resultX
    }
    return JSON.stringify(result);
}

// 校验数据长度是否足够(默认已去除所有空格)pase
function BlCable_vaildMsgLength(msg) {
    // 然后获取数据长度位(长度位是第3个字节，起始索引是4，结束索引是6)
    var lengthStr = msg.substring(4, 6);
    // 将十六进制转换成十进制
    var length = parseInt(lengthStr, 16);
    // 判断长度是不是符合要求(基础数据5个字节+数据位length个字节)
    return msg.length === ((5 + length) * 2);

}

// 校验数据CRC16是否正确(默认已去除所有空格)
function BlCable_vaildMsgCrc16(msg) {
    // 分别获取数据和CRC16(CRC16是最后2个字节)
    var msgbody = msg.substring(0, msg.length - 4);
    var msgCrc16 = msg.substring(msg.length - 4);
    var crc16 = toModbusCRC16(msgbody, true);
    // 判断CRC16是否一致
    return msgCrc16 === crc16;

}