package com.zcloud.alone.network.component.youren;

import cn.hutool.extra.spring.SpringUtil;
import com.zcloud.alone.conf.ConnectProperties;
import com.zcloud.alone.constant.AttrKeyConstant;
import com.zcloud.alone.network.annotation.NettyHandler;
import com.zcloud.alone.network.helper.SyncHelper;
import com.zcloud.alone.network.link.GinkgoLinkClint;
import com.zcloud.alone.network.manage.MsgManage;
import com.zcloud.alone.util.CodeUtils;
import com.zcloud.ginkgo.core.device.DeviceUniqueId;
import com.zcloud.ginkgo.core.message.DeviceMessageRawPayload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dzm
 */
@Slf4j
@NettyHandler(nettyServer = {"mojiang","youren"},order = 5)
public class YouRenDataHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private ConnectProperties connectProperties;

	public YouRenDataHandler(ConnectProperties connectProperties){
		this.connectProperties = connectProperties;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
		//获取当前通道
		Channel channel = ctx.channel();
		//获取通道上对应的终端编号
		String deviceId = channel.attr(AttrKeyConstant.TERMINAL_ID).get();
		log.info("产品ID:{},设备ID:{}-{} 接受到待处理原始消息:{}", this.connectProperties.getProductId(),deviceId, channel.id().asShortText(), ByteBufUtil.hexDump(ByteBufUtil.getBytes(in)));
		dealData(deviceId,in);
	}
	
	private void dealData(String deviceId, ByteBuf in) {
		if(in.hasArray()){
			log.warn("接收到待处理原始消息为空串");
			return;
		}
		if(in.capacity() < 6){
			log.warn("接收到待处理原始消息为错误串{}", ByteBufUtil.getBytes(in));
			return;
		}
		// 获取发送给对应传感器指令的地址与传感器响应指令中的地址进行对比
		SyncHelper syncHelper = MsgManage.getSyncHelper(DeviceUniqueId.of(this.connectProperties.getProductId(),deviceId));
		if(null == syncHelper) {
			log.warn("接受的消息没找到对应的发送消息体");
			return;
		}
		byte[] bytes = ByteBufUtil.getBytes(in);
		//转为16进制字符串进行数据推送平台
		this.pushPackeData(CodeUtils.bytesToHex(bytes).getBytes(),syncHelper.getSubDeviceId());
		return;
	}

	protected void pushPackeData(byte[] data, String deviceId) {
		GinkgoLinkClint ginkgoLinkClint = SpringUtil.getBean(GinkgoLinkClint.class);
		ginkgoLinkClint.receivedDeviceMsg(this.connectProperties.getProductId(),deviceId,new DeviceMessageRawPayload(data));
	}
}
