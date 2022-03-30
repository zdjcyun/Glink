package com.zcloud.alone.network.component.cezhi;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcloud.alone.conf.ConnectProperties;
import com.zcloud.alone.constant.AttrKeyConstant;
import com.zcloud.alone.constant.Method;
import com.zcloud.alone.constant.ScriptConstant;
import com.zcloud.alone.network.annotation.NettyHandler;
import com.zcloud.alone.network.device.FeignAdapterImpl;
import com.zcloud.alone.network.entity.CeZhiModel;
import com.zcloud.alone.network.entity.MeasureDataJson;
import com.zcloud.alone.network.entity.PackageData;
import com.zcloud.alone.network.handler.CeZhiDataHelper;
import com.zcloud.alone.network.helper.CommonHelper;
import com.zcloud.alone.network.helper.SyncHelper;
import com.zcloud.alone.network.link.GinkgoLinkClint;
import com.zcloud.alone.network.manage.ChannelManage;
import com.zcloud.alone.network.manage.MsgManage;
import com.zcloud.alone.network.manage.ThreadManage;
import com.zcloud.alone.util.CodeUtils;
import com.zcloud.alone.util.EvalScriptUtils;
import com.zcloud.alone.util.MathUtils;
import com.zcloud.ginkgo.core.device.DeviceInfo;
import com.zcloud.ginkgo.core.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * 测智数据处理
 * @author Monster
 */
@Slf4j
@NettyHandler(nettyServer = "cezhi",order = 3)
public class CeZhiDataHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final ObjectMapper GSON = new ObjectMapper();

    private ConnectProperties connectProperties;

    public CeZhiDataHandler(ConnectProperties connectProperties){
        this.connectProperties = connectProperties;
    }


    //{"method":"NTS101","dev":"iot-cezhi1"}$_
    //{"method":"NTS102","A02":"iot-cezhi1","A04":"1","A05":"DC","V01":"10","V02":"20"}$_
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        // 复制内容到字节数组bytes
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        // 将字节数组转换为16进制字符串
        String msg = CodeUtils.bytesToHex(bytes);
        // 测智的数据解析
        msg = CodeUtils.hexToAscii(msg);
        //获取当前通道
        Channel channel = ctx.channel();
        //获取通道上对应的终端编号
        String terminalId = channel.attr(AttrKeyConstant.TERMINAL_ID).get();
        log.info("通道{}-{} 收到原始json的数据:{}", terminalId, channel.id().asShortText(), msg);
        try {
            // 按集合的形式解析
            PackageData packageData = GSON.readValue(msg, PackageData.class);
            if (packageData.getCntdata() == null) {
                // 按单条数据解析
                CeZhiModel keyBind = GSON.readValue(msg, CeZhiModel.class);
                // 单条数据转换成集合的形式
                List<CeZhiModel> ceZhiModels = new ArrayList<>();
                ceZhiModels.add(keyBind);
                packageData.setCntdata(ceZhiModels);
            }
            parserCommonData(packageData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parserCommonData(PackageData packageData) {
        if (packageData.getCntdata().size() == 0) {
            return;
        }
        CeZhiModel ceZhiModel = packageData.getCntdata().get(0);
        if (Method.METHOD_RECEIVE_NTS108.equals(ceZhiModel.getMethod())) {
            log.info("自检数据解析开始:{}", packageData.toString());
            return;
        }
        if(StringUtils.isBlank(ceZhiModel.getDeviceId())){
            log.error("测智数据解析开始设备ID(A02)为空 ceZhiModel:{}", ceZhiModel);
            return;
        }
        String method = ceZhiModel.getMethod();
        method = method.replace("NTS", "");
        int methodMath = Integer.parseInt(method);
        SyncHelper syncHelper = MsgManage.getSyncHelper(ceZhiModel.getDeviceId());
        // 如果terminalNumber在SyncHelper中有值，则为主动下发指令的响应（绑定操作和主动采集）
        if (null != syncHelper) {
            String methodRequest = "NTS" + (methodMath - 1);
            // 将结果返回
            String sendFlag = syncHelper.getSendFlag();
            try {
                CeZhiModel ceZhiModelRequest = GSON.readValue(sendFlag, CeZhiModel.class);
                if (methodRequest.equals(ceZhiModelRequest.getMethod())) {
                    syncHelper.setResponse("");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        //自主上报数据处理
        String methodResponse = "NTS" + (methodMath + 1);
        //构建返回响应数据
        CeZhiModel ceZhiModelResponse = new CeZhiModel();
        ceZhiModelResponse.setMethod(methodResponse);
        ceZhiModelResponse.setDeviceId(ceZhiModel.getDeviceId());
        ceZhiModelResponse.setDeviceNumber(ceZhiModel.getDeviceNumber());
        ceZhiModelResponse.setTerminalChannel(ceZhiModel.getTerminalChannel());
        ceZhiModelResponse.setSensorAddress(ceZhiModel.getSensorAddress());
        String sendMessage = null;
        try {
            sendMessage = GSON.writeValueAsString(ceZhiModelResponse);
            Channel channel = ChannelManage.getChannel(ceZhiModel.getDeviceId());
            if (channel != null) {
                channel.writeAndFlush(sendMessage);
                log.info("通道{}-响应测智终端数据：{}", ceZhiModel.getDeviceId(), sendMessage);
            } else {
                log.info("通道{}-响应测智终端数据失败，连接断开：", ceZhiModel.getDeviceId());
                CommonHelper.deviceOffLine(this.connectProperties.getProductId(),ceZhiModel.getDeviceId());
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(),e);
        }

        // 解析上传的数据数据
        // 如果连接kafka超时，会有线程无法释放，一直使用MaximumPoolSize创建线程直到内存溢出的问题
        ThreadManage.getInstance().addTask(() -> {
            //解析数据的方法
            parseData(packageData);
            /*String terminalId = ceZhiModel.getDeviceId();
            // 一旦通道可用, 将缓存的终端告警信息删除
            CommonHelper.clearTerminalAlarm(terminalId);
            // 一旦通道可用, 更新终端为在线状态
            CommonHelper.terminalOnLine(terminalId, new Date(), 100);*/
        });
    }

    /**
     * 解析终端上传的数据
     * @param packageData
     */
    private void parseData(PackageData packageData) {
        for (CeZhiModel ceZhiModel : packageData.getCntdata()) {
            ceZhiModel.setDateTime(packageData.getDateTime());
            // 201为应答式上传弦式传感器数据， 301为自报式上传弦式传感器数据
            if (Objects.equals(ceZhiModel.getMethod(), Method.METHOD_RECEIVE_NTS201) || Objects.equals(ceZhiModel.getMethod(), Method.METHOD_RECEIVE_NTS301)) {
                // 弦式传感器获取设备绑定关系
                DeviceInfo deviceInfo = CeZhiDataHelper.getDeviceLink(this.connectProperties.getProductId(),ceZhiModel, true);
                if (deviceInfo == null) {
                    //如果获取设备绑定关系失败，则结束数据处理
                    log.error("parseData cizhi 应答式上传弦式传感器数据,自报式上传弦式传感器数据 失败,productId:{},deviceId:{},terminalChannel:{},sensorAddress:{}",
                            connectProperties.getProductId(),ceZhiModel.getDeviceId(),ceZhiModel.getTerminalChannel(),ceZhiModel.getSensorAddress());
                    return;
                }
                saveVibratingWireData(ceZhiModel, deviceInfo);
            } else {
                // 非弦式传感器获取设备绑定关系
                DeviceInfo deviceInfo = CeZhiDataHelper.getDeviceLink(this.connectProperties.getProductId(),ceZhiModel, false);
                if (deviceInfo == null) {
                    //如果获取设备绑定关系失败，则结束数据处理
                    log.error("parseData cizhi 非弦式传感器获取设备绑定关系 失败,productId:{},deviceId:{},terminalChannel:{},sensorAddress:{}",
                            connectProperties.getProductId(),ceZhiModel.getDeviceId(),ceZhiModel.getTerminalChannel(),ceZhiModel.getSensorAddress());
                    return;
                }
                saveData(ceZhiModel, deviceInfo);
            }
        }
    }

    /**
     * 保存振弦传感器的数据
     *
     * @param ceZhiModel   收到的振弦传感器的数据
     * @param deviceInfo 设备绑定关系(子设备数据)
     * @author dzm
     */
    private void saveVibratingWireData(CeZhiModel ceZhiModel, DeviceInfo deviceInfo) {
        FeignAdapterImpl feignAdapterImpl = SpringUtil.getBean(FeignAdapterImpl.class);

        Double deviceLinkTimingFactor = feignAdapterImpl.getTimingFactor(deviceInfo.getProductId(),deviceInfo.getDeviceId());
        if(deviceLinkTimingFactor==null){
            log.error("productId:{},deviceId:{}获取传感器的标定系数失败",deviceInfo.getProductId(),deviceInfo.getDeviceId());
            return;
        }
        BigDecimal frequency = new BigDecimal(ceZhiModel.getMeasuredData1());
        BigDecimal timingFactor = new BigDecimal(deviceLinkTimingFactor);

       /*
        @TODO 原始获取产品脚本，Ginkgo中标准产品的脚本会继承到设备
        String sensorId = ceZhiModel.getSensorNumber();
        SensorCache sensorCache = (SensorCache) RedisUtils.getHash(CacheConstant.SENSOR_INFO, sensorId);
        String baseSensorId = sensorCache.getBaseSensorId().toString();
        BaseSensorCache baseSensorCache = (BaseSensorCache) RedisUtils.getHash(CacheConstant.SENSOR_PRODUCT, baseSensorId);
        String fileName = baseSensorCache.getFileName();
        if(null == fileName){
            log.info("传感器{}不存在解析脚本文件，请确认传感器添加是否正确", sensorId);
            return;
        }*/
        String scriptFileName = feignAdapterImpl.getScriptFileName(deviceInfo.getProductId(),deviceInfo.getDeviceId());
        try {
            //加载脚本
            String result = EvalScriptUtils.invokeOneLoad(scriptFileName, ScriptConstant.CALCULATE, timingFactor, frequency);
            //记录值
            ceZhiModel.setMeasuredData1(result);
        } catch (Exception e) {
            log.info("加载执行脚本{}时出错", scriptFileName);
        }
        saveData(ceZhiModel, deviceInfo);
    }

    /**
     * 保存传感器的数据
     *
     * @param ceZhiModel   收到的传感器的数据
     * @param deviceInfo 设备绑定关系(子设备数据)
     * @author dzm
     */
    private void saveData(CeZhiModel ceZhiModel, DeviceInfo deviceInfo) {
        // 无传感器 待修改 by lin.yong.qiang
        String status = "00";
        if (status.equals(ceZhiModel.getSensorStatus())) {
            // 如果有web实时检测任务，需要响应
            return;
        }
        MeasureDataJson measureDataJson = new MeasureDataJson();
        measureDataJson.setX(MathUtils.doubleRoundOff(ceZhiModel.getMeasuredData1(), 5) + "");
        if (ceZhiModel.getMeasuredData2() != null) {
            measureDataJson.setY(MathUtils.doubleRoundOff(ceZhiModel.getMeasuredData2(), 5) + "");
        }
        try {
            String json = GSON.writeValueAsString(measureDataJson);
            GinkgoLinkClint ginkgoLinkClint = SpringUtil.getBean(GinkgoLinkClint.class);
            ginkgoLinkClint.pushPackeData(deviceInfo.getProductId(),deviceInfo.getDeviceId(),GSON.readValue(json, Map.class));
        } catch (Exception e) {
            log.error("product:{},device:{},saveData error:{}",deviceInfo.getProductId(),deviceInfo.getDeviceId(),e.getMessage(),e);
        }
    }
}
