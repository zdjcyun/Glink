package com.zcloud.alone.network.helper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author dzm
 */
public class SyncHelper {
	
    /**因为请求和响应是一一对应的，因此初始化CountDownLatch值为1。*/
    private CountDownLatch latch = new CountDownLatch(1);
    
    /**需要响应线程设置的响应结果*/
    private String resData = null;
    /**存放终端发送的指令（终端需要根据这个值来做比较）*/
    private String sendFlag = null;
    /**设置接受数据是否延时*/
    private boolean isDelay = false;
    /**设置延迟任务是否存在*/
    private boolean isExit = false;
    /**设置子设备地址(传感器的地址)（与传感器应答的数据中的地址进行对比，判断此数据是否归属当前传感器）*/
    private String sensorAddr = null;
    /**设置子设备ID（传感器地址）*/
    private String subDeviceId = null;
    
    public SyncHelper() {
    }
    
    public boolean isDone() {
        if (resData != null) {
            return true;
        }
        return false;
    }
    
    /**获取响应结果，直到有结果才返回。*/
    public String get() throws InterruptedException {
        latch.await();
        return this.resData;
    }
    
    /**获取响应结果，直到有结果或者超过指定时间就返回。*/
    public String get(long timeout, TimeUnit unit) throws InterruptedException {
        if (latch.await(timeout, unit)) {
            return this.resData;
        }
        return null;
    }
    
    /**用于设置响应结果，并且做countDown操作，通知请求线程*/
    public void setResponse(String resData) {
        this.resData = resData;
        latch.countDown();
    }
    
	/**用于设置响应结果，可延时获取结果*/
    public void setResponseAndWait(String resData) {
    	if(null == this.resData){
    		this.resData = resData;
    	}else{
    		this.resData += " " + resData;
    	}
    }
    
    /**做countDown操作，通知请求线程*/
    public void setCountDown() {
    	latch.countDown();
    }

    public String getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(String sendFlag) {
        this.sendFlag = sendFlag;
    }

    public String getResData() {
        return resData;
    }

    public boolean isDelay() {
		return isDelay;
	}

	public void setDelay(boolean isDelay) {
		this.isDelay = isDelay;
	}

    public boolean isExit() { return isExit; }

    public void setExit(boolean isExit) { this.isExit = isExit; }

    public String getSensorAddr() {
        return sensorAddr;
    }

    public void setSensorAddr(String sensorAddr) {
        this.sensorAddr = sensorAddr;
    }

    public String getSubDeviceId() {
        return subDeviceId;
    }

    public void setSubDeviceId(String subDeviceId) {
        this.subDeviceId = subDeviceId;
    }
}