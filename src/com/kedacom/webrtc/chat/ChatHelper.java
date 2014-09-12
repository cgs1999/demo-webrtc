/**
 * @(#)ChatHelper.java 2013-6-27 Copyright 2013 it.kedacom.com, Inc. All rights reserved.
 */
package com.kedacom.webrtc.chat;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天帮助类
 * @author chengesheng@kedacom.com
 * @date 2013-9-27 下午5:44:54
 * @note ChatHelper.java
 */
public class ChatHelper {

	private static List<ChatClient> clientList = new ArrayList<ChatClient>(0);

	public static List<ChatClient> getClientList() {
		return clientList;
	}
}
