//江西飞尚固定杆式导轮式测斜仪解析
//生成采集指令
function FsRodsurvey_generateInstruct(sensorAddr, sensorType) {
	if (sensorAddr.length === 2){
		sensorAddr = '00' + sensorAddr;
	}
    var partInstruct = '0016' + sensorAddr + '0100';
    var crc16 = toModbusCRC16(partInstruct, false); //true代表低位在前，false代表高位在前 。
    return partInstruct + crc16;
}

// 解析回复指令(默认已去除所有空格)
function FsRodsurvey_parserInstruct(msg) {
    // 判断CRC16是否一致
    if (!FsRodsurvey_vaildMsgLength(msg)) {
        return "ERROR01" //ERROR01 代表长度不够 异常
    }
    if (!FsRodsurvey_vaildMsgCrc16(msg)) {
        return "ERROR02" //ERROR02 代表CRC16校验不一致 异常
    }
    var sensorAddr = msg.substring(4, 8);
    // 通用数据格式需除去设备类型2个字节，模块号2个字节，功能码1个字节，预留数据6个字节，总共一起11个字节
    var dataBody = msg.substring(22, msg.length - 4);
	var tmpTem = dataBody.substring(0, 4)
	var tmpX = dataBody.substring(4, 12);
	var tmpY = dataBody.substring(12);
	// 将长整数转换成16位有符号整数
	var tem = new Int16Array([parseInt(tmpTem, 16)])[0] / 100;
	// 将长整数转换成32位有符号整数
	var arrRes = new Int32Array([parseInt(tmpX, 16), parseInt(tmpY, 16)]);
	var resultX = arrRes[0] / 1000000;
    var resultY = arrRes[1] / 1000000;

    var result = {
		temperature : tem,
        x : resultX,
		y : resultY
    }
    return JSON.stringify(result);
}

// 校验数据长度是否足够(默认已去除所有空格)pase
function FsRodsurvey_vaildMsgLength(msg) {
    // 判断长度是不是符合要求(江西飞尚固定杆式导轮式测斜仪回复指令固定23个字节)
    return msg.length === 46;
}

// 校验数据CRC16是否正确(默认已去除所有空格)
function FsRodsurvey_vaildMsgCrc16(msg) {
    // 分别获取数据和CRC16(CRC16是最后2个字节)
    var msgbody = msg.substring(0, msg.length - 4);
    var msgCrc16 = msg.substring(msg.length - 4);
    var crc16 = toModbusCRC16(msgbody, false);
    // 判断CRC16是否一致
    return msgCrc16 === crc16;
}