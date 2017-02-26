package com.ea.bs.protocol.files.message;

public class FileMessageType {

	/**
	 * Messages sent from RU to Server
	 */
	public enum RUMessageType {
		ACK, ERROR, SEND_FILE
	};

	/**
	 * Messages sent from Server to RU
	 */
	public enum ServerMessageType {
		ACK, ERROR
	};
	
}
