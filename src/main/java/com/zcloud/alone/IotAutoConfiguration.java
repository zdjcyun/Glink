package com.zcloud.alone;

import com.zcloud.alone.conf.IotProperties;
import com.zcloud.alone.network.component.cezhi.CeZhiDataHandler;
import com.zcloud.alone.network.component.cezhi.CeZhiLoginHeartHandler;
import com.zcloud.alone.network.component.kunpeng.KunPengDataHandler;
import com.zcloud.alone.network.component.kunpeng.KunPengLoginHeartHandler;
import com.zcloud.alone.network.component.mojiang.MoJiangLoginHeartHandler;
import com.zcloud.alone.network.component.youren.YouRenDataHandler;
import com.zcloud.alone.network.component.youren.YouRenLoginHeartHandler;
import com.zcloud.alone.network.component.youren.YouRenMsgEncoder;
import com.zcloud.alone.network.component.zd.CeZhiComponent;
import com.zcloud.alone.network.component.zd.KunPengComponent;
import com.zcloud.alone.network.component.zd.MoJiangComponent;
import com.zcloud.alone.network.component.zd.YouRenComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(IotProperties.class)
public class IotAutoConfiguration {

    @Autowired
    private IotProperties properties;


    /***测智组件***/
    @Bean
    @ConditionalOnExpression("${iot.connect-config.cezhi.port:null} != null and ${iot.server:false} != false")
    public CeZhiComponent ceZhiComponent() {
        return new CeZhiComponent(properties.getCeZhiConnectConfig());
    }

    @Bean
    @ConditionalOnExpression("${iot.connect-config.cezhi.port:null} != null and ${iot.server:false} != false")
    public CeZhiDataHandler ceZhiDataHandler(){
        return new CeZhiDataHandler(properties.getCeZhiConnectConfig());
    }

    @Bean
    @ConditionalOnExpression("${iot.connect-config.cezhi.port:null} != null and ${iot.server:false} != false")
    public CeZhiLoginHeartHandler ceZhiLoginHeartHandler(){
        return new CeZhiLoginHeartHandler(properties.getCeZhiConnectConfig());
    }


    /**鲲鹏终端**/
    @Bean
    @ConditionalOnExpression("${iot.connect-config.kunpeng.port:null} != null and ${iot.server:false} != false")
    public KunPengComponent kunPengComponent() {
        return new KunPengComponent(properties.getKunPengConnectConfig());
    }

    @Bean
    @ConditionalOnExpression("${iot.connect-config.kunpeng.port:null} != null and ${iot.server:false} != false")
    public KunPengLoginHeartHandler kunPengLoginHeartHandler(){
        return new KunPengLoginHeartHandler(properties.getKunPengConnectConfig());
    }

    @Bean
    @ConditionalOnExpression("${iot.connect-config.kunpeng.port:null} != null and ${iot.server:false} != false")
    public KunPengDataHandler kunPengDataHandler(){
        return new KunPengDataHandler(properties.getKunPengConnectConfig());
    }

    /**墨匠终端**/
    @Bean
    @ConditionalOnExpression("${iot.connect-config.mojiang.port:null} != null and ${iot.server:false} != false")
    public MoJiangComponent moJiangComponent() {
        return new MoJiangComponent(properties.getMoJiangConnectConfig());
    }

    @Bean
    @ConditionalOnExpression("${iot.connect-config.mojiang.port:null} != null and ${iot.server:false} != false")
    public MoJiangLoginHeartHandler moJiangLoginHeartHandler(){
        return new MoJiangLoginHeartHandler(properties.getMoJiangConnectConfig());
    }

    /**有人终端**/
    @Bean
    @ConditionalOnExpression("${iot.connect-config.youren.port:null} != null and ${iot.server:false} != false")
    public YouRenComponent youRenComponent() {
        return new YouRenComponent(properties.getYouRenConnectConfig());
    }

    @Bean
    @ConditionalOnExpression("${iot.connect-config.youren.port:null} != null and ${iot.server:false} != false")
    public YouRenLoginHeartHandler youRenLoginHeartHandler() {
        return new YouRenLoginHeartHandler(properties.getYouRenConnectConfig());
    }

    @Bean
    @ConditionalOnExpression("${iot.connect-config.youren.port:null} != null and ${iot.server:false} != false or ${iot.connect-config.mojiang.port:null} != null and ${iot.mojiang:false} != false")
    public YouRenDataHandler youRenDataHandler() {
        return new YouRenDataHandler(properties.getYouRenConnectConfig());
    }

    @Bean
    @ConditionalOnExpression("${iot.connect-config.youren.port:null} != null and ${iot.server:false} != false or ${iot.connect-config.mojiang.port:null} != null and ${iot.mojiang:false} != false")
    public YouRenMsgEncoder youRenMsgEncoder() {
        return new YouRenMsgEncoder(properties.getYouRenConnectConfig());
    }

}
