package com.webmoli.loadtest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class HarLoadTest {

	private static final String domain = "demo.di.com";

	static {
		System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
	}

	public static void main(String[] args) throws Exception {
		// XTrustProvider.install();
		MyTrustManager.install();
		HtmlUnitClient client = new HtmlUnitClient();
		JSONObject jsonObject = new JSONObject(IOUtils.toString(getFileReader()));
		JSONArray entries = jsonObject.getJSONObject("log").getJSONArray("entries");
		WebResponse response = null;
		for (int i = 0; i < entries.length(); i++) {
			JSONObject entry = entries.getJSONObject(i);
			String url = entry.getJSONObject("request").getString("url");
			if (url.endsWith(".css") || url.indexOf("google") > 0 || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".gif")
					|| url.endsWith(".m4v") || url.indexOf(".js?") > 0) {
				continue;
			} else if (url.indexOf("wicket:interface") > 0 && url.endsWith("::::")) {
				continue;
			} else if (url.indexOf(domain) <= 0) {
				continue;
			}

			System.out.println(url);
			if (url.indexOf("wicket:interface") > 0) {
				try {
					url = WicketUtils.replaceUrlWithWicketInterface(url, response);
				} catch (Exception exp) {
					System.out.println("Response: " + response.getText());
					throw exp;
				}
			}
			WebRequest request;
			if (entry.getJSONObject("request").has("postData")) {
				String postData = entry.getJSONObject("request").getJSONObject("postData").getString("text");
				String mimeType = entry.getJSONObject("request").getJSONObject("postData").getString("mimeType");
				System.out.println(postData);
				request = new PostMethodWebRequest(url, new ByteArrayInputStream(postData.getBytes()), mimeType);
			} else {
				request = new GetMethodWebRequest(url);
			}
			response = client.getResource(request);
			if (response.getResponseCode() == 302) {
				url = response.getHeaderField("location");
				System.out.println("Redirect: " + url);
				response = client.get(url);
			} else if (response.getResponseCode() != 200) {
				System.out.println(response.getResponseMessage());
				throw new Exception("Error occured");
			}
			System.out.println("Response: " + response.getText());
		}
	}

	public static Reader getFileReader() {
		return new BufferedReader(new InputStreamReader(HarLoadTest.class.getResourceAsStream("/demo.di.com_1.har")));
	}
}
