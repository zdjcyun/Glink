package com.zcloud.alone.network.component.mojiang;

import com.zcloud.alone.conf.ConnectProperties;
import com.zcloud.alone.constant.AttrKeyConstant;
import com.zcloud.alone.network.annotation.NettyHandler;
import com.zcloud.alone.network.handler.LoginHeartHandler;
import com.zcloud.alone.network.helper.CommonHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author xuhsanbin
 */
@Slf4j
@NettyHandler(nettyServer = {"mojiang"},order = 1)
public class MoJiangLoginHeartHandler extends LoginHeartHandler {

    public MoJiangLoginHeartHandler(ConnectProperties connectProperties) {
        this.connectProperties = connectProperties;
    }

    @Override
    protected void parserHeartOrData(ChannelHandlerContext ctx, ByteBuf in) {
        // 判断是不是心跳包
        int mjHeart = isMjHeart(in);
        if (mjHeart == 1) {
            String msg = paserByteData(in);
            dealHeart(ctx, msg);
        } else if(mjHeart == 2) { //如果不是心跳包，往下放，交给数据处理handler处理
            String msg = "zdjc";
            dealHeart(ctx, msg);
            ReferenceCountUtil.retain(in);
            ctx.fireChannelRead(in);
        } else { //如果不是心跳包，往下放，交给数据处理handler处理
            ReferenceCountUtil.retain(in);
            ctx.fireChannelRead(in);
        }
    }

    private void dealHeart(ChannelHandlerContext ctx, String msg) {
        callBackIsHeart(ctx, msg);
        String deviceId = ctx.channel().attr(AttrKeyConstant.TERMINAL_ID).get();
        String channelId = ctx.channel().id().asShortText();
        //更新终端为在线状态
        CommonHelper.deviceOnLine(this.connectProperties.getProductId(),deviceId);
        log.info("设备{}-{} 心跳包：{}", deviceId, channelId, msg);
        lossConnectTime = 0;
    }

    /**
     * 有人DTU的注册包以ZDJC开头，冒号分割，例：ZDJC:ZY2020050501
     */
    @Override
    protected String getLoginData(String msg) {
        char separators = ':';
        if (msg.length() > 4 && separators == msg.charAt(4)) {
            String[] splitArr = msg.split(":");
            String deviceId = splitArr[1];
            return deviceId;
        }
        return null;
    }

    /**
     * 有人dtu心跳包("zdjc")
     */
    private int isMjHeart(ByteBuf in) {
        if (in.readableBytes() < 4) {
            return 0;
        }
        boolean flag = in.getByte(0) == 122 && in.getByte(1) == 100 && in.getByte(2) == 106 && in.getByte(3) == 99;
        if(in.readableBytes() == 4 && flag){
            return 1;
        }
        /*
         * 发生沾包，读取掉前面4个字节的心跳
         */
        if(flag){
            log.info("读取后可读字节：{}", in.readableBytes());
            in.readBytes(4);
            return 2;
        }else{
            if(in.readableBytes() == 4){
                return 1;
            }
            return 0;
        }
    }
}
