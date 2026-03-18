package com.volodymyr.ppm.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserAuthData {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String ip;
	private String userAgent;
	private String sessionId;
	
	public UserAuthData() {}
	
	public Long getId() {
		return id;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public String getUserAgent() {
		return userAgent;
	}
	
	public String getIp() {
		return ip;
	}
		
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
