package com.webmoli.loadtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoadTestEngine {

	private int numberOfThread;

	private URL harFile;

	private String domain;

	private Collection<String> extensionToIgnore;

	public LoadTestEngine(URL harFile, int numberOfThread, String domain, Collection<String> extensionToIgnore) {
		this.harFile = harFile;
		this.numberOfThread = numberOfThread;
		this.domain = domain;
		this.extensionToIgnore = extensionToIgnore;
	}

	public void startTest() throws JSONException, IOException {
		List<LoadTestRequest> requests = getLoadTestRequest();
		for (int i = 0; i < numberOfThread; i++) {
			LoadTestThread thread = new LoadTestThread(requests);
			thread.start();
		}
	}

	public List<LoadTestRequest> getLoadTestRequest() throws JSONException, IOException {
		List<LoadTestRequest> requests = new ArrayList<LoadTestRequest>();
		JSONObject jsonObject = new JSONObject(IOUtils.toString(new BufferedReader(new InputStreamReader(harFile.openStream()))));
		JSONArray entries = jsonObject.getJSONObject("log").getJSONArray("entries");
		for (int i = 0; i < entries.length(); i++) {
			JSONObject entry = entries.getJSONObject(i);
			String url = entry.getJSONObject("request").getString("url");
			if (!url.contains(domain)) {
				continue;
			}
			URL urlObj = new URL(url);
			String fileName = urlObj.getFile();
			fileName = StringUtils.substringBefore(fileName, "?");
			String fileExtension = StringUtils.substringAfterLast(fileName, ".");
			fileExtension = StringUtils.substringBefore(fileExtension, ";");
			if (fileExtension != null && extensionToIgnore.contains(fileExtension.toLowerCase())) {
				continue;
			} else if (url.indexOf("wicket:interface") > 0 && url.endsWith("::::")) {
				continue;
			} else if (url.indexOf(domain) <= 0) {
				continue;
			}

			if (entry.getJSONObject("request").has("postData")) {
				String postData = entry.getJSONObject("request").getJSONObject("postData").getString("text");
				String mimeType = entry.getJSONObject("request").getJSONObject("postData").getString("mimeType");
				requests.add(new LoadTestRequest(url, mimeType, postData.getBytes()));
			} else {
				requests.add(new LoadTestRequest(url));
			}
		}
		return Collections.unmodifiableList(requests);
	}
}
