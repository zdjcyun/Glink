package com.zcloud.alone.conf;

import io.netty.handler.logging.LogLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static io.netty.handler.logging.LogLevel.WARN;

@Component
@ConfigurationProperties(prefix = "netty.server")
public class NettyServerProperties {

    /**
     * netty 打印的日志级别
     */
    private LogLevel level = WARN;

    /**
     * netty Selector boss组线程数量
     */
    private short bossThreadNum = 1;

    /**
     * netty 工作组线程数量
     */
    private short workerThreadNum = 3;

    public short getBossThreadNum() {
        return bossThreadNum;
    }

    public void setBossThreadNum(short bossThreadNum) {
        this.bossThreadNum = bossThreadNum;
    }

    public short getWorkerThreadNum() {
        return workerThreadNum;
    }

    public void setWorkerThreadNum(short workerThreadNum) {
        this.workerThreadNum = workerThreadNum;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

}
