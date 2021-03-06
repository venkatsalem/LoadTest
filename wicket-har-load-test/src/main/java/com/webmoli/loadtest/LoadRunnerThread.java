package com.webmoli.loadtest;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class LoadRunnerThread extends Thread {

	private List<LoadRunnerRequest> requests;

	public LoadRunnerThread(List<LoadRunnerRequest> requests) {
		this.requests = requests;
	}

	public void run() {
		try {
			HtmlUnitClient client = new HtmlUnitClient();
			WebResponse oldHttpResponse = null;
			for (LoadRunnerRequest loadTestRequest : requests) {
				String url = loadTestRequest.getUrl();
				if (url.indexOf("wicket:interface") > 0) {
					try {
						url = WicketUtils.replaceUrlWithWicketInterface(url, oldHttpResponse);
					} catch (Exception exp) {
						System.err.println("Response: " + oldHttpResponse.getText());
						throw exp;
					}
				}
				WebRequest request;
				if (loadTestRequest.getPostData() != null) {
					request = new PostMethodWebRequest(url, new ByteArrayInputStream(loadTestRequest.getPostData()), loadTestRequest.getMimeType());
				} else {
					request = new GetMethodWebRequest(url);
				}
				long startTime = System.currentTimeMillis();
				WebResponse response = client.getResource(request);
				long endTime = System.currentTimeMillis();
				System.out.printf("%d = %s", (endTime - startTime), url);
				System.out.println();
				if (response.getResponseCode() == 302) {
					url = response.getHeaderField("location");
					// System.out.println("Redirect: " + url);
					startTime = System.currentTimeMillis();
					response = client.get(url);
					endTime = System.currentTimeMillis();
					System.out.printf("%d = %s", (endTime - startTime), url);
					System.out.println();
				} else if (response.getResponseCode() != 200) {
					System.out.println(response.getResponseMessage());
					throw new Exception("Error occured");
				}
				if (StringUtils.containsIgnoreCase(response.getContentType(), "html")) {
					oldHttpResponse = response;
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	};
}
