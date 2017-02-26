package com.ea.bs.protocol.cmd.message;

public class CommandMessageType {

	/**
	 * RU to Server
	 */
	public enum RUMessageType {
		ACK, ERROR, SEND_STATUS, SEND_SAMPLE_FILE
	};

	/**
	 * Server to RU
	 */
	public enum ServerMessageType {
		ACK, ERROR, START_SERVICE, STOP_SERVICE, GET_SAMPLE_FILE, GET_STATUS
	};
}
