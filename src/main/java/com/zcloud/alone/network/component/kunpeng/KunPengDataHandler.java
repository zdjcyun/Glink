package com.zcloud.alone.network.component.kunpeng;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcloud.alone.conf.ConnectProperties;
import com.zcloud.alone.constant.AttrKeyConstant;
import com.zcloud.alone.constant.DeviceConstant;
import com.zcloud.alone.network.annotation.NettyHandler;
import com.zcloud.alone.network.device.FeignAdapterImpl;
import com.zcloud.alone.network.helper.CommonHelper;
import com.zcloud.alone.network.link.GinkgoLinkClint;
import com.zcloud.alone.util.CodeUtils;
import com.zcloud.ginkgo.core.device.DeviceInfo;
import com.zcloud.ginkgo.core.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author dzm
 */
@Slf4j
@NettyHandler(nettyServer = {"kunpeng"},order = 3)
public class KunPengDataHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private ConnectProperties connectProperties;

	public KunPengDataHandler(ConnectProperties connectProperties){
		this.connectProperties = connectProperties;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
		// 复制内容到字节数组bytes
		byte[] bytes = new byte[in.readableBytes()];
		in.readBytes(bytes);
		// 将字节数组转换为16进制字符串
		String msg = CodeUtils.bytesToHex(bytes);
		//String msg ="09 55 54 7F 73 99 44 8A 79 B0 41 6C 00 00 00 00 00 00 44 F2 5F 48 41 64 00 00 00 00 00 00 44 A3 25 D6 41 70 00 00 00 00 00 00 44 B0 FB 65 41 68 00 00 00 00 00 00 44 95 C5 2A 41 74 00 00 00 00 00 00 44 B7 25 AB 41 6C 00 00 00 00 00 00 44 AE 66 76 41 70 00 00 00 00 00 00 44 E3 77 8D 41 6C 00 00 00 00 00 00 44 A0 C2 C1 41 6C 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 5C 94";

		//获取当前通道
		Channel channel = ctx.channel();
		//根据通道上对应的 deviceId
		String deviceId = channel.attr(AttrKeyConstant.TERMINAL_ID).get();
		log.info("产品ID:{},设备ID:{}-{} 接受到待处理原始消息:{}", this,connectProperties.getProductId(),deviceId, channel.id().asShortText(), msg);
		dealData(deviceId, msg);
	}

	private void dealData(String deviceId, String msg) {
		// 一旦通道可用, 更新终端为在线状态
		CommonHelper.deviceOnLine(this.connectProperties.getProductId(),deviceId);

		FeignAdapterImpl feignAdapterImpl = SpringUtil.getBean(FeignAdapterImpl.class);

		List<DeviceInfo> deviceInfos = feignAdapterImpl.getSubDeviceInfo(this.connectProperties.getProductId(),deviceId);
		if (CollectionUtil.isEmpty(deviceInfos)) {
			log.error("=== ERROR === 根据产品ID:{},设备ID:{} 查询设备绑定信息返回空值或多个值 === ERROR ====", this.connectProperties.getProductId(),deviceId);
			return;
		}

		msg = msg.replaceAll(" ", "");
		String dataStr = msg.substring(12, msg.length() - 8);
		if(dataStr.length() % 24 != 0){
			log.error("=== ERROR === 产品ID:{},设备ID:{} 传回的数据异常，无法解析 === ERROR ====", this.connectProperties.getProductId(),deviceId);
			return;
		}
		String[] dataArr = new String[dataStr.length() / 24];
		int i = 0, start = 0;
		while(i < dataArr.length){
			start = 24 * i;
			dataArr[i] = dataStr.substring(start, start + 24);
			i ++;
		}
		GinkgoLinkClint ginkgoLinkClint = SpringUtil.getBean(GinkgoLinkClint.class);
		for (DeviceInfo deviceInfo : deviceInfos) {
			try{
				Integer terminalChannel = feignAdapterImpl.getTerminalChannel(deviceInfo.getProductId(),deviceInfo.getDeviceId());
				String sensorKind = feignAdapterImpl.getSensorKind(deviceInfo.getProductId(),deviceInfo.getDeviceId());
				Double timingFactor = feignAdapterImpl.getTimingFactor(deviceInfo.getProductId(),deviceInfo.getDeviceId());
				if(terminalChannel==null || StringUtils.isBlank(sensorKind) || timingFactor==null || dataArr.length < terminalChannel){
					log.error("dealData error 产品ID:{},设备ID:{},terminalChannel:{},sensorKind:{},timingFactor:{},dataArr.length:{}",
							deviceInfo.getProductId(),deviceInfo.getDeviceId(),terminalChannel,sensorKind,timingFactor,dataArr.length);
					continue;
				}

				String originData = dataArr[terminalChannel - 1];
				String dataJson;
				float resultX = CodeUtils.hexToFloat(originData.substring(0, 8));
				float resultY = CodeUtils.hexToFloat(originData.substring(8, 16));
				// 如果是振弦式传感器，则需乘上标定系数进行校准
				if(NumberUtil.parseInt(sensorKind) == DeviceConstant.VIBRATING){
					double physicalData = timingFactor * (resultX * resultX);
					dataJson = "{\"x\":" + physicalData + ",\"temperature\":" + resultY + "}";
				}else if(NumberUtil.parseInt(sensorKind) == DeviceConstant.PROTOCOL){
					dataJson = "{\"x\":" + resultX + ",\"y\":" + resultY + "}";
				}else if(NumberUtil.parseInt(sensorKind) == DeviceConstant.KP_TEMPERATURE){
					// 对于温度传感器，Y值就是温度
					dataJson = "{\"x\":" + resultY + "}";
				}else{
					dataJson = "{\"x\":" + resultX + "}";
				}
				//数据存储
				ginkgoLinkClint.pushPackeData(deviceInfo.getProductId(),deviceInfo.getDeviceId(),objectMapper.readValue(dataJson,Map.class));
			}catch (Exception e){
				log.error("dealData error 产品ID:{},设备ID:{} error:{}",deviceInfo.getProductId(),deviceInfo.getDeviceId(),e.getMessage());
			}
		}
	}
}
