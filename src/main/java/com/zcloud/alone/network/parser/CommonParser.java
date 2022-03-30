package com.zcloud.alone.network.parser;

import com.zcloud.alone.constant.ScriptConstant;
import com.zcloud.alone.enums.ParserEnum;
import com.zcloud.alone.network.entity.DtuDataModel;
import com.zcloud.alone.util.EvalScriptUtils;

/**
 * 处理传感器协议解析
 */
public class CommonParser {

    /**
     * 生成传感器采集指令
     * @param fileName
     * @param sensorAddr
     * @param sensorType
     * @return
     */
    public static String generateInstruct(String fileName, String sensorAddr, String sensorType) {
        sensorType = (sensorType == null ? "" : sensorType.trim());
        return EvalScriptUtils.invokeOneLoad(fileName, ScriptConstant.GENERATE, sensorAddr, sensorType);
    }

    /**
     * 解析传感器回复指令
     * @param fileName
     * @param replyInstruct
     * @return
     */
    public static DtuDataModel parserInstruct(String fileName, String replyInstruct) {
        DtuDataModel dtuDataModel = new DtuDataModel();
        String result = EvalScriptUtils.invokeOneLoad(fileName, ScriptConstant.PARSER, replyInstruct);
        ParserEnum parserEnum = ParserEnum.getParserEnumByCode(result);
        if(null == parserEnum){
            dtuDataModel.setCode(ParserEnum.NORMAL.getCode());
            dtuDataModel.setResult(result);
        }else{
            dtuDataModel.setCode(parserEnum.getCode());
            dtuDataModel.setResult(parserEnum.getValue());
        }
        return dtuDataModel;
    }
}
