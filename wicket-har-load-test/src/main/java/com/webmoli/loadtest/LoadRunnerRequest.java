package com.webmoli.loadtest;

public class LoadRunnerRequest {

	private String url;

	private String mimeType;

	private byte[] postData;

	public LoadRunnerRequest(String url) {
		this.url = url;
	}

	public LoadRunnerRequest(String url, String mimeType, byte[] postData) {
		this.url = url;
		this.postData = postData;
		this.mimeType = mimeType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public byte[] getPostData() {
		return postData;
	}

	public void setPostData(byte[] postData) {
		this.postData = postData;
	}

}
