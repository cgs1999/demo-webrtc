/**
 * @(#)ChatHelper.java 2013-6-27 Copyright 2013 it.kedacom.com, Inc. All rights reserved.
 */
package com.kedacom.webrtc.whiteboard;

import java.util.ArrayList;
import java.util.List;

/**
 * 电子 白板帮助类
 * @author chengesheng@kedacom.com
 * @date 2013-9-27 下午5:44:54
 * @note ChatHelper.java
 */
public class WhiteboardHelper {

	private static List<WhiteboardClient> clientList = new ArrayList<WhiteboardClient>(0);

	public static List<WhiteboardClient> getClientList() {
		return clientList;
	}
}
