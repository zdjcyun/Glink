package com.zcloud.alone.network.component.cezhi;


import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcloud.alone.conf.ConnectProperties;
import com.zcloud.alone.constant.CommonConstant;
import com.zcloud.alone.constant.Method;
import com.zcloud.alone.network.annotation.NettyHandler;
import com.zcloud.alone.network.device.FeignAdapterImpl;
import com.zcloud.alone.network.entity.CeZhiModel;
import com.zcloud.alone.network.handler.LoginHeartHandler;
import com.zcloud.alone.network.link.GinkgoLinkClint;
import com.zcloud.ginkgo.core.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 测智心跳
 * @author xiadaru
 */
@Slf4j
@NettyHandler(nettyServer = "cezhi",order = 2)
public class CeZhiLoginHeartHandler extends LoginHeartHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginHeartHandler.class);

    private static final ObjectMapper GSON = new ObjectMapper();

    public CeZhiLoginHeartHandler(ConnectProperties connectProperties) {
        this.connectProperties = connectProperties;
    }

    /**
     * 珠海测智注册包`
     */
    @Override
    protected String getLoginData(String msg) {
        String terminalNumber = null, bindString = "{\"method\":\"NTS101\"";
        if (msg.startsWith(bindString)) {
            terminalNumber = msg.substring(26, 36);
        }
        return terminalNumber;
    }

    @Override
    protected void callBackIsLogin(ChannelHandlerContext ctx, String msg) {
        CeZhiModel ceZhiModel = null;
        try {
            ceZhiModel = GSON.readValue(msg, CeZhiModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //心跳数据上报
        String deviceId = this.getLoginData(msg);

        CeZhiModel jsonModel = new CeZhiModel();
        jsonModel.setMethod(Method.METHOD_RECEIVE_NTS102);
        if (ceZhiModel != null) {
            jsonModel.setDeviceId(ceZhiModel.getDeviceId());
            jsonModel.setPowerStatus(ceZhiModel.getPowerStatus());
        }

        String json4key2Domain1 = null;
        try {
            json4key2Domain1 = GSON.writeValueAsString(jsonModel);

            //数据上报
            GinkgoLinkClint ginkgoLinkClint = SpringUtil.getBean(GinkgoLinkClint.class);
            ginkgoLinkClint.pushPackeData(this.connectProperties.getProductId(),deviceId,GSON.readValue(json4key2Domain1, Map.class));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(),e);
        }
        ctx.writeAndFlush(json4key2Domain1);
        logger.info("响应测智终端注冊：" + json4key2Domain1);
    }

    @Override
    protected boolean isHeart(ByteBuf in) {
        // 测智终端心跳包("NTS105")
        return in.getByte(11) == 78 && in.getByte(14) == 49 && in.getByte(16) == 53;
    }

    @Override
    protected void callBackIsHeart(ChannelHandlerContext ctx, String msg) {
        logger.info("心跳包解析开始:" + msg);
        CeZhiModel ceZhiModel = null;
        try {
            ceZhiModel = GSON.readValue(msg, CeZhiModel.class);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }

        if (ceZhiModel == null || StringUtils.isBlank(ceZhiModel.getDeviceId())) {
            logger.info("未添加的终端编号:" + ceZhiModel.getDeviceId());
            return;
        }

        int collectFrequency = 0;
        FeignAdapterImpl feignAdapterImpl = SpringUtil.getBean(FeignAdapterImpl.class);
        Integer devicCollectFrequency = feignAdapterImpl.getCollectFrequency(super.connectProperties.getProductId(),ceZhiModel.getDeviceId());
        if(devicCollectFrequency!=null){
            collectFrequency = devicCollectFrequency / 60;
        }

        String intervalString = "01";
        switch (collectFrequency) {
            case (10): {
                intervalString = "01";
                break;
            }
            case (20): {
                intervalString = "02";
                break;
            }
            case (30): {
                intervalString = "03";
                break;
            }
            case (40): {
                intervalString = "04";
                break;
            }
            case (50): {
                intervalString = "05";
                break;
            }
            case (60): {
                intervalString = "06";
                break;
            }
            default:
                intervalString = "01";
        }
        CeZhiModel ceZhiModel1 = new CeZhiModel();
        ceZhiModel1.setDeviceId(ceZhiModel.getDeviceId());
        LocalDateTime now = LocalDateTime.now();
        String yyyyMMdd = DateTimeFormatter.ofPattern(CommonConstant.DATE_CEZHI).format(now);
        String HHmmss = DateTimeFormatter.ofPattern(CommonConstant.TIME_CEZHI).format(now);

        ceZhiModel1.setMethod(Method.METHOD_SEND_NTS106);
        ceZhiModel1.setDate(yyyyMMdd);
        ceZhiModel1.setTime(HHmmss);
        ceZhiModel1.setInterval(intervalString);
        String json4key2Domain1 = null;
        try {
            json4key2Domain1 = GSON.writeValueAsString(ceZhiModel1);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(),e);
        }
        ctx.writeAndFlush(json4key2Domain1);
        logger.info("响应测智终端心跳：" + json4key2Domain1);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(),cause);
        super.exceptionCaught(ctx, cause);
    }
}
