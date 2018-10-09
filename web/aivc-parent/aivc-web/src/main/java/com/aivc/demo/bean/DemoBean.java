package com.aivc.demo.bean;

public class DemoBean {
	

	

	@Override
	public String toString() {
		return "DemoBean [id=" + id + ", url=" + url + ", date=" + date + "]";
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	String id;
	String url;
	String date;

}
