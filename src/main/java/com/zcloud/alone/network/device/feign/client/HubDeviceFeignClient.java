package com.zcloud.alone.network.device.feign.client;

import com.zcloud.ginkgo.core.device.DeviceInfo;
import com.zcloud.ginkgo.core.device.ProductInfo;
import com.zcloud.ginkgo.core.web.RestApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Set;

/**
 * 设备消息client
 * @author xiadaru
 */
@FeignClient(contextId = "alone-device", name = "ginkgo-device")
public interface HubDeviceFeignClient {

    /**
     * 获取单个产品操作
     * @param productId
     * @return
     */
    @GetMapping("/device/register/getProductOperator")
    byte[] getProduct(@RequestParam("productId") String productId);


    /**
     * 根据产品获取产品状态
     * @param productIds
     * @return
     */
    @GetMapping("/device/register/getProductState")
    byte[] getProductState(@RequestParam(name = "productIds") Set<String> productIds);

    /**
     * 产品注册
     * @param productInfo
     * @return
     */
    @PutMapping("/device/register/registerProductInfo")
    byte[] registerProduct(@RequestBody ProductInfo productInfo);

    /**
     * 产品注销
     * @param productId
     * @return
     */
    @PutMapping("/device/register/unregisterProduct")
    void unregisterProduct(@RequestParam("productId") String productId);

    /**
     * 设备注册
     * @param deviceInfo
     * @return
     */
    @PutMapping("/device/register/registerDevice")
    byte[] registerDevice(@RequestBody DeviceInfo deviceInfo);

    /**
     * 设备注销
     * @param productId
     * @param deviceId
     * @return
     */
    @PutMapping("/device/register/unregisterDevice")
    void unregisterDevice(@RequestParam("productId") String productId, @RequestParam("deviceId") String deviceId);

    /**
     * 获取单个设备操作
     * @param productId
     * @param deviceId
     * @return
     */
    @GetMapping("/device/register/getDevice")
    byte[] getDevice(@RequestParam("productId") String productId, @RequestParam("deviceId") String deviceId);


    /**
     * 根据租户获取产品列表
     * @param tenantId
     * @param deviceId
     * @return
     */
    @GetMapping("/device/register/getTenantProductOperator")
    byte[] getTenantProductOperator(@RequestParam("tenantId") String tenantId, @RequestParam("deviceId") String deviceId);


    /**
     * 获取网关设备
     * @param subProductId
     * @param subDeviceId
     * @return
     */
    @GetMapping("/device/register/getGatewayDevice")
    byte[] getGatewayDevice(@RequestParam("subProductId") String subProductId, @RequestParam("subDeviceId") String subDeviceId);

    /**
     * 获取产品下所有的设备
     * @param productId
     * @return
     */
    @GetMapping("/device/register/getDevices")
    byte[] getDevices(@RequestParam("productId") String productId);

    /**
     * 获取子设备
     * @param productId 网关设备产品ID
     * @param deviceId  网关设备设备ID
     * @return
     */
    @GetMapping("/device/register/getSubDevices")
    byte[] getSubDevices(@RequestParam("productId") String productId, @RequestParam("deviceId") String deviceId);

    /**
     * 获取网关子设备
     * @param productId
     * @param deviceId
     * @param subIdentifier
     * @return
     */
    @GetMapping("/device/register/getSubDevice")
    byte[] getSubDevice(@RequestParam("productId") String productId, @RequestParam("deviceId") String deviceId, @RequestParam("subIdentifier") String subIdentifier);

    /**
     * 改变设备的在线离线状态
     * @param productId
     * @param deviceId
     * @param deviceState
     */
    @PutMapping("/device/register/changeDeviceState")
    Boolean changeDeviceState(@RequestParam("productId") String productId, @RequestParam("deviceId") String deviceId, @RequestParam("deviceState") Integer deviceState);

    /**
     * 获取脚本解析
     * @param productId
     * @return
     */
    @GetMapping("/device/register/getProductScript")
    byte[] getProductScript(@RequestParam("productId") String productId);

    /**
     * 查找设备名称
     * @param productId
     * @param deviceId
     * @return
     */
    @GetMapping("/device/devices/get_device_name")
    RestApiResult<String> getDeviceName(@RequestParam("productId") String productId, @RequestParam("deviceId") String deviceId);


    /**
     * 获取解析脚本
     * @param productId
     * @param deviceId
     * @return
     */
    @GetMapping("/device/devices/standard_biz_tag")
    RestApiResult<Map<String,String>> getStandardBizTag(@RequestParam("productId") String productId, @RequestParam("deviceId") String deviceId);


}
