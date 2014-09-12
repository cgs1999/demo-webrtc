/**
 * @(#)RtcClient.java 2013-10-8 Copyright 2013 it.kedacom.com, Inc. All rights
 *                    reserved.
 */

package com.kedacom.webrtc.p2p;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

import com.kedacom.webrtc.util.IpHelper;

/**
 * (用一句话描述类的主要功能)
 * @author kongchun
 * @date 2013-10-8
 */

public class VipClient extends MessageInbound {

	private String id;
	private String name;
	private String ip;
	private String browser;
	private boolean busy = false;

	/**
	 * @param request
	 */

	public VipClient(HttpServletRequest request) {
		this.id = UUID.randomUUID().toString();
		this.ip = IpHelper.getRemortIp(request);
		this.name = request.getParameter("name");
		this.browser = request.getParameter("browser");
		this.busy = false;
		try {
			this.name = new String(this.name.getBytes("ISO8859-1"), "UTF-8");
			this.browser = new String(this.browser.getBytes("ISO8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onOpen(WsOutbound outbound) {
		super.onOpen(outbound);
		this.setName(this.uniqueName(this.name, 1));
		VipUtils.getClientList().add(this);
		sendOnlineMessage();
	}

	// 创建唯一用户名
	private String uniqueName(String name, int count) {
		if (VipUtils.getRtcClineByName(name) == null) {
			return name;
		}
		String n = this.name + " (" + count + ")";
		return this.uniqueName(n, ++count);
	}

	@Override
	protected void onClose(int status) {
		VipUtils.getClientList().remove(this);
		sendOfflineMessage();
		super.onClose(status);
	}

	private void sendOnlineMessage() {
		// 给自己发送上线消息
		sendTextMessage(this, this.setMessage("info", this));
		// 更新当前的在线列表
		sendTextMessage(this, this.setMessage("list", VipUtils.getListWithoutSelf(this.id)));
		// 通知其他成员本终端上线
		broadcastTextMessageWithoutSelf(this.setMessage("add", this));

		// 通知用户聊天室信息
		sendTextMessage(this, this.setMessage("publicRoom", VipUtils.getPublicRoom()));
	}

	@SuppressWarnings("unchecked")
	private String setMessage(String str, Object t) {
		return new VipSendMessage(str, t).toString();
	}

	private String setMessage(String str, Object t, String id) {
		return new VipSendMessage(str, t, id).toString();
	}

	private void sendOfflineMessage() {
		// 通知其他成员本终端上线
		broadcastTextMessageWithoutSelf(this.setMessage("remove", this));
	}

	@Override
	protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
		// TODO 该方法尚未实现

	}

	@Override
	protected void onTextMessage(CharBuffer message) throws IOException {
		// TODO 该方法尚未实现
		JSONObject object = JSONObject.fromObject(message.toString());
		String cmd = object.getString("cmd");
		String remoteId = null;
		if (object.has("remoteId")) {
			remoteId = object.getString("remoteId");
		}
		String roomId = null;
		if (object.has("roomId")) {
			roomId = object.getString("roomId");
		}
		System.out.println(cmd);
		if (cmd.equals("call")) {
			VipClient remote = VipUtils.getRtcClineById(remoteId);
			// 如果对方离线了
			if (remote == null) {
				return;
			}
			// 如果对方正在呼叫
			if (remote.isBusy() == true) {
				this.sendTextMessage(this, this.setMessage("error", "对方忙"));
				return;
			}
			remote.setBusy(true);
			this.sendTextMessage(remote, this.setMessage("beCall", this));

			VipClient self = VipUtils.getRtcClineById(this.id);
			self.setBusy(true);

		}

		if (cmd.equals("passCall")) {
			VipClient remote = VipUtils.getRtcClineById(remoteId);
			VipClient self = VipUtils.getRtcClineById(this.id);
			// 如果对方离线了
			if (remote == null) {
				self.setBusy(false);
				return;
			}
			// 如果对方挂断了
			if (remote.isBusy() == false) {
				self.setBusy(false);
				return;
			}
			this.sendTextMessage(remote, this.setMessage("passCall", this));
		}

		if (cmd.equals("rejectCall")) {
			VipClient remote = VipUtils.getRtcClineById(remoteId);
			VipClient self = VipUtils.getRtcClineById(this.id);
			self.setBusy(false);
			if (remote == null) {
				return;
			}
			this.sendTextMessage(remote, this.setMessage("rejectCall", this));
			remote.setBusy(false);
		}

		if (cmd.equals("offer")) {
			VipClient remote = VipUtils.getRtcClineById(remoteId);
			VipClient self = VipUtils.getRtcClineById(this.id);
			if (remote == null) {
				self.setBusy(false);
				return;
			}

			if (remote.isBusy() == false) {
				self.setBusy(false);
				return;
			}
			String data = object.getString("data");
			this.sendTextMessage(remote, this.setMessage("offer", data, this.getId()));
		}

		if (cmd.equals("answer")) {
			VipClient remote = VipUtils.getRtcClineById(remoteId);
			VipClient self = VipUtils.getRtcClineById(this.id);
			if (remote == null) {
				self.setBusy(false);
				return;
			}

			if (remote.isBusy() == false) {
				self.setBusy(false);
				return;
			}
			String data = object.getString("data");
			this.sendTextMessage(remote, this.setMessage("answer", data, this.getId()));
		}

		if (cmd.equals("candidate")) {
			VipClient remote = VipUtils.getRtcClineById(remoteId);
			VipClient self = VipUtils.getRtcClineById(this.id);
			if (remote == null) {
				self.setBusy(false);
				return;
			}
			if (remote.isBusy() == false) {
				self.setBusy(false);
				return;
			}
			String data = object.getString("data");
			this.sendTextMessage(remote, this.setMessage("candidate", data));
			return;
		}
		if (cmd.equals("hang")) {
			VipClient self = VipUtils.getRtcClineById(this.id);
			self.setBusy(false);
			VipClient remote = VipUtils.getRtcClineById(remoteId);
			if (remote == null) {
				return;
			}
			remote.setBusy(false);
			this.sendTextMessage(remote, this.setMessage("beHang", this));

		}

		if (cmd.equals("finishCall")) {
			VipClient remote = VipUtils.getRtcClineById(remoteId);
			VipClient self = VipUtils.getRtcClineById(this.id);
			if (remote == null) {
				self.setBusy(false);
				return;
			}
			this.sendTextMessage(remote, this.setMessage("finishCall", remote));
		}

		// 消息发送
		if (cmd.equals("text")) {
			String data = object.getString("data");
			String msg = "[" + this.name + "]" + data;
			broadcastTextMessage(this.setMessage("text", msg));
		}

		// ---------------------------------------------------
		if (cmd.equals("loginRoom")) {
			VipClient self = VipUtils.getRtcClineById(this.id);
			self.setBusy(true);
			VipRoom room = null;
			if (roomId == VipUtils.getPublicRoom().getId()) {
				room = VipUtils.getPublicRoom();
			} else {
				room = VipUtils.getRoomById(roomId);
			}
			if (room.getSize() == 4) {
				self.setBusy(false);
				this.sendTextMessage(this, this.setMessage("roomFull", this));
			} else {
				this.sendTextMessage(this, this.setMessage("intoRoom", room.getList()));
				room.getList().add(this);
			}

		}

	}

	// 广播消息除了自己
	private void broadcastTextMessageWithoutSelf(String message) {
		for (VipClient c : VipUtils.getListWithoutSelf(this.id)) {
			sendTextMessage(c, message);
		}
	}

	// 广播消息
	private void broadcastTextMessage(String message) {
		for (VipClient c : VipUtils.getClientList()) {
			sendTextMessage(c, message);
		}
	}

	// 发送文本消息给指定的人
	private void sendTextMessage(MessageInbound client, String message) {
		try {
			if (client == null) {
				client = this;
			}
			System.out.println(message);
			CharBuffer buffer = CharBuffer.wrap(message);
			WsOutbound outbound = client.getWsOutbound();
			outbound.writeTextMessage(buffer);
			outbound.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public void setBusy(boolean busy) {
		if (this.busy == busy) {
			return;
		}
		this.busy = busy;
		broadcastTextMessageWithoutSelf(this.setMessage("updataBusy", this));
	}

	public boolean isBusy() {
		return busy;
	}
}
