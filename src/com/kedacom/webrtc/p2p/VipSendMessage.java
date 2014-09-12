/**
 * @(#)VipSendMessage.java 2013-10-12 Copyright 2013 it.kedacom.com, Inc. All
 *                         rights reserved.
 */

package com.kedacom.webrtc.p2p;

import java.io.Serializable;

import net.sf.json.JSONObject;

/**
 * (用一句话描述类的主要功能)
 * @author kongchun
 * @date 2013-10-12
 */

public class VipSendMessage<T> implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 23131231231L;

	public VipSendMessage(String type, T data) {
		super();
		this.type = type;
		this.data = data;
	}

	public VipSendMessage(String type, T data, String remoteId) {
		super();
		this.type = type;
		this.remoteId = remoteId;
		this.data = data;
	}

	private String type;
	private String remoteId;
	private T data;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return JSONObject.fromObject(this).toString();
	}

	public String getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId;
	}
}
