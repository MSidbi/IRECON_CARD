package com.recon.model;

import javax.servlet.http.HttpServletRequest;

public class SessionModel {

	public static HttpServletRequest req;

	public HttpServletRequest getReq() {
		return req;
	}

	public void setReq(HttpServletRequest req) {
		this.req = req;
	}
	
}
