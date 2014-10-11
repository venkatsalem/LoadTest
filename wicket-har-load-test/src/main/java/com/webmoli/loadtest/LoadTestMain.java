package com.webmoli.loadtest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.json.JSONException;

public class LoadTestMain {

	public static void main(String[] args) throws JSONException, IOException {
		Collection<String> extensionToIgnore = new ArrayList<String>(Arrays.asList(new String[] { "png", "css", "js", "gif", "m4v" }));
		String harFile = "/demo.di.com_1.har";
		int numberOfThread = 2;
		String domain = "demo.di.com";
		LoadTestEngine engine = new LoadTestEngine(LoadTestMain.class.getResource(harFile), numberOfThread, domain, extensionToIgnore);
		engine.startTest();
	}
}
