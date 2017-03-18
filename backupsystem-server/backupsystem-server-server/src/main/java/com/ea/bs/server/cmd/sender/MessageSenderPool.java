package com.ea.bs.server.cmd.sender;

import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import javax.jms.DeliveryMode;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.jms.Message;
import com.ea.jms.MessageConnection;
import com.ea.jms.exception.MessageException;
import com.ea.jms.exception.NoReplyException;

/**
 * JMS 2.0 
 * Using @Inject allows the container to inject the JMSContext instance, and the container assumes full responsibility for it’s lifecycle.
 */

public abstract class MessageSenderPool {

	private static final Log log = LogFactory.getLog(MessageSenderPool.class);

	@Inject
	@JMSConnectionFactory("java:/jms/remoteCF")
	private JMSContext context;
	
	protected String destinationName;
	
	abstract public void setDestinationName(InvocationContext ctx);

	/**
	 * Send an Asynchronous message in persistent mode.    
	 */
	protected void sendAsynchMessage(MessageConnection messageConnection, Message message) throws MessageException {
		sendAsynchMessage(messageConnection, message, true);
	}

	/**
	 * Send an Asynchronous message
	 */
	protected void sendAsynchMessage(MessageConnection messageConnection, Message message, boolean msgPersistence) throws MessageException {
		JMSProducer producer = null;

		log.info("Sending ASYNCHronous message of type: "+message.getMessageType()+". EJB SingleMessageSender: " + hashCode());
		try {
			Queue destination = context.createQueue(destinationName);
			producer = context.createProducer();
			ObjectMessage jmsMessage = context.createObjectMessage(message);
			jmsMessage.setStringProperty("messageType", message.getMessageType());
			if (!msgPersistence){
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			}
			producer.send(destination, jmsMessage);
			log.info("Asynchronous message of type: "+message.getMessageType()+" successfully sent. EJB SingleMessageSender: " + hashCode());
		}  catch (JMSException e) {
			log.error("JMSException while sending asynchronous message of type: "+message.getMessageType()+" in SingleMessageSender: " + hashCode()+". Exception: "+ e.getMessage(), e);
			throw new MessageException("JMSException occurred while sending asynchronous message. Exception:"+ e.getMessage(), e);
		}
	}

	/**
	 * Send a Synchronous message in persistent mode
	 */
	protected Message sendSynchMessage(MessageConnection messageConnection, Message message) throws MessageException {
		return sendSynchMessage(messageConnection, message, true);
	}

	/**
	 * Send a Synchronous message
	 */
	protected Message sendSynchMessage(MessageConnection messageConnection, Message message, boolean msgPersistence) throws MessageException {
		JMSProducer jmsProducer = null;
		Message response = null;
		
		log.info("Sending SYNCHronous message of type: "+message.getMessageType()+". EJB SingleMessageSender: " + hashCode());
		try {
			log.debug("Sending synchronous message. EJB SingleMessageSender: " + hashCode());
			Queue destination = context.createQueue(destinationName);
			log.trace("Queue created. Destination: " + destination + ". EJB SingleMessageSender: " + hashCode());
			jmsProducer = context.createProducer();
			log.trace("Producer created. EJB SingleMessageSender: " + hashCode());
			Queue replyQueue = context.createTemporaryQueue();
			log.trace("Temporary queue created: "+replyQueue+". EJB SingleMessageSender: " + hashCode());
			
			ObjectMessage jmsMessage = context.createObjectMessage(message);
			jmsMessage.setStringProperty("messageType", message.getMessageType());
			jmsMessage.setJMSReplyTo(replyQueue);
			jmsMessage.setJMSExpiration(messageConnection.getReadTimeout());
			if (!msgPersistence){
				jmsProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			}

			log.debug("Sending jmsMessage: "+jmsMessage+". EJB SingleMessageSender: " + hashCode());
			jmsProducer.send(destination, jmsMessage);
			log.info("Synchronous message of type: "+message.getMessageType()+" successfully sent. EJB SingleMessageSender: " + hashCode());
			
			JMSConsumer consumer = context.createConsumer(replyQueue); //create consumer, waiting the reply
			ObjectMessage reply = (ObjectMessage) consumer.receive(messageConnection.getReadTimeout());
			if (reply != null) {
				log.debug("Received reply for synchronous message of type: "+message.getMessageType()+". EJB SingleMessageSender: " + hashCode());
				response = (Message) reply.getObject();
			} else {
				log.error("Reply from the Destination '"+destinationName+"' messageType: "+message.getMessageType()+" is NULL! MessageConnection: "+messageConnection+", EJB SingleMessageSender: " + hashCode());
				throw new NoReplyException("The Destination '"+destinationName+"' couldn't reply before timeout.");
			}
			log.info("Reply correctly received from Destination for synchronous message of type: "+message.getMessageType()+". Reply: "+response.getMessageType()+". EJB SingleMessageSender: " + hashCode());
			return response;
		}
		catch (JMSException e) {
			throw new MessageException("Exception occurred while sending synchronous message: "+e.getMessage(), e);
		} 
	}
}
