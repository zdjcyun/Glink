package com.zcloud.alone.constant;

import java.io.File;

/**
 * 脚本文件相关常量类
 *
 * @author dzm
 */
public class ScriptConstant {
    /**
     * 脚本文件路径
     */
    public static final String BASE_SCRIPT_URL = System.getProperty("user.dir") + File.separator + "script" + File.separator;

    /**
     * 常用方法的脚本路径
     */
    public static final String COMMON_SCRIPT_URL = BASE_SCRIPT_URL + "CommonMethod.js";

    /**
     * 常用方法的脚本中计算CRC16校验的方法
     */
    public static final String CRC_METHOD = "toModbusCRC16";

    /**
     * 常用方法的脚本中生根据IEEE754标准十六进制字符串转单精度浮点数的方法
     */
    public static final String IEEE754_METHOD = "hexToSingle";

    /**
     * 脚本中生成采集指令的方法
     */
    public static final String GENERATE = "generateInstruct";

    /**
     * 脚本中解析回复指令的方法
     */
    public static final String PARSER = "parserInstruct";

    /**
     * 脚本中计算测量数据的方法
     */
    public static final String CALCULATE = "calculateData";
}
