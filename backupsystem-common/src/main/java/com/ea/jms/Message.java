package com.ea.jms;

import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = -29882385389130331L;

	private String messageType = null;
	private Object messageObject = null;
	private String ipAddress = null;
	
	public Message(String messageType) {
		this.messageType = messageType;
	}

	public Message(String messageType, Object messageObject) {
		this.messageType = messageType;
		this.messageObject = messageObject;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Object getMessageObject() {
		return messageObject;
	}

	public void setMessageObject(Object messageObject) {
		this.messageObject = messageObject;
	}

	public String toString() {
		final String SEPARATOR = ", ";//, or \t\n
		
		StringBuffer sb = new StringBuffer("Message [");
		sb.append("messageType: " + messageType + SEPARATOR);
		sb.append("ipAddress: " + ipAddress + SEPARATOR);
		sb.append("messageObject present?: " + ((messageObject!=null) ? true : false));
	
		sb.append("]");
		return sb.toString();
	}

}
