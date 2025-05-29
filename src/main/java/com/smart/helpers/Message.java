package com.smart.helpers;

public class Message {
	public String content;
	public String type;
	public Message(String content, String type) {
		super();
		this.content = content;
		this.type = type;
	}
	public Message() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
