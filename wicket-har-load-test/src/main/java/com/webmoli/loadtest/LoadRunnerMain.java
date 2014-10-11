package com.webmoli.loadtest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.math.NumberUtils;

public class LoadRunnerMain {

	public static void main(String[] args) {
		if (args == null || args.length < 3) {
			System.err.println("This program require 3 arguments. <har-file-path> <nunber-of-thread> <domain-name>");
		} else {
			try {
				String harFile = args[0];
				int numberOfThread = NumberUtils.toInt(args[1]);
				String domain = args[2];

				Collection<String> extensionToIgnore = new ArrayList<String>(Arrays.asList(new String[] { "png", "css", "js", "gif", "m4v" }));
				LoadRunnerEngine engine = new LoadRunnerEngine(harFile, numberOfThread, domain, extensionToIgnore);
				engine.startTest();
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
	}
}
