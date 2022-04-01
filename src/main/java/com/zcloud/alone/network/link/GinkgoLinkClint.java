package com.zcloud.alone.network.link;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.zcloud.alone.conf.IotProperties;
import com.zcloud.alone.enums.ParserEnum;
import com.zcloud.alone.network.device.FeignAdapterImpl;
import com.zcloud.alone.network.entity.DtuDataModel;
import com.zcloud.alone.network.helper.InteractiveHelper;
import com.zcloud.alone.network.manage.DtuTaskManager;
import com.zcloud.alone.network.parser.CommonParser;
import com.zcloud.ginkgo.common.info.util.ShiroUserUtils;
import com.zcloud.ginkgo.core.device.DeviceInfo;
import com.zcloud.ginkgo.core.device.DeviceSessionInfo;
import com.zcloud.ginkgo.core.device.DeviceUniqueId;
import com.zcloud.ginkgo.core.message.*;
import com.zcloud.ginkgo.core.message.payload.PropertyReportPayload;
import com.zcloud.ginkgo.core.message.support.DeviceMessageBuilder;
import com.zcloud.ginkgo.core.util.StringUtils;
import com.zcloud.ginkgo.link.GinkgoLinkService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@Slf4j
@Component
@Order(value = Integer.MAX_VALUE)
public class GinkgoLinkClint {

    private static final ObjectMapper GSON = new ObjectMapper();

    @Autowired
    private GinkgoLinkService ginkgoLinkService;

    @Autowired
    private FeignAdapterImpl feignAdapterImpl;

    @Autowired
    private IotProperties properties;

    private static Map<DeviceUniqueId, Semaphore> semaphore = new ConcurrentHashMap<>();

    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(50, 100, 200, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(1000));

    @PostConstruct
    public void init(){
        ginkgoLinkService.onSendToDeviceThingMsg().doOnNext(this::deviceAccept).subscribe();
    }

    /**
     * 设备上线
     * @param productId
     * @param deviceId
     */
    public void deviceConnected(String productId,String deviceId){
        log.info("iothub-->ginkgo  deviceConnected  产品ID:{},设备ID:{} ",productId,deviceId);
        ginkgoLinkService.deviceConnected( new DeviceSessionInfo(IdUtil.randomUUID(),productId,deviceId,null,null,System.currentTimeMillis()));
    }

    /**
     * 设备上线
     * @param productId
     * @param deviceId
     */
    public void deviceDisconnected(String productId,String deviceId){
        log.info("iothub-->ginkgo  deviceDisconnected  产品ID:{},设备ID:{} ",productId,deviceId);
        ginkgoLinkService.deviceDisconnected( new DeviceSessionInfo(IdUtil.randomUUID(),productId,deviceId,null,null,System.currentTimeMillis()));
    }

    /**
     * 物模型数据推送
     * @param productId
     * @param deviceId
     * @param values
     */
    public void pushPackeData(String productId, String deviceId, Map<String,Object> values) {
        PropertyReportPayload payload = new PropertyReportPayload();
        payload.setParams(values);
        log.info("iothub-->ginkgo  pushPackeData 产品ID:{},设备ID:{} values:{} ",productId,deviceId,values);
        this.receivedDeviceMsg(productId, deviceId, payload);
    }

    /**
     * 设备数据推送
     * @param productId
     * @param deviceId
     * @param payload
     */
    public void receivedDeviceMsg(String productId, String deviceId, DeviceMessagePayload payload){
        log.info("iothub-->ginkgo  receivedDeviceMsg 产品ID:{},设备ID:{}, payload:{} ",productId,deviceId,payload);
        final DeviceMessage<?> message = new DeviceMessageBuilder()
                .withProductId(productId)
                .withDeviceId(deviceId)
                .withTimestamp(System.currentTimeMillis())
                .withPayload(payload)
                .withTenantId(ShiroUserUtils.getCurrentTenantId().orElse(null))
                .build();
        ginkgoLinkService.receivedDeviceMsg(message);
    }


    private DtuDataModel getDtuDataModel(String productId,String deviceId,String subpProductId,String subDeviceId,Object queryInstruct){
        log.info("产品:{} 设备:{},子产品：{},子设备：{}, 查询指令:{}", productId, deviceId, subpProductId,subDeviceId,queryInstruct);
        Stopwatch stopwatch = Stopwatch.createStarted();
        String sensorAddr = feignAdapterImpl.getSensorAddr(subpProductId,subDeviceId);
        if(StringUtils.isBlank(sensorAddr)){
            log.error("产品:{} 设备:{},子产品：{},子设备：{}, 查询指令:{} 传感器地址sensorAddr 为空", productId, deviceId, subpProductId,subDeviceId,queryInstruct);
            return null;
        }
        String deviceResult = InteractiveHelper.sendAndGetRes(productId,deviceId, subDeviceId, sensorAddr, queryInstruct, false);
        log.info("产品:{} 设备:{} ,子产品：{},子设备：{},查询指令:{},返回结果:{},cost:{}", productId, deviceId,subpProductId,subDeviceId, queryInstruct,deviceResult,stopwatch.elapsed(TimeUnit.MILLISECONDS));
        if(deviceResult == null){
            return null;
        }
        String replyInstruct = deviceResult.replace(" ", "").toUpperCase();
        //获取指令脚本
        String parserInstructTag = feignAdapterImpl.getScriptFileName(subpProductId,subDeviceId);
        if(StringUtils.isBlank(parserInstructTag)){
            log.error("产品:{} 设备:{} ,子产品：{},子设备：{},解析指令:{} 收到返回指令:{}解析指令出现异常:{}", productId, deviceId,subpProductId,subDeviceId, queryInstruct,deviceResult, parserInstructTag);
            return null;
        }
        // 解析返回的数据
        log.info("产品:{} 设备:{}，子产品：{},子设备：{}, 解析翻译脚本:{},解析指令:{}", productId, deviceId,subpProductId,subDeviceId, parserInstructTag,replyInstruct);
        DtuDataModel dtuDataModel = CommonParser.parserInstruct(parserInstructTag, replyInstruct);
        if (!ParserEnum.Constant.NORMAL.equals(dtuDataModel.getCode())) {
            log.error("产品:{} 设备:{}，子产品：{},子设备：{}, 解析指令:{} 收到返回指令:{}解析指令出现异常:{}", productId, deviceId,subpProductId,subDeviceId, queryInstruct,deviceResult, dtuDataModel.getResult());
            return null;
        }
        try{
            log.debug("产品:{} 设备:{} 采集指令:{} 采集结果:{}",GSON.writeValueAsString(dtuDataModel));
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return dtuDataModel;
    }

    /**
     * 定时任务下发采集指令
     * @param ginkgoDeviceMessage
     */
    private void deviceAccept(DeviceMessage<DeviceMessageThingPayload> ginkgoDeviceMessage) {
        log.info("device kafka Accept,产品:{} 设备:{}",ginkgoDeviceMessage.productId(),ginkgoDeviceMessage.deviceId());
        if (!Objects.equals(DeviceMessageType.TASK, ginkgoDeviceMessage.getPayload().getMessageType())) {
            //非task类型的任务
            log.debug("MessageType:{} is not task messageType",ginkgoDeviceMessage.getPayload().getMessageType());
            return;
        }
        if(!properties.getProductIds().contains(ginkgoDeviceMessage.productId())){
            //非standalone的产品不需要执行相应的任务
            log.debug("productId:{} is not standalone process",ginkgoDeviceMessage.productId());
            return;
        }
        DeviceUniqueId taskDeviceUniqueId = DeviceUniqueId.of(ginkgoDeviceMessage.productId(), ginkgoDeviceMessage.deviceId());
        semaphore.putIfAbsent(taskDeviceUniqueId, new Semaphore(1));
        poolExecutor.execute(new CollectInstructRunnable(taskDeviceUniqueId));
    }


    /**
     * 采集指令任务
     */
    public class CollectInstructRunnable implements Runnable {
        /**任务网关设备号**/
        private DeviceUniqueId taskDeviceUniqueId;

        public CollectInstructRunnable(DeviceUniqueId taskDeviceUniqueId) {
            this.taskDeviceUniqueId = taskDeviceUniqueId;
        }

        @SneakyThrows
        @Override
        public void run() {
            if(semaphore.get(taskDeviceUniqueId) == null || !DtuTaskManager.taskIsActive(taskDeviceUniqueId)){
                log.debug("产品:{} 设备:{} 通过关闭，不需要做任务采集");
                return;
            }
            try {
                if(!semaphore.get(taskDeviceUniqueId).tryAcquire()){
                    log.debug("产品:{} 设备:{} 正在执行采集指令，无法获取执行锁");
                    return;
                }
                //采集指令
                List<DeviceInfo> subDeviceInfos = feignAdapterImpl.getSubDeviceInfo(taskDeviceUniqueId.getProductId(), taskDeviceUniqueId.getDeviceId());
                if (CollectionUtil.isEmpty(subDeviceInfos)) {
                    log.error("产品:{} 设备:{} 获取不到子设备", taskDeviceUniqueId.getProductId(), taskDeviceUniqueId.getDeviceId());
                    return;
                }
                subDeviceInfos.forEach(subDeviceInfo -> {
                    try {
                        String collectInstruct = feignAdapterImpl.getCollectInstruct(subDeviceInfo.getProductId(), subDeviceInfo.getDeviceId());
                        if (StringUtils.isBlank(collectInstruct)) {
                            log.error("产品:{} 设备:{} collectInstruct:{}为空", taskDeviceUniqueId.getProductId(), taskDeviceUniqueId.getDeviceId(), collectInstruct);
                            return;
                        }
                        DtuDataModel dtuDataMode = getDtuDataModel(taskDeviceUniqueId.getProductId(), taskDeviceUniqueId.getDeviceId(), subDeviceInfo.getProductId(), subDeviceInfo.getDeviceId(), collectInstruct);

                        if (dtuDataMode != null) {
                            pushPackeData(subDeviceInfo.getProductId(), subDeviceInfo.getDeviceId(), GSON.readValue(dtuDataMode.getResult(), Map.class));
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }finally {
                if(semaphore.get(taskDeviceUniqueId)!=null) {
                    semaphore.get(taskDeviceUniqueId).release();
                }
                semaphore.remove(taskDeviceUniqueId);
            }
        }
    }
}
