package com.zcloud.alone.network.component.kunpeng;

import com.zcloud.alone.conf.ConnectProperties;
import com.zcloud.alone.network.annotation.NettyHandler;
import com.zcloud.alone.network.handler.LoginHeartHandler;
import io.netty.buffer.ByteBuf;

/**
 * @author dzm
 */
@NettyHandler(nettyServer = {"kunpeng"},order = 1)
public class KunPengLoginHeartHandler extends LoginHeartHandler {

	public KunPengLoginHeartHandler(ConnectProperties connectProperties) {
		this.connectProperties = connectProperties;
	}
	
	@Override
	protected String getLoginData(String msg) {
		String deviceId = null, kunpeng="ETUNG:";
		if(msg.startsWith(kunpeng)){
			deviceId = msg.split(":")[1];
			deviceId = deviceId.substring(0, deviceId.length() - 1);
		}
		return deviceId;
	}
	
	@Override
	protected boolean isHeart(ByteBuf in) {
		// 鲲鹏终端心跳包(大写E的ASCII码值十进制69)
		return in.getByte(0) == 69 && in.getByte(2) == 85 && in.getByte(4) == 71 && in.getByte(5) == 0;
	}
}