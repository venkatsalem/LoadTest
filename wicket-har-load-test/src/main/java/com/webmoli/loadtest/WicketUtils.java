package com.webmoli.loadtest;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.meterware.httpunit.WebResponse;

public class WicketUtils {

	public static String replaceUrlWithWicketInterface(String url, WebResponse response) throws IOException {
		String uri = extractUri(url);
		String formName = findFormName(uri);
		String newUri = extractWicketInterface(response.getText(), formName);
		return url.replace(uri, newUri);
	}

	public static String findFormName(String uri) {
		return StringUtils.substringBetween(StringUtils.substringAfter(uri, "=:"), ":", "::");
	}

	public static String extractUri(String url) {
		return StringUtils.substringAfter(url, "?");
	}

	public static String extractWicketInterface(String responseText, String formName) {
		Pattern p = Pattern.compile(String.format(".*\\?(.+:%s:.+?)\"", formName));
		Matcher m = p.matcher(responseText);
		String extract = null;
		if (m.find()) {
			extract = m.group(1);
		}
		return extract;
	}

	public static void main(String[] args) {
		System.out
				.println(extractWicketInterface(
						"<form id=\"sectionIntroFormbf\" method=\"post\" action=\"?wicket:interface=:10:sectionIntroForm::IFormSubmitListener::\"><div style=\"width:0px;height:0px;position:absolute;left:-100px;top:-100px;overflow:hidden\"><input type=\"hidden\" name=\"sectionIntroFormbf_hf_0\" id=\"sectionIntroFormbf_hf_0\" /></div>",
						"sectionIntroForm"));
	}
}
