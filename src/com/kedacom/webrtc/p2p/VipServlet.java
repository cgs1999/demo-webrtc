/**
 * @(#)VipServlet.java 2013-10-12 Copyright 2013 it.kedacom.com, Inc. All rights
 *                     reserved.
 */

package com.kedacom.webrtc.p2p;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

/**
 * (用一句话描述类的主要功能)
 * @author kongchun
 * @date 2013-10-12
 */
@WebServlet(urlPatterns = "/p2p.do")
public class VipServlet extends WebSocketServlet {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/**
	 * @see org.apache.catalina.websocket.WebSocketServlet#createWebSocketInbound(java.lang.String,
	 *      javax.servlet.http.HttpServletRequest)
	 */

	@Override
	protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest request) {
		// TODO 该方法尚未实现
		return new VipClient(request);
	}

}
