package com.wai.whiteley.http;

public class ServerConfig {
	
	// publish server
	public static String HOST_LOCAL_URL = "10.70.5.4";
	// publish server
	public static String HOST_URL = "glow.weareignition.com/whiteley";//"drakecircus.ignitionlondon.com";

	public static final int CONNECTION_TIMEOUT = 60;
	public static final String BUSY = "-1";
	public static final String NO_INTERNET = "-2";
	
	public static String getServerUrl() {
		return "http://" + HOST_URL + "/json_api";
//		return "http://" + HOST_LOCAL_URL + "/drake/json_api";
	}
	
	public static final String RESPONSE_STATUS_OK = "ok";
}
