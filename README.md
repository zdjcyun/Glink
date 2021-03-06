### iot大数据采集中间件(v2.4.1)

使用java语言且基于netty, spring boot, redis等开源项目开发来的物联网网络中间件, 支持udp, tcp通讯等底层协议和http, mqtt, modbus(tcp,rtu),plc,dtu(支持心跳，设备注册功能以及AT协议和自定义协议支持),dtu for modbus tcp,dtu for modbus rtu组件适配 等上层协议. 主打工业物联网底层网络交互、设备管理、数据存储、大数据处理. (其中plc包括西门子S7系列，欧姆龙Fins，罗克韦尔CIP，三菱MC). 数据存储将使用taos数据库以及redis消息队列

#### 主要特性

- 支持服务端启动监听多个端口, 统一所有协议可使用的api接口
- 包含一套代理客户端通信协议，支持调用：客户端 -> 服务端 -> 设备 -> 服务端 -> 客户端
- 支持设备协议对象和其业务对象进行分离(支持默认业务处理器【spring单例注入】和自定义业务处理器)
- 支持同步和异步调用设备, 支持应用程序代理客户端和设备服务端和设备三端之间的同步和异步调用
- 服务端支持设备上线/下线/异常的事件通知, 支持自定义心跳事件， 客户端支持断线重连
- 丰富的日志打印功能，包括设备上线，下线提示， 一个协议的生命周期(请求或者请求+响应)等
- 支持请求时如果连接断线会自动重连(同步等待成功后发送)
- 支持客户端发送请求时如果客户端不存在将自动创建客户端(同步等待成功后发送)

#### 已/待开发的协议

1. tcp(固定长度解码, 长度字段解码, 换行符解码，自定义分隔符解码，自定义字节到报文解码)[已完成/v2.0.0]
2. mqtt协议客户端，支持连接标准mqtt broker服务器[已完成/v2.1.0]
3. 提供modbus支持[已完成/v2.2.0]
   - 支持modbus tcp、rtu [已完成/v2.4.0]
   - 支持modbus tcp/rtu for dtu [已完成/v2.4.0]
   - 支持modbus tcp客户端 [已完成/v2.4.0]
   - 支持dtu心跳、设备注册、AT指令、以及自定义指令
4. 新增plc(西门子、欧姆龙)支持(已完成/v2.4.0)
5. taos数据库适配(已完成/v2.4.0)
   - 支持单条写入、批量写入

#### 更新日志(最新 v2.4.1)

1. 完成和测试通过modbus tcp客户端协议实现(2021/9/11)
2. 重新构建modbus tcp client以及新增modbus tcp for dtu功能【v2.4.0+】(2022/4/5)
3. 重新构建modbus以及新增modbus rtu for dtu功能并新增DTU AT协议和自定义协议支持【v2.4.0+】(2022/4/5)

#### 并发测试

1. 使用方法：测试包是一个springboot应用，需要安装jdk环境，下载下来后在控制台输入运行命令：`java -jar ***.jar`。 其中iot.num参数用来指定要创建的连接数。此测试应用是用iot-test打包的源码请看iot-test模块
2. 测试方式：首先会快速创建iot.num指定的连接数量, 然后会定时(3秒)从这些数量的连接中随机取出一台并且发送报文。其中包含有一个获取服务器实时配置数据的连接，用来实时报告测试服务器的运行状态。整个测试服务端会开启两个监听端口(15800, 15811)
3. 测试环境的配置：2核8G centos8.0 带宽2M
4. 服务启动前资源详情：可用内存：5634MB  启动后(客户端连接还没创建)：5418MB

#### 模拟工具

1. [QtSwissArmyKnife](https://gitee.com/qsaker/QtSwissArmyKnife) 支持udp、tcp、modbus、websocket、串口等调试
2. [IotClient](https://github.com/zhaopeiym/IoTClient) 支持plc(西门子，欧姆龙，三菱)，modbus，串口，mqtt，tcp, udp等模拟和调试

### 使用教程

首先创建一个springboot应用

