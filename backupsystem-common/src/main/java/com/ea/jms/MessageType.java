package com.ea.jms;

/**
 * Reply Message for Synchronous messaging.
 * The caller receive ACK from the destination in case of success and ERROR in case of failure/timeout.
 */
public enum MessageType {
	ACK, ERROR;
}
