package com.zcloud.alone.network.component.kunpeng;

import com.zcloud.alone.network.annotation.NettyHandler;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteOrder;

/**
 * Title : KunPengMsgDecoder管理
 * Description : 处理鲲鹏终端发送数据的分包，组包问题
 * @Author dzm
 */
@Component
@NettyHandler(nettyServer = {"kunpeng"},order = 2)
public class KunPengPacketDecoder extends LengthFieldBasedFrameDecoder {
	
	private static final Logger logger = LoggerFactory.getLogger(KunPengPacketDecoder.class);

	public KunPengPacketDecoder(){
		this(1024, 3, 1, 0, 4);
	}

	/**
	 * @param maxFrameLength 解码时，处理每个帧数据的最大长度
	 * @param lengthFieldOffset 该帧数据中，存放该帧数据的长度的数据的起始位置
	 * @param lengthFieldLength 记录该帧数据长度的字段本身的长度
	 * @param lengthAdjustment 修改帧数据长度字段中定义的值，可以为负数
	 * @param initialBytesToStrip 解析的时候需要跳过的字节数
	 */
	public KunPengPacketDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
	}
	
	@Override
	protected long getUnadjustedFrameLength(ByteBuf buf, int offset,
			int length, ByteOrder order) {
		long frameLength;
		frameLength = super.getUnadjustedFrameLength(buf, offset, length, order);
		logger.info("鲲鹏数据通道数：" + frameLength + ", 字节长度  ：" + (frameLength * 12 + 10));
        return frameLength * 12 + 10;
	}
}