package com.zcloud.alone.network.component.youren;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.zcloud.alone.conf.ConnectProperties;
import com.zcloud.alone.constant.AttrKeyConstant;
import com.zcloud.alone.network.annotation.NettyHandler;
import com.zcloud.alone.network.device.FeignAdapterImpl;
import com.zcloud.alone.network.helper.InteractiveHelper;
import com.zcloud.alone.network.helper.SyncHelper;
import com.zcloud.alone.network.manage.MsgManage;
import com.zcloud.alone.util.CodeUtils;
import com.zcloud.ginkgo.core.device.DeviceInfo;
import com.zcloud.ginkgo.core.device.DeviceUniqueId;
import com.zcloud.ginkgo.core.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

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
		// 复制内容到字节数组bytes
		byte[] bytes = new byte[in.readableBytes()];
		in.readBytes(bytes);
		// 将字节数组转换为16进制字符串
		String msg = CodeUtils.bytesToHex(bytes);
		//String msg = "52 04 09 5A 44 4A 43 02 43 AC 0C 10 52 E4";
		//获取当前通道
		//获取当前通道
		Channel channel = ctx.channel();
		//获取通道上对应的终端编号
		String deviceId = channel.attr(AttrKeyConstant.TERMINAL_ID).get();
		log.info("产品ID:{},设备ID:{}-{} 接受到待处理原始消息:{}", this.connectProperties.getProductId(),deviceId, channel.id().asShortText(), msg);
		dealData(deviceId,msg);
	}

	private void dealData(String deviceId, String msg) {
		if("".equals(msg)){
			log.warn("接收到待处理原始消息为空串");
			return;
		}
		if(msg.length() < 3){
			log.warn("接收到待处理原始消息为错误串{}", msg);
			return;
		}
		FeignAdapterImpl feignAdapterImpl = SpringUtil.getBean(FeignAdapterImpl.class);

		List<DeviceInfo> subDeviceInfos = feignAdapterImpl.getSubDeviceInfo(this.connectProperties.getProductId(),deviceId);
		if(CollectionUtil.isEmpty(subDeviceInfos)){
			return;
		}

		// 获取发送给对应传感器指令的地址与传感器响应指令中的地址进行对比
		//将响应的传感器指令返回给发送方
		subDeviceInfos.stream().forEach(deviceInfo -> {
			SyncHelper syncHelper = MsgManage.getSyncHelper(DeviceUniqueId.of(this.connectProperties.getProductId(),deviceId));
			if(syncHelper!=null){
				boolean isDtuSensor = feignAdapterImpl.isDtuSensor(deviceInfo.getProductId(), deviceInfo.getDeviceId());
				String sensorAddr = syncHelper.getSensorAddr();
				if (StringUtils.isNotBlank(sensorAddr)) {
					// 只有DTU连接的传感器发送指令时会设置传感器baseId和传感器地址
					if (isDtuSensor) {
						// 对某些需要进行地址对比的传感器（传感器的响应指令会带上地址）,进行地址对比
						String resSensorAddr = msg.substring(0, 2);
						// 如果不一致，等待下个传感器的指令返回
						if (!Objects.equals(sensorAddr, resSensorAddr)) {
							log.warn("产品:{},设备:{} 当前发送指令的传感器地址{}与响应指令中的地址{}不一致", deviceInfo.getProductId(),deviceInfo.getDeviceId(),sensorAddr, resSensorAddr);
						}
					}
				}
				InteractiveHelper.getAndParserRes(syncHelper, msg);
			}else{
				log.warn("产品:{},设备:{} 接受的消息没找到对应的发送消息体",deviceInfo.getProductId(),deviceInfo.getDeviceId());
			}
		});
	}
}
