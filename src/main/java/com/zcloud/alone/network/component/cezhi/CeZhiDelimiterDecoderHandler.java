package com.zcloud.alone.network.component.cezhi;

import com.zcloud.alone.network.annotation.NettyHandler;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import org.springframework.stereotype.Component;

/**
 * 测智解码handler
 * @author xiadaru
 */
@Component
@NettyHandler(nettyServer = {"cezhi"},order = Integer.MIN_VALUE)
public class CeZhiDelimiterDecoderHandler extends DelimiterBasedFrameDecoder {

    /**
     * 默认构造方法
     */
    public CeZhiDelimiterDecoderHandler(){
        super(1024, Unpooled.copiedBuffer("$_".getBytes()));
    }
}
