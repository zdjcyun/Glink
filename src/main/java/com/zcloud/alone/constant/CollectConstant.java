package com.zcloud.alone.constant;

/**
 * 采集管理相关常量类
 *
 * @author dzm
 */
public class CollectConstant {

    /**
     * 定时任务名称前缀
     */
    public static final String TASK_NAME_PRE = "TASK_";

    /**
     * 终端采集状态（未采集）
     */
    public static final String TERMINAL_NO_COLLECT = "未采集";

    /**
     * 终端采集状态（采集中）
     */
    public static final String TERMINAL_COLLECTING = "采集中";

    /**
     * 传感器采集激活状态（开）
     */
    public static final String SENSOR_ACTIVE_ON = "开";

    /**
     * 传感器采集激活状态（关）
     */
    public static final String SENSOR_ACTIVE_OFF = "关";

    /**
     * 定时任务JobDataMap中终端业务信息key值（terminal）
     */
    public static final String KEY_TERMINAL = "terminal";

    /**
     * 自动采集指令生成文件列表，不固定从数据库查询
     * include
     * 1. 墨匠传感器 MjCommon.js
     */
    public static final String AUTO_GEN_INSTRUCT_FILE_NAMES = "HOTRELOAD_";
}
