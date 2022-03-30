package com.zcloud.alone.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 格式化
 */
public class PrecisionFormattingUtils {

    public static String precision(double value){
        final DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(2);
        formater.setGroupingSize(0);
        formater.setRoundingMode(RoundingMode.FLOOR);
        return formater.format(value);
    }
}
