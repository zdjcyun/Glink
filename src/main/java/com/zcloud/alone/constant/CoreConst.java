package com.zcloud.alone.constant;

import io.netty.util.AttributeKey;

public interface CoreConst {


    /**
     * 服务端解码器
     */
    public String SERVER_DECODER_HANDLER = "ServerProtocolDecoder";

    /**
     * 服务端编码器
     */
    public String SERVER_ENCODER_HANDLER = "ServerProtocolEncoder";

    /**
     * 服务端业务处理器
     */
    public String SERVER_SERVICE_HANDLER = "ServerServiceHandler";

    /**
     * 存活状态事件处理器
     */
    public String IDLE_STATE_EVENT_HANDLER = "IdleStateEventHandler";

    /**
     * 客户端上下线、存活等事件处理器
     */
    public String EVENT_MANAGER_HANDLER = "EventManagerHandler";

    /**
     * 注册新调处理
     */
    public String LOGINHEART_SERVICE_HANDLER = "LoginHeartServiceHandler";

    /**
     * 设备编号的KEY
     */
    public AttributeKey DEVICE_ID = AttributeKey.newInstance("deviceId");

}
