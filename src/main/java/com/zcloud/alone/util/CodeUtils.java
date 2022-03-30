package com.zcloud.alone.util;

/**
 * Title : CodeUtils管理
 * Description : 处理字符串和字节数组互相转换的工具类
 * @author dzm
 */
public class CodeUtils {

	public static String decimalToHex(int decimal) {
		String hexString = Integer.toHexString(decimal);
		int hesCode=2;
        if (hexString.length() % hesCode == 1) {
        	hexString = "0" + hexString;
        }
		return hexString;
	}
	
	public static int hexToDecimal(String hexString) {
        return Integer.parseInt(hexString, 16);
	}
	
	/**
	 * 将需要发送的ascii形式数据转换成字节数组
	 * @param data 需要发送的ascii形式数据
	 * @return 转换后的字节数组
	 */
	public static byte[] asciiToBytes(String data) {
		data = asciiToHex(data);
		return hexToBytes(data);
	}

	/**
	 * 将ascii形式的数据转换为十六进制字符串形式
	 * @param 'ASCII形式的数据'
	 * @return 转换后的十六进制字符串
	 */
	public static String asciiToHex(String data) {
		char[] charArray = data.toCharArray();
		StringBuilder sb = new StringBuilder();
		int temp;
		for (int i = 0; i < charArray.length; i++) {
			//将字符转换为对应的ASCII码值
			temp = (int)charArray[i];
			//将对应的ASCII码值转换为对应的十六进制字符串,追加到字符串后
			sb.append(Integer.toHexString(temp));
			sb.append(" ");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	/**
	 * 将十六进制字符串转换成字节数组
	 * @param hexString 十六进制字符串
	 * @return 返回字节数组
	 */
	public static byte[] hexToBytes(String hexString) {
		if (null == hexString || "".equals(hexString.trim())) {
            return null;
        }
		hexString = hexString.replaceAll(" ", "");
        int len = hexString.length();
        int index = 0;
        byte[] bytes = new byte[len / 2];
        while (index < len) {
            String sub = hexString.substring(index, index + 2);
            bytes[index/2] = (byte)Integer.parseInt(sub, 16);
            index += 2;
        }
        return bytes;
	}

	/**
	 * 将接受到的字节数组转换成ascii形式数据
	 * @param bytes 字节数组
	 * @return 转换后的文本数据
	 */
	public static String bytesToAscii(byte[] bytes) {
		String hexString = bytesToHex(bytes);
		return hexToAscii(hexString);
	}

	/**
	 * 将字节数组转换成十六进制字符串
	 * @param bytes 字节数组
	 * @return 返回十六进制字符串
	 */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xFF;
            String hex = Integer.toHexString(value).toUpperCase();
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
            if(i< bytes.length - 1){
            	sb.append(" ");
            }
        }
        return sb.toString();
    }
    
	/**
	 * 将十六进制字符串形式转换为ascii形式的数据
	 * @param hexString 十六进制字符串
	 * @return 转换后的ascii形式的数据
	 */
	public static String hexToAscii(String hexString) {
		if (null == hexString || "".equals(hexString.trim())) {
            return null;
        }
	    StringBuilder sb = new StringBuilder();
	    hexString = hexString.replaceAll(" ", "");
        int len = hexString.length();
        int index = 0;
        while (index < len) {
            String sub = hexString.substring(index, index + 2);
            int i = Integer.parseInt(sub, 16);
            if(i > 127){
            	sb.append(".");
            }else{
            	sb.append((char)i);
            }
            index += 2;
        }
		return sb.toString();
	}
	
    /**
     * 16进制字符串转化Float数，包括（正数和负数）
     * 注意16进制字符串需要去除前后空格
     * @param hexString 输入的十六进制字符串
     * @return
     */
    public static float hexToFloat(String hexString) {
        String binaryString = hexToBinary(hexString);
        return binaryToFloat(binaryString);
    }

    /**
     * 16进制字符串转化2进制字符
     * @param hexString 十六进制字符串
     * @return
     */
    public static String hexToBinary(String hexString) {
        int code=2;
        if (hexString == null || hexString.length() % code != 0) {
            return null;
        }
        StringBuilder bString = new StringBuilder();
        String tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString.append(tmp.substring(tmp.length() - 4));
        }
        return bString.toString();
    }

    /**
     * 这里只处理规格化数，非规格化数，NaN，finite等没有考虑
     * 2进制字符串转Float数，包括（正数和负数）
     * @param binaryString
     * @return
     */
    public static float binaryToFloat(String binaryString) {
        // float是32位，将这个binaryString左边补0补足32位，如果是Double补足64位。
        final String stringValue = leftPad(binaryString, '0', 32);
        // 首位是符号部分，占1位。
        // 如果符号位是0则代表正数，1代表负数
        final int sign = stringValue.charAt(0) == '0' ? 1 : -1;
        // 第2到9位是指数部分，float占8位，double占11位。
        final String exponentStr = stringValue.substring(1, 9);
        // 将这个二进制字符串转成整数，由于指数部分加了偏移量（float偏移量是127，double是1023）
        // 所以实际值要减去127
        final int exponent = Integer.parseInt(exponentStr, 2) - 127;
        // 最后的23位是尾数部分，由于规格化数，小数点左边隐含一个1，现在加上
        final String mantissaStr = "1".concat(stringValue.substring(9, 32));
        // 这里用double，尽量保持精度，最好用BigDecimal，这里只是方便计算所以用double
        double mantissa = 0.0;

        for (int i = 0; i < mantissaStr.length(); i++) {
            final int intValue = Character.getNumericValue(mantissaStr.charAt(i));
            // 计算小数部分，具体请查阅二进制小数转10进制数相关资料
            mantissa += (intValue * Math.pow(2, -i));
        }
        // 根据IEEE 754 标准计算：符号位 * 2的指数次方 * 尾数部分
        return (float) (sign * Math.pow(2, exponent) * mantissa);
    }

	/**
     * 一个简单的补齐方法，很简单，没考虑很周到。
     * 具体请参考common-long.jar/StringUtils.leftPad()
     * @param str
     * @param padChar
     * @param length
     * @return
     */
    public static String leftPad(final String str, final char padChar, int length) {
        final int repeat = length - str.length();
        if (repeat <= 0) {
            return str;
        }
        final char[] buf = new char[repeat];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = padChar;
        }
        return new String(buf).concat(str);
    }
}
