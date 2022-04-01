package com.zcloud.alone.network.component.youren;

import com.zcloud.alone.conf.ConnectProperties;
import com.zcloud.alone.constant.AttrKeyConstant;
import com.zcloud.alone.network.annotation.NettyHandler;
import com.zcloud.alone.util.CodeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 出站消息
 * @author xiadaru
 */
@Slf4j
@NettyHandler(nettyServer = {"mojiang","youren"},order = 6)
public class YouRenMsgEncoder extends MessageToByteEncoder<String> {

	private ConnectProperties connectProperties;

	public YouRenMsgEncoder(ConnectProperties connectProperties){
		this.connectProperties = connectProperties;
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
		//需要发送的消息
		Channel channel = ctx.channel();
		String terminalId = channel.attr(AttrKeyConstant.TERMINAL_ID).get();
		log.info("产品ID:{},设备ID:{}-{} 下发采集指令:{}", this.connectProperties.getProductId(),terminalId, channel.id().asShortText(), msg);
		byte[] bytes = CodeUtils.hexToBytes(msg);
		out.writeBytes(bytes);
	}
}