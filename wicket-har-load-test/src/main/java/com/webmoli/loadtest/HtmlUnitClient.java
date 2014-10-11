package com.webmoli.loadtest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class HtmlUnitClient {

	private WebConversation conversation;

	private Map<String, String> cookies;

	public HtmlUnitClient() {
		cookies = new HashMap<String, String>();
		conversation = new WebConversation();
		conversation.setProxyServer("localhost", 8888);
	}

	public WebResponse get(String url) throws IOException {
		return getResource(new GetMethodWebRequest(url));
	}

	public WebResponse post(String url, String postData, String mimeType)
			throws IOException {
		return getResource(new PostMethodWebRequest(url,
				new ByteArrayInputStream(postData.getBytes()), mimeType));
	}

	public WebResponse getResource(WebRequest request) throws IOException {
		request.setHeaderField("Cookie", getCookieString());
		WebResponse response = conversation.getResource(request);
		processCookie(response);
		return response;
	}

	private String getCookieString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : cookies.entrySet()) {
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
			sb.append("; ");
		}
		return sb.toString();
	}

	private void processCookie(WebResponse response) {
		String cookieString = response.getHeaderField("Set-Cookie");
		String cookies[] = StringUtils.split(cookieString, ";");
		if (cookies == null || cookies.length == 0) {
			return;
		}
		for (String cookie : cookies) {
			String keyValue[] = StringUtils.split(cookie, "=");
			this.cookies.put(keyValue[0], keyValue[1]);
		}
	}
}
