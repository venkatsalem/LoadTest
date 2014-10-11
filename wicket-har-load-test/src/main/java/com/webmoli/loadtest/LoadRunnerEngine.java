package com.webmoli.loadtest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

public class LoadRunnerEngine {

	private int numberOfThread;

	private String harFile;

	private String domain;

	private Collection<String> extensionToIgnore;

	public LoadRunnerEngine(String harFilePath, int numberOfThread, String domain, Collection<String> extensionToIgnore) {
		this.harFile = harFilePath;
		this.numberOfThread = numberOfThread;
		this.domain = domain;
		this.extensionToIgnore = extensionToIgnore;
	}

	public void startTest() throws JSONException, IOException {
		List<LoadRunnerRequest> requests = getLoadTestRequest();
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < numberOfThread; i++) {
			LoadRunnerThread thread = new LoadRunnerThread(requests);
			thread.start();
			threads.add(thread);
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public List<LoadRunnerRequest> getLoadTestRequest() throws JSONException, IOException {
		List<LoadRunnerRequest> requests = new ArrayList<LoadRunnerRequest>();
		JSONObject jsonObject = new JSONObject(IOUtils.toString(new BufferedReader(new FileReader(harFile))));
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
				requests.add(new LoadRunnerRequest(url, mimeType, postData.getBytes()));
			} else {
				requests.add(new LoadRunnerRequest(url));
			}
		}
		return Collections.unmodifiableList(requests);
	}
}
