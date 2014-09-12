package com.kedacom.webrtc.util;

import javax.servlet.http.HttpServletRequest;

public class IpHelper {

	/**
	 * 获取客户端IP地址
	 * @param request
	 * @return
	 */
	public static String getRemortIp(HttpServletRequest request) {
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
	private static boolean isNotIp(String ip) {
		return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip) || "127.0.0.1".equals(ip);
	}
}
