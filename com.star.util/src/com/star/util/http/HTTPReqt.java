package com.star.util.http;


public class HTTPReqt {
	
	private Long startDate = System.currentTimeMillis();
	
	private String url;
	
	private String path;
	
	private String params;
	
	public Long getStartDate() {
		return startDate;
	}
	@Override
	public String toString() {
		return url+" "+path+" "+params+" ";
	}

	public String getUrl() {
		return url;
	}

	public String getPath() {
		return path;
	}

	public String getParams() {
		return params;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setParams(String params) {
		this.params = params;
	}

}
