/**
 * @(#)WhiteboardClient.java 2013-6-27 Copyright 2013 it.kedacom.com, Inc. All rights reserved.
 */
package com.kedacom.webrtc.whiteboard;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

import com.kedacom.webrtc.util.ByteUtil;

/**
 * 电子 白板客户端类
 * @author chengesheng@kedacom.com
 * @date 2013-6-27 下午5:34:03
 * @note WhiteboardClient.java
 */
public class WhiteboardClient extends MessageInbound {

	private String clientIp;

	public WhiteboardClient(HttpServletRequest request) {
		this.clientIp = getRemortIp(request);
	}

	public String getClientIp() {
		return clientIp;
	}

	@Override
	protected void onBinaryMessage(ByteBuffer data) throws IOException {
		sendBinaryMessage(data);
	}

	@Override
	protected void onTextMessage(CharBuffer message) throws IOException {
//		sendTextMessage("[" + getClientIp() + "] " + message.toString());
		sendTextMessage(message.toString());
	}

	@Override
	protected void onClose(int status) {
		WhiteboardHelper.getClientList().remove(this);
//		try {
//			sendTextMessage(getClientIp());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		super.onClose(status);
	}

	@Override
	protected void onOpen(WsOutbound outbound) {
		super.onOpen(outbound);

//		try {
//			sendTextMessage(getClientIp());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		WhiteboardHelper.getClientList().add(this);
	}

	// 发送文本消息给所有人
	private void sendTextMessage(String message) {
		if (message == null) {
			return;
		}

		for (MessageInbound client : WhiteboardHelper.getClientList()) {
			sendTextMessage(client, message);
		}
	}

	// 发送文本消息给指定的人
	private void sendTextMessage(MessageInbound client, String message) {
		try {
			if (client == null) {
				client = this;
			}

			WsOutbound outbound = client.getWsOutbound();
			outbound.writeTextMessage(ByteUtil.string2CharBuffer(message));
			outbound.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 发送字节流给所有人
	private void sendBinaryMessage(ByteBuffer data) {
		if (data == null) {
			return;
		}

		for (MessageInbound client : WhiteboardHelper.getClientList()) {
			if (client != this) {
				sendBinaryMessage(client, data);
			}
		}
	}

	// 发送字节流给指定终端
	private void sendBinaryMessage(MessageInbound client, ByteBuffer data) {
		try {
			if (client == null) {
				client = this;
			}

			WsOutbound outbound = client.getWsOutbound();
			outbound.writeBinaryMessage(data);
			outbound.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取客户端IP地址
	 * @param request
	 * @return
	 */
	private String getRemortIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (isNotIp(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			if (isNotIp(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
				if (isNotIp(ip)) {
					ip = request.getRemoteAddr();
				}
			}
		}
		return ip;
	}

	/**
	 * 是否是IP地址
	 * @param ip
	 * @return
	 */
	private boolean isNotIp(String ip) {
		return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip) || "127.0.0.1".equals(ip);
	}
}
