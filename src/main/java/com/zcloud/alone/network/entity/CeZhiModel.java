package com.zcloud.alone.network.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author brady_cc
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class CeZhiModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * method
     */
    private String method;

    /**
     * 日期 6n YYMMDD STM32 RTC年份只取后两位
     */
    @JsonProperty("T01")
    private String date;

    /**
     * 时间 6n hhnnss
     */
    @JsonProperty("T02")
    private String time;

    /** 05：5分钟
     * 主动上传间隔 2n XX 01：10分钟02：20分钟03：30分钟 04：40分钟 05：50分钟 06：60分钟
     */
    @JsonProperty("T03")
    private String interval;

    /**
     * 日期&时间 Xn 例： 2017-10-22 12:31:27
     */
    @JsonProperty("T04")
    private String dateTime;

    /**
     * 设备类型 2n XX 01：终端 02：采集器 03：振弦式传感器 04：测距传感器
     */
    @JsonProperty("A01")
    private String deviceType;

    /**
     * 终端编号 10n XXXXXXXXXX
     */
    @JsonProperty("A02")
    private String deviceId;

    /**
     * 采集器编号 10n XXXXXXXXXX FFFFFFFFFF：本机
     */
    @JsonProperty("A03")
    private String deviceNumber;

    /**
     * CH通道编号 2n XX 振弦式为十进制
     */
    @JsonProperty("A04")
    private String terminalChannel;

    /**
     * 传感器ID 2n XX Modbus为十六进制 地址：01~199
     */
    @JsonProperty("A05")
    private String sensorAddress;

    /**
     * 信号强度 2n XX 信号31最强
     */
    @JsonProperty("A11")
    private String terminalSignal;

    /**
     * 电源状态 2n XX Bit0: 电池供电 Bit1: 太阳能供电 Bit2: 交流电供电 Bit3: Bit4: 电池良好 Bit5:
     * 太阳能良好 Bit6: 交流电良好 Bit7:
     */
    @JsonProperty("A12")
    private String powerStatus;

    /**
     * 电池状态 2n XX Bit0: 充电中 Bit1: 充电完成 Bit2: Bit3: Bit4: Bit5: Bit6: Bit7:
     */
    @JsonProperty("A13")
    private String batteryStatus;

    /**
     * 电池电压 4n XX.XX 单位V
     */
    @JsonProperty("A14")
    private String batteryVoltage;

    /**
     * 终端版本 4n XX.XX 例：V1.00
     */
    @JsonProperty("A20")
    private String version;

    /**
     * 传感器编号 10n XXXXXXXXXX
     */
    @JsonProperty("S01")
    private String sensorNumber;

    /**
     * 传感器类型 2n XX
     */
    @JsonProperty("S02")
    private String sensorType;

    /**
     * 传感器型号 10n XXXXXXXXXX
     */
    @JsonProperty("S03")
    private String sensorModel;

    /**
     * 传感器状态 2n XX 0x00: 无传感器 0x01: 有传感器
     */
    @JsonProperty("S04")
    private String sensorStatus;

    /**
     * 分隔符 5A444A43（对应ASCII码为：ZDJC）
     */
    @JsonProperty("S08")
    private String separator;

    /**
     * V01 测量值 10n XXXXXXXXXX 振弦式：XXXXXXXX.XX Hz 测距：XXXXXXXXXX mm
     */
    @JsonProperty("V01")
    private String measuredData1;

    /**
     * V02 测量值 10n XXXXXXXXXX 振弦式：YYYYYYYY.YY Hz 测距：YYYYYYYY mm
     */
    @JsonProperty("V02")
    private String measuredData2;

    /**
     * 温度 3n XXX
     */
    @JsonProperty("V03")
    private String temperature;

    /**
     * 原始数据 Xn Modbus读取到的原始数据
     */
    @JsonProperty("V05")
    private String primitiveValues;

    /**
     * 数据长度 2n XX 联合V07使用
     */
    @JsonProperty("V06")
    private String dataLength;

    /**
     * 有效数据 Xn 联合V06使用
     */
    @JsonProperty("V07")
    private String validData;

    /**
     * 湿度 3n XXX
     */
    @JsonProperty("V08")
    private String humidity;

    /**
     * 气压 4n XXXX
     */
    @JsonProperty("V09")
    private String pressure;

    /**
     * 传感器编号（广州建筑设计院振弦式传感器）
     */
    @JsonProperty("V10")
    private String sensorIdV10;

    /**
     * 传感器类型（广州建筑设计院振弦式传感器）
     */
    @JsonProperty("V11")
    private String sensorTypeV11;

    /**
     * 数据采集方式(自动或手动)
     */
    private Integer createType;
}
