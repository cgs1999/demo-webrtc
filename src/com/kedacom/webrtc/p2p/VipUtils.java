/**
 * @(#)ChatHelper.java 2013-10-8 Copyright 2013 it.kedacom.com, Inc. All rights
 *                     reserved.
 */

package com.kedacom.webrtc.p2p;

import java.util.ArrayList;
import java.util.List;

public class VipUtils {

	private static List<VipClient> clientList = new ArrayList<VipClient>(0);
	private static List<VipRoom> roomList = new ArrayList<VipRoom>(0);
	private static VipRoom publicRoom = null;

	public static VipRoom getPublicRoom() {
		if (publicRoom == null) {
			publicRoom = new VipRoom("公开聊天室");
		}
		return publicRoom;
	}

	public static List<VipClient> getClientList() {
		return clientList;
	}

	public static VipClient getRtcClineByName(String name) {
		for (VipClient c : clientList) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	public static VipClient getRtcClineById(String id) {
		for (VipClient c : clientList) {
			if (c.getId().equals(id)) {
				return c;
			}
		}
		return null;
	}

	public static List<VipClient> getListWithoutSelf(String id) {
		List<VipClient> mylist = new ArrayList<VipClient>();
		for (VipClient c : clientList) {
			if (!c.getId().equals(id)) {
				mylist.add(c);
			}
		}
		return mylist;
	}

	public static VipRoom getRoomById(String id) {
		for (VipRoom c : roomList) {
			if (c.getId().equals(id)) {
				return c;
			}
		}
		return null;
	}

}
