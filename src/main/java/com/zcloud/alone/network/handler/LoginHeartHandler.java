package com.zcloud.alone.network.handler;


import com.zcloud.alone.conf.ConnectProperties;
import com.zcloud.alone.constant.AttrKeyConstant;
import com.zcloud.alone.network.helper.CommonHelper;
import com.zcloud.alone.network.manage.ChannelManage;
import com.zcloud.alone.util.CodeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author dzm
 * 处理注册和心跳数据
 */
@Slf4j
public class LoginHeartHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	protected int lossConnectTime = 0;

	protected ConnectProperties connectProperties;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		//获取当前通道
		Channel channel = ctx.channel();
		//根据通道上对应的terminalId
		String terminalId = channel.attr(AttrKeyConstant.TERMINAL_ID).get();
		// 如果不存在terminalId，则是dtu或终端第一次发送过来的数据
		if (null == terminalId) {
			terminalId = parserLoginData(ctx, in);
			//解析注册包成功，获取设备编号成功
			if(null != terminalId){
				//在当前通道上附加设备编号
				channel.attr(AttrKeyConstant.TERMINAL_ID).set(terminalId.trim());
				String channelId = channel.id().asShortText();
				log.info("设备{}-{}建立连接!", terminalId, channelId);
				//如果缓存中有以前有相同的终端编号的通道，则关闭该通道
				Channel oldChannel = ChannelManage.getChannel(terminalId);
				if(null != oldChannel){
					log.debug("terminalId:{} 缓存中有以前有相同的终端编号的通道，关闭该通道",terminalId);
					oldChannel.close();
				}
				//将新通道加入到缓存中
				ChannelManage.putChannel(terminalId, channel);
				//更新终端为在线状态
				callBackActive(terminalId, channelId);
			}else{ //解析注册包失败，获取终端编号失败，则是非适配终端或者非认证终端接入，直接关闭通道
				log.warn("解析注册包失败，获取终端编号失败，则是非适配终端或者非认证终端接入，直接关闭通道");
				channel.close();
			}
		}else{
			//如果不是第一次发送过来的数据，则判断是心跳包还是数据包，分别坐相应的处理
			parserHeartOrData(ctx, in);
		}
	}

	/**
	 * 将字节数组转换为字符串
	 * @param in
	 */
	public String paserByteData(ByteBuf in) {
		// 复制内容到字节数组bytes
		byte[] bytes = new byte[in.readableBytes()];
		in.readBytes(bytes);
		// 将字节数组转换为16进制字符串
		String hexString = CodeUtils.bytesToHex(bytes);
		// 将十六进制字符串转换成字符串(按照ASCII码表转换)
		return CodeUtils.hexToAscii(hexString);
	}

	protected String parserLoginData(ChannelHandlerContext ctx, ByteBuf in) {
		String terminalId = null;
		String msg = paserByteData(in);
		//判断消息是否是注册包
		if(null == (terminalId = getLoginData(msg))){
			//非适配设备或非认证设备接入
			log.info("非适配设备或非认证设备连入-请联系管理员处理-服务已关闭该连接通道：{}", msg);
		}else{
			callBackIsLogin(ctx, msg);
		}
		return terminalId;
	}

	protected void parserHeartOrData(ChannelHandlerContext ctx, ByteBuf in) {
		// 判断是不是心跳包
		if (isHeart(in)) {
			String msg = paserByteData(in);
			callBackIsHeart(ctx, msg);
			String terminalId = ctx.channel().attr(AttrKeyConstant.TERMINAL_ID).get();
			String channelId = ctx.channel().id().asShortText();
			//更新终端为在线状态
			callBackActive(terminalId, channelId);
			log.info("设备{}-{} 心跳包：{}", terminalId, channelId, msg);
			lossConnectTime = 0;
		} else { //如果不是心跳包，往下放，交给数据处理handler处理
			ReferenceCountUtil.retain(in);
			ctx.fireChannelRead(in);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		String terminalId = ctx.channel().attr(AttrKeyConstant.TERMINAL_ID).get();
		String channelId = ctx.channel().id().asShortText();
		log.info("设备{}-{} 远程地址：{} 正在尝试连接服务 !", terminalId, channelId, ctx.channel().remoteAddress());
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		String terminalId = channel.attr(AttrKeyConstant.TERMINAL_ID).get();
		String channelId = channel.id().asShortText();
		if(channel.isActive()){
			log.info("设备{}-{} 连接异常，但没有关闭!", terminalId, channelId);
		}else{ //如果通道断开
			String tailMsg = "连接关闭!";
			//如果终端编号存在，即为适配且认证的终端接入的通道，则进行下一步的处理
			if(!StringUtils.isEmpty(terminalId)){
				//移除断开连接的通道
				ChannelManage.removeChannel(channel);
			}else{
				tailMsg = "连接关闭!(非适配)";
			}
			log.error("设备{}-{} {}", terminalId, channelId, tailMsg);
		}
		super.channelInactive(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception {
		Channel channel = ctx.channel();
		String terminalId = channel.attr(AttrKeyConstant.TERMINAL_ID).get();
		String channelId = channel.id().asShortText();
		log.info("设备{}-{} 连接异常!", terminalId, channelId);
		cause.printStackTrace();
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent){
			IdleStateEvent event = (IdleStateEvent) evt;
			if(event.state()== IdleState.READER_IDLE){
				lossConnectTime ++;
				String terminalId = ctx.channel().attr(AttrKeyConstant.TERMINAL_ID).get();
				String channelId = ctx.channel().id().asShortText();
				log.error("设备{}-{} 第{}次60秒没有接收到终端的信息了", terminalId, channelId, lossConnectTime);
				if(lossConnectTime > 2){
					log.error("设备{}-{} 关闭不活跃的通道!", terminalId, channelId);
					ctx.channel().close();
					callBackInactive(terminalId, channelId);
				}
			}
		}else {
			super.userEventTriggered(ctx,evt);
		}
	}
    
    /**
     * 获取设备注册包的数据，由子类重写，可验证设备是否非法接入（必须重写）
     * @param msg
     * @return
     */
    protected String getLoginData(String msg) {
		return null;
	}
    
    /**
     * 判断是不是设备心跳包的数据，由子类重写，决定数据下一步的流程（必须重写）
     * @param in
     * @return
     */
	protected boolean isHeart(ByteBuf in) {
		return false;
	}
    
    /**
	 * 认证通过的设备注册数据回调处理，由子类根据自身的业务逻辑重写（可不重写）
	 * @param ctx
	 * @param msg
	 */
	protected void callBackIsLogin(ChannelHandlerContext ctx, String msg) {
	}
	
	/**
	 * 认证通过的设备心跳数据回调处理，由子类根据自身的业务逻辑重写（可不重写）
	 * @param ctx
	 * @param msg
	 */
	protected void callBackIsHeart(ChannelHandlerContext ctx, String msg) {
	}

	/**
	 * 建立连接的设备回调处理，由子类根据自身的业务逻辑重写（可不重写）
	 * @param deviceId
	 * @param socketId
	 */
	protected void callBackActive(String deviceId, String socketId) {
		CommonHelper.deviceOnLine(this.connectProperties.getProductId(),deviceId);
	}

	/**
	 * 断开连接的设备回调处理，由子类根据自身的业务逻辑重写（可不重写）
	 * @param deviceId
	 * @param socketId
	 */
	protected void callBackInactive(String deviceId, String socketId) {
		CommonHelper.deviceOffLine(this.connectProperties.getProductId(),deviceId);
	}
}
