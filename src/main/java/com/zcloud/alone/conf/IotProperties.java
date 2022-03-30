package com.zcloud.alone.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.TreeMap;

@ConfigurationProperties(prefix = "iot")
public class IotProperties {

    /**
     * 启用服务端测试
     */
    private boolean server;

    /**
     * dtuModbus解码组件测试
     */
    private Map<String, ServerConnectConfig> connectConfig = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /**
     * 服务端主机地址
     */
    private String host = "127.0.0.1";

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    public Map<String, ServerConnectConfig> getConnectConfig() {
        return connectConfig;
    }

    public void setConnectConfig(Map<String, ServerConnectConfig> connectConfig) {
        this.connectConfig = connectConfig;
        this.connectConfig.forEach((k,v) -> v.setNettyServer(StringUtils.isEmpty(v.getNettyServer()) ? k : v.getNettyServer()));
    }

    public ServerConnectConfig getZdDtuModbusConnectConfig(){
        return this.connectConfig.get("dtu-modbus");
    }

    public ServerConnectConfig getCeZhiConnectConfig(){
        return this.connectConfig.get("cezhi");
    }

    public ServerConnectConfig getBeiDouConnectConfig(){
        return this.connectConfig.get("beidou");
    }

    public ServerConnectConfig getKunPengConnectConfig(){
        return this.connectConfig.get("kunpeng");
    }

    public ServerConnectConfig getMoJiangConnectConfig(){
        return this.connectConfig.get("mojiang");
    }

    public ServerConnectConfig getYouRenConnectConfig(){
        return this.connectConfig.get("youren");
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public static class ServerConnectConfig extends ConnectProperties {

        public ServerConnectConfig() {
        }

        public ServerConnectConfig(Integer port) {
            super(port);
        }

        public ServerConnectConfig(String host, Integer port) {
            super(host, port);
        }

    }

}
