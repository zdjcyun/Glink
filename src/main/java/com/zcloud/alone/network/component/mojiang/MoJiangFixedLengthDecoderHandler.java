package com.zcloud.alone.network.component.mojiang;

import com.zcloud.alone.network.annotation.NettyHandler;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 墨江
 * @author xiadaru
 */
@Slf4j
@Component
@NettyHandler(nettyServer = {"mojiang"},order = 2)
public class MoJiangFixedLengthDecoderHandler extends FixedLengthFrameDecoder{

    public MoJiangFixedLengthDecoderHandler() {
        this(95);
    }
    public MoJiangFixedLengthDecoderHandler(int frameLength) {
        super(frameLength);
    }
}
