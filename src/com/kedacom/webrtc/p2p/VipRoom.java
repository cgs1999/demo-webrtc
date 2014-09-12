/**
 * @(#)VipRoom.java 2013-10-15 Copyright 2013 it.kedacom.com, Inc. All rights
 *                  reserved.
 */

package com.kedacom.webrtc.p2p;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * (用一句话描述类的主要功能)
 * @author kongchun
 * @date 2013-10-15
 */

public class VipRoom {

	public VipRoom(String name) {
		super();
		this.id = UUID.randomUUID().toString();
		this.name = name;
	}

	private String id;
	private String name;
	private List<VipClient> list = new ArrayList<VipClient>(0);

	public void add(VipClient client) {
		this.list.add(client);
	}

	public void remove(VipClient client) {
		this.list.remove(client);
	}

	// 获取房间大小
	public int getSize() {
		return this.list.size();
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

	public List<VipClient> getList() {
		return list;
	}

	public void setList(List<VipClient> list) {
		this.list = list;
	}

}
