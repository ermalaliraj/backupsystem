package com.ea.jms;

import java.io.Serializable;

/**
 * Connection descriptor
 */
public class MessageConnection implements Serializable {

	private static final long serialVersionUID = -8095580354941201344L;

	private String initialContext = null;
	private String urlPkgPrefixes = null;
	private String protocol = null;
	private String host = null;
	private long port = -1;
	private long timeout = -1;
//	private long readTimeout = -1;
	
	private String connectionFactory = null;
	private String destinationName = null;
	private String username = null;
	private String password = null;

	

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public long getPort() {
		return port;
	}

	public void setPort(long port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getInitialContext() {
		return initialContext;
	}

	public void setInitialContext(String initialContext) {
		this.initialContext = initialContext;
	}

	public String getUrlPkgPrefixes() {
		return urlPkgPrefixes;
	}

	public void setUrlPkgPrefixes(String urlPkgPrefixes) {
		this.urlPkgPrefixes = urlPkgPrefixes;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(String connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String toString() {
		final String SEPARATOR = ", ";//, or \t\n
		
		StringBuffer sb = new StringBuffer("MessageConnection [");
		sb.append("initialContext: " + initialContext + SEPARATOR);
		sb.append("urlPkgPrefixes: " + urlPkgPrefixes + SEPARATOR);
		sb.append("protocol: " + protocol + SEPARATOR);
		sb.append("host: " + host + SEPARATOR);
		sb.append("port: " + port + SEPARATOR);
		sb.append("timeout: " + timeout + SEPARATOR);
		//sb.append("readTimeout: " + readTimeout + SEPARATOR);
		sb.append("connectionFactory: " + connectionFactory + SEPARATOR);
		sb.append("destinationName: " + destinationName + SEPARATOR);
		sb.append("username: " + username + SEPARATOR);
		sb.append("password: " + password + SEPARATOR);
		sb.append("]");
		return sb.toString();
	}


}


