package com.zcloud.alone.network.device;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zcloud.alone.network.device.feign.client.HubDeviceFeignClient;
import com.zcloud.alone.network.entity.DeviceIdentity;
import com.zcloud.ginkgo.core.device.DeviceInfo;
import com.zcloud.ginkgo.core.device.defaults.DefaultDeviceOperator;
import com.zcloud.ginkgo.core.web.RestApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 设备注册中心相关接口调用
 *
 * @author xiadaru
 */
@Service
@Order(value = Integer.MIN_VALUE)
@Slf4j
public class FeignAdapterImpl {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /***
     * 设备标签缓存
     */
    private LoadingCache<DeviceIdentity, Map<String,String>> bigTagLoadingCache;

    /***
     * 子设备缓存
     */
    private LoadingCache<DeviceIdentity, List<DefaultDeviceOperator>> subDeviceLoadingCache;

    @Autowired
    private HubDeviceFeignClient hubDeviceFeignClient;

    @PostConstruct
    public void init(){
        //根据设备产品获取标签数据
        bigTagLoadingCache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(10)).expireAfterAccess(Duration.ofSeconds(10)).build(new CacheLoader<DeviceIdentity, Map<String,String>>() {
            @Override
            public Map<String,String> load(DeviceIdentity deviceIdentity) {
                return getDeviceBizTag(deviceIdentity);
            }
        });

        //子设备缓存
        subDeviceLoadingCache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(10)).expireAfterAccess(Duration.ofSeconds(10)).build(new CacheLoader<DeviceIdentity, List<DefaultDeviceOperator>>() {
            @Override
            public List<DefaultDeviceOperator> load(DeviceIdentity deviceIdentity) {
                return getSubDevices(deviceIdentity);
            }
        });
    }

    /**
     * 根据产品设备获取业务标签
     * @param deviceIdentity
     * @return
     */
    private Map<String,String> getDeviceBizTag(DeviceIdentity deviceIdentity){
        RestApiResult<Map<String,String>> restApiResult = hubDeviceFeignClient.getStandardBizTag(deviceIdentity.getProductId(),deviceIdentity.getDeviceId());
        if(restApiResult==null || !Objects.equals(RestApiResult.SUCCESS_CODE,restApiResult.getCode())){
            return Maps.newHashMap();
        }
        return restApiResult.getData();
    }

    private List<DefaultDeviceOperator> getSubDevices(DeviceIdentity deviceIdentity) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<DefaultDeviceOperator> result = Lists.newArrayList();
        try{
            byte[] restResult = hubDeviceFeignClient.getSubDevices(deviceIdentity.getProductId(), deviceIdentity.getDeviceId());
            if (restResult!=null) {
                OBJECT_MAPPER.readValue(restResult, new TypeReference<List<DefaultDeviceOperator>>() {}).stream().forEach(entry -> result.add(entry));
            }
        }catch (Exception e){
            log.error("getSubDevices productId:{},deviceId:{},cost:{},error:{},trace:{}",deviceIdentity.getProductId(),deviceIdentity.getDeviceId(),stopwatch.elapsed(TimeUnit.MILLISECONDS),e.getMessage(),e);
        }
        return result;
    }

    public List<DeviceInfo> getSubDeviceInfo(String productId, String deviceId){
        List<DeviceInfo> deviceInfos = Lists.newArrayList();
        try{
            List<DefaultDeviceOperator> list = subDeviceLoadingCache.get(DeviceIdentity.of(productId, deviceId));
            if(CollectionUtil.isEmpty(list)){
                return deviceInfos;
            }
            return list.stream().map(DefaultDeviceOperator::getDeviceInfo).collect(Collectors.toList());
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return deviceInfos;
    }

    public Map<String,String> getDeviceBizTag(String productId,String deviceId){
        try{
            return bigTagLoadingCache.get(DeviceIdentity.of(productId, deviceId));
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return Maps.newHashMap();
    }

    /**
     * 获取终端通道号
     * @param productId
     * @param deviceId
     * @return
     */
    public Integer getTerminalChannel(String productId,String deviceId){
        return MapUtil.getInt(this.getDeviceBizTag(productId, deviceId),"terminalChannel");
    }

    /**
     * 获取传感器类型
     * @param productId
     * @param deviceId
     * @return
     */
    public String getSensorKind(String productId,String deviceId){
        return MapUtil.getStr(this.getDeviceBizTag(productId, deviceId),"sensorKind");
    }

    /**
     * 获取传感器地址
     * @param productId
     * @param deviceId
     * @return
     */
    public String getSensorAddr(String productId,String deviceId){
        return MapUtil.getStr(this.getDeviceBizTag(productId, deviceId),"sensorAddr");
    }

    /**
     * 传感器的标定系数
     * @param productId
     * @param deviceId
     * @return
     */
    public Double getTimingFactor(String productId,String deviceId){
        return MapUtil.getDouble(this.getDeviceBizTag(productId, deviceId),"timingFactor");
    }

    /**
     * 获取解析脚本
     * @param productId
     * @param deviceId
     * @return
     */
    public String getScriptFileName(String productId,String deviceId){
        return MapUtil.getStr(this.getDeviceBizTag(productId, deviceId),"parserInstructTag");
    }

    /**
     * 获取采集间隔
     * @param productId
     * @param deviceId
     * @return
     */
    public Integer getCollectFrequency(String productId,String deviceId){
        return MapUtil.getInt(this.getDeviceBizTag(productId, deviceId),"collectFrequency");
    }

    /**
     * 获取采集指令
     * @param productId
     * @param deviceId
     * @return
     */
    public String getCollectInstruct(String productId,String deviceId){
        return MapUtil.getStr(this.getDeviceBizTag(productId, deviceId),"collectInstruct");
    }

}
