package com.wai.whiteley.http;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpApi {
	private static final String LOG = HttpApi.class.getSimpleName();
	private static CookieStore cookieStore = null;
	
	/*
	 * Get Request
	 */
	public static String sendGetRequest(String url) {
		int status = -1;
		String resultStr = null;
		
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			
			status = response.getStatusLine().getStatusCode();
			String body = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
			
			Log.v(LOG, "###### STATUS: "+status);
			Log.v(LOG, "###### BODY: "+body);
			
			if (status == 200) {
				String strResponse = body;
				Log.v(LOG, "###### RESPONSE: "+strResponse);
				return strResponse.trim();
			
			} else {
				switch (status) {
				case 400:
					resultStr = "Bad Request";
					break;
				case 401:
					resultStr = "Access denied";
					break;
				case 403:
					resultStr = "Request is forbidden";
					break;
				case 404:
					resultStr = "Url not found";
					break;
				case 405:
					resultStr = "Method Not Allowed";
					break;
				case 500:
					resultStr = "Internal Server Error";
					break;
				}
			}
			
		} catch (Exception e) {
			resultStr = e.getMessage();
			if (resultStr == null) {
				if (e instanceof java.net.SocketTimeoutException) {
					resultStr = "It has occured SocketTimeoutException";
				}
			}
			
			Log.v(LOG, "####### Send GET request error: " + resultStr);
			status = -1;
		}
		
		return resultStr;
	}
	
	
	/*
	 * Post Request
	 */
	public static String sendPostRequest(String url, String data) {
		int status = -1;
		String resultStr = null;

		try
		{
			StringEntity se = new StringEntity(data);
			
			/* Sets the HTTP post request */
			HttpPost request = new HttpPost(url);
			request.getParams().setParameter("http.protocol.expect-continue", false);
			request.getParams().setParameter("http.connection.timeout", ServerConfig.CONNECTION_TIMEOUT * 1000);
			request.getParams().setParameter("http.socket.timeout", ServerConfig.CONNECTION_TIMEOUT * 1000);
			request.setHeader("Content-Type", "application/json");
			request.setEntity(se);
			
			DefaultHttpClient client = new DefaultHttpClient();
			
			/* Gets the HTTP response*/
			HttpResponse response = client.execute(request);
			
			status = response.getStatusLine().getStatusCode();
			resultStr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
			
			Log.v(LOG, "###### STATUS: "+status);
			Log.v(LOG, "###### BODY: "+resultStr);
			
			/* if the status code is 200, response successfully */
			if (status == 200) {
				Log.v(LOG, "###### RESPONSE SUCCESS #####");
				
			} else {
				switch (status) {
				case 400:
					resultStr = "Bad Reqeust";
					break;
				case 401:
					resultStr = "Access denied";
					break;
				case 403:
					resultStr = "Request is forbidden";
					break;
				case 404:
					resultStr = "Url not found";
					break;
				case 405:
					resultStr = "Method Not Allowed";
					break;
				case 406:
					resultStr = "Not Acceptable Response";
					break;
				case 500:
					resultStr = "Internal Server Error";
					break;
				}
			}
		
		} catch (Exception e) {
			resultStr = e.getMessage();
			if (resultStr == null) {
				if (e instanceof java.net.SocketTimeoutException) {
					resultStr = "It has occured SocketTimeoutException";
				}
			}
			
			Log.v(LOG, "####### Send POST request error: " + resultStr);
			status = -1;
		}
		
		return resultStr;
	}
	
	
	public static String sendPostRequest(String serverUrl, HttpParams params) {

		String resultStr = null;

		HttpPost httpRequest = new HttpPost(serverUrl);
		httpRequest.getParams().setParameter("http.protocol.expect-continue", false);
		httpRequest.getParams().setParameter("http.connection.timeout", ServerConfig.CONNECTION_TIMEOUT * 1000);
		httpRequest.getParams().setParameter("http.socket.timeout", ServerConfig.CONNECTION_TIMEOUT * 1000);
		int status = -1;

		try {
			/** Makes an HTTP request request */
			if (params != null) {
				httpRequest.setEntity(new UrlEncodedFormEntity(params.getParams(), HTTP.UTF_8));
			}

			/** Create an HTTP client */
			DefaultHttpClient httpClient = new DefaultHttpClient();

			/** Set Cookie information */
			if (cookieStore != null) {
				httpClient.setCookieStore(cookieStore);
			}

			/** Gets the HTTP response response */
			HttpResponse httpresponse = httpClient.execute(httpRequest);

			status = httpresponse.getStatusLine().getStatusCode();
			String body = EntityUtils.toString(httpresponse.getEntity(), HTTP.UTF_8);

			/** If the status code 200 response successfully */
			Log.v(LOG, "" + status);
			Log.v(LOG, "" + body);
			if (status == 200) {
				/** Remove the response string */
				String strResponse = body;

				cookieStore = httpClient.getCookieStore();
				return strResponse.trim();
			} else {
				switch (status) {
				case 400:
					resultStr = "Bad Request.";
					break;
				case 401:
					resultStr = "Access denied.";
					break;
				case 403:
					resultStr = "Request is forbidden.";
					break;
				case 404:
					resultStr = "Url not found.";
					break;
				case 405:
					resultStr = "Method Not Allowed.";
					break;
				case 500:
					resultStr = "Internal Server Error.";
					break;
				}
			}
		} catch (Exception e) {
			resultStr = e.getMessage();
			if (resultStr == null) {
				if (e instanceof java.net.SocketTimeoutException) {
					resultStr = "It has occured SoketTimeoutException.";
				}
			}
			Log.v(LOG, "send request error");
			status = -1;
		}

		return resultStr;
	}
}
