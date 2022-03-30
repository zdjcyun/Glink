package com.zcloud.alone.constant;

/**
 * @author brady_cc
 */
public class Method {
    // 1. NTS101 终端登录 NTS102
    // 2. NTS102 终端登录应答
    // 3. NTS105 终端心跳包
    // 4. NTS200 采集单个振弦式传感器 NTS201
    // 5. NTS202 采集单个测距传感器 NTS203
    // 6. NTS204 采集单个静力水准仪 NTS205
    // 7. NTS206 调零单个静力水准仪 NTS207
    // 8. NTS301 主动上传振弦式传感器数据LOG NTS302
    // 9. NTS303 主动上传振测距传感器数据LOG NTS304
    // 10. NTS305 主动上传静力水准仪数据LOG NTS306
    // 11. NTS402 配置终端或采集器绑定测距传感器 NTS403
    // 12. NTS404 配置终端或采集器绑定静力水准仪 NTS405

    /***
     *功能描述 SEND发送给终端，RECEIVE从终端收到
     * @author brady_cc
     * @date 2019-04-13
     * @time 14:59
     * @param
     * @return
     */

    /**
     * 终端登录应答
     */
    public static final String METHOD_RECEIVE_NTS102 = "NTS102";
    /**
     * 终端心跳包应答
     */
    public static final String METHOD_SEND_NTS106 = "NTS106";
    /**
     * 设备自检
     */
    public static final String METHOD_SEND_NTS107 = "NTS107";

    /**
     * 设备自检应答
     */
    public static final String METHOD_RECEIVE_NTS108 = "NTS108";

    /**
     * 采集单个振弦式传感器应答
     */
    public static final String METHOD_RECEIVE_NTS201 = "NTS201";
    /**
     * 采集单个测距传感器
     */
    public static final String METHOD_SEND_NTS202 = "NTS202";
    /**
     * 采集单个测距传感器应答
     */
    public static final String METHOD_RECEIVE_NTS203 = "NTS203";
    /**
     * 采集单个静力水准仪
     */
    public static final String METHOD_SEND_NTS204 = "NTS204";
    /**
     * 采集单个静力水准仪应答
     */
    public static final String METHOD_RECEIVE_NTS205 = "NTS205";
    /**
     * 调零单个静力水准仪
     */
    public static final String METHOD_SEND_NTS206 = "NTS206";
    /**
     * 调零单个静力水准仪应答
     */
    public static final String METHOD_RECEIVE_NTS207 = "NTS207";
    /**
     * NTS208采集单个拉线位移传感器
     */
    public static final String METHOD_SEND_NTS208 = "NTS208";
    /**
     * NTS209采集单个拉线位移传感器应答
     */
    public static final String METHOD_RECEIVE_NTS209 = "NTS209";
    /**
     *
     * NTS210采集单个翻斗雨量传感器
     */
    public static final String METHOD_SEND_NTS210 = "NTS210";
    /**
     * NTS211 采集单个翻斗雨量传感器应答
     */
    public static final String METHOD_RECEIVE_NTS211 = "NTS211";
    /**
     * NTS212采集单个风速传感器
     */
    public static final String METHOD_SEND_NTS212 = "NTS212";
    /**
     * NTS213采集单个风速传感器应答
     */
    public static final String METHOD_RECEIVE_NTS213 = "NTS213";
    /**
     * NTS214采集单个风向传感器
     */
    public static final String METHOD_SEND_NTS214 = "NTS214";
    /**
     * NTS215采集单个风向传感器应答
     */
    public static final String METHOD_RECEIVE_NTS215 = "NTS215";
    /**
     * NTS216采集单个液位传感器
     */
    public static final String METHOD_SEND_NTS216 = "NTS216";
    /**
     * NTS217采集单个液位传感器应答
     */
    public static final String METHOD_RECEIVE_NTS217 = "NTS217";
    /**
     * NTS218采集单个大气温湿度气压传感器
     */
    public static final String METHOD_SEND_NTS218 = "NTS218";
    /**
     * NTS219采集单个大气温湿度气压传感器应答
     */
    public static final String METHOD_RECEIVE_NTS219 = "NTS219";
    /**
     * NTS220采集单个测斜传感器
     */
    public static final String METHOD_SEND_NTS220 = "NTS220";
    /**
     * NTS221采集单个测斜传感器应答
     */
    public static final String METHOD_REVICE_NTS221 = "NTS221";
    /**
     * 主动上传振弦式传感器数据LOG应答
     */
    public static final String METHOD_RECEIVE_NTS301 = "NTS301";
    /**
     * 主动上传振弦式传感器数据LOG
     */
    public static final String METHOD_SEND_NTS302 = "NTS302";
    /**
     * 主动上传振测距传感器数据LOG应答
     */
    public static final String METHOD_RECEIVE_NTS303 = "NTS303";
    /**
     * 主动上传振测距传感器数据LOG
     */
    public static final String METHOD_SEND_NTS304 = "NTS304";
    /**
     * 主动上传静力水准仪数据LOG应答
     */
    public static final String METHOD_RECEIVE_NTS305 = "NTS305";
    /**
     * 主动上传静力水准仪数据LOG
     */
    public static final String METHOD_SEND_NTS306 = "NTS306";
    /**
     * NTS309主动上传拉线位移传感器数据LOG应答
     */
    public static final String METHOD_RECEIVE_NTS309 = "NTS309";
    /**
     * NTS310主动上传拉线位移传感器数据LOG
     */
    public static final String METHOD_SEND_NTS310 = "NTS310";
    /**
     * NTS311主动上传翻斗雨量传感器数据LOG应答
     */
    public static final String METHOD_RECEIVE_NTS311 = "NTS311";
    /**
     * NTS312主动上传翻斗雨量传感器数据LOG
     */
    public static final String METHOD_SEND_NTS312 = "NTS312";
    /**
     * NTS313主动上传风速传感器数据LOG应答
     */
    public static final String METHOD_RECEIVE_NTS313 = "NTS313";
    /**
     * NTS314主动上传风速传感器数据LOG
     */
    public static final String METHOD_SEND_NTS314 = "NTS314";
    /**
     * NTS315主动上传风向传感器数据LOG应答
     */
    public static final String METHOD_RECEIVE_NTS315 = "NTS315";
    /**
     * NTS316主动上传风向传感器数据LOG
     */
    public static final String METHOD_SEND_NTS316 = "NTS316";
    /**
     * NTS317主动上传液位传感器数据LOG应答
     */
    public static final String METHOD_RECEIVE_NTS317 = "NTS317";
    /**
     * NTS318主动上传液位传感器数据LOG应答
     */
    public static final String METHOD_SEND_NTS318 = "NTS318";
    /**
     * NTS319主动上传大气温湿度气压传感器数据LOG
     */
    public static final String METHOD_RECEIVE_NTS319 = "NTS319";
    /**
     * NTS320主动上传大气温湿度气压传感器数据LOG应答
     */
    public static final String METHOD_SEND_NTS320 = "NTS320";
    /**
     * NTS321主动上传侧斜传感器数据LOG
     */
    public static final String METHOD_RECEIVE_NTS321 = "NTS321";
    /**
     * NTS322主动上传侧斜传感器数据LOG应答
     */
    public static final String METHOD_SEND_NTS322 = "NTS322";
    /**
     * NTS323主动上传固定式测斜仪数据LOG
     */
    public static final String METHOD_RECEIVE_NTS323 = "NTS323";
    /**
     * NTS324主动上传固定式测斜仪数据LOG
     */
    public static final String METHOD_SEND_NTS324 = "NTS324";

    /**
     * NTS327主动上传鲲鹏单点位移计数据LOG
     */
    public static final String METHOD_RECEIVE_NTS327 = "NTS327";

    /**
     * NTS329主动上传鲲鹏测缝计数据LOG
     */
    public static final String METHOD_RECEIVE_NTS329 = "NTS329";

    /**
     * NTS330主动上传鲲鹏测缝计数据LOG
     */
    public static final String METHOD_RECEIVE_NTS330 = "NTS330";

    /**
     * NTS331 K波雷达数据LOG
     */
    public static final String METHOD_RECEIVE_NTS331 = "NTS331";
    /**
     * NTS332 K波雷达数据LOG
     */
    public static final String METHOD_SEND_NTS332 = "NTS332";

    /**
     * 配置终端或采集器绑定测距传感器
     */
    public static final String METHOD_SEND_NTS402 = "NTS402";
    /**
     * 配置终端或采集器绑定测距传感器应答
     */
    public static final String METHOD_RECEIVE_NTS403 = "NTS403";
    /**
     * 配置终端或采集器绑定静力水准仪
     */
    public static final String METHOD_SEND_NTS404 = "NTS404";
    /**
     * 配置终端或采集器绑定静力水准仪应答
     */
    public static final String METHOD_RECEIVE_NTS405 = "NTS405";
    /**
     * NTS406配置终端或采集器绑定拉线位移传感器
     */
    public static final String METHOD_SEND_NTS406 = "NTS406";
    /**
     * NTS407配置终端或采集器绑定拉线位移传感器应答
     */
    public static final String METHOD_RECEIVE_NTS407 = "NTS407";
    /**
     * NTS408配置终端或采集器绑定翻斗雨量传感器
     */
    public static final String METHOD_SEND_NTS408 = "NTS408";
    /**
     * NTS409配置终端或采集器绑定翻斗雨量传感器应答
     */
    public static final String METHOD_RECEIVE_NTS409 = "NTS409";
    /**
     * NTS410配置终端或采集器绑定风速传感器
     */
    public static final String METHOD_SEND_NTS410 = "NTS410";

    /**
     * NTS411配置终端或采集器绑定风速传感器应答
     */
    public static final String METHOD_RECEIVE_NTS411 = "NTS411";

    /**
     * NTS412配置终端或采集器绑定风向传感器
     */
    public static final String METHOD_SEND_NTS412 = "NTS412";

    /**
     * NTS413配置终端或采集器绑定风向传感器应答
     */
    public static final String METHOD_RECEIVE_NTS413 = "NTS413";

    /**
     * NTS414配置终端或采集器绑定液位传感器
     */
    public static final String METHOD_SEND_NTS414 = "NTS414";

    /**
     * NTS415配置终端或采集器绑定液位传感器应答
     */
    public static final String METHOD_RECEIVE_NTS415 = "NTS415";

    /**
     * NTS416配置终端或采集器绑定大气温湿度气压传感器
     */
    public static final String METHOD_SEND_NTS416 = "NTS416";

    /**
     * NTS417配置终端或采集器绑定大气温湿度气压传感器应答
     */
    public static final String METHOD_RECEIVE_NTS417 = "NTS417";
    /**
     * NTS418配置终端或采集器绑定测斜传感器
     */
    public static final String METHOD_SEND_NTS418 = "NTS418";

    /**
     * NTS419配置终端或采集器绑定测斜传感器应答
     */
    public static final String METHOD_RECEIVE_NTS419 = "NTS419";

    /**
     * NTS421配置终端或采集器绑定固定测斜仪应答
     */
    public static final String METHOD_RECEIVE_NTS421 = "NTS421";

}
