/**
 * @(#)MyWebSocketServlet.java 2013-6-27 Copyright 2013 it.kedacom.com, Inc. All rights reserved.
 */
package com.kedacom.webrtc.whiteboard;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

/**
 * 白板客户端WebSocketServlet
 * @author chengesheng@kedacom.com
 * @date 2013-6-27 下午5:44:31
 * @note ChatClientServlet.java
 */
@WebServlet(urlPatterns = "/whiteboard.do")
public class WhiteboardServlet extends WebSocketServlet {

	/** serialVersionUID */
	private static final long serialVersionUID = -225065782828683005L;

	/**
	 * @see org.apache.catalina.websocket.WebSocketServlet#createWebSocketInbound(java.lang.String,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest request) {
		return new WhiteboardClient(request);
	}
}
