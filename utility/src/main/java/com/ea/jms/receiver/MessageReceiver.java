package com.ea.jms.receiver;

import javax.annotation.Resource;
import javax.ejb.MessageDrivenContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.jms.exception.MessageApplicationException;
import com.ea.jms.exception.MessageException;

/**
 * Abstract class as Receiver for JMS messages.
 */
public abstract class MessageReceiver implements MessageListener {
	
	private static final Log log = LogFactory.getLog(MessageReceiver.class);

	@Resource
	protected MessageDrivenContext ctx;

	//@Resource(mappedName = "java:/LocalJMS")
	//@Resource(mappedName = "java:/ConnectionFactory")
	//@Resource(mappedName = "java:/JmsXA")
	//public final static String CONNECTION_FACTOY_DEFAULT = "jms/RemoteConnectionFactory";
	
	@Resource(mappedName = "java:/ConnectionFactory")
	protected ConnectionFactory connectionFactory;

	public void onMessage(Message jmsMessage) {
		com.ea.jms.Message messageRequest = null;
		com.ea.jms.Message messageResponse = null;
		try {
			if (jmsMessage instanceof ObjectMessage) {
				messageRequest = (com.ea.jms.Message) ((ObjectMessage) jmsMessage).getObject();
				log.info("New JMS message from IP: " + messageRequest.getIpAddress()+", messageType: " + messageRequest.getMessageType()+", MessageReceiver: " + hashCode());
				
				messageResponse = process(messageRequest);
				
				log.debug("Check if the message is synch/asynch. MessageReceiver: " + hashCode());
				if (jmsMessage.getJMSReplyTo() != null) {
					log.debug("Is synch message, need to reply back to TMP: "+jmsMessage.getJMSReplyTo()+". MessageReceiver: " + hashCode());
					if (messageResponse != null) {
						Queue responseQueue = (Queue) jmsMessage.getJMSReplyTo();
						sendReply(responseQueue, messageResponse);
						log.info("Reply for synch message with messageType: " + messageRequest.getMessageType() + " sent correctly to the sender. MessageReceiver: " + hashCode());
					} else {
						log.error("MessageResponse cannot be NULL in a synch mode scenario. MessageReceiver: " + hashCode());
						throw new MessageException("MessageResponse cannot be NULL in a synch mode scenario");
					}
				}
				log.info("Message with messageType: " + messageRequest.getMessageType() + " correctly elaborated. MessageReceiver: " + hashCode());
			}
		} catch (MessageApplicationException e) {
			log.error("MessageApplicationException in MDB MessageReceiver: " + hashCode() +", NO NEED for rollback.", e);
		} 
		catch (MessageException e) {
			log.error("MessageException in MDB MessageReceiver: " + hashCode() +", ROLLBACK!", e);
			ctx.setRollbackOnly();
		} catch (Exception e) {
			log.error("Exception in MDB MessageReceiver: " + hashCode() +", ROLLBACK!", e);
			ctx.setRollbackOnly();
		} 
	}

	/**
	 * Abstract method there extended classes will implement the business logic
	 */
	protected abstract com.ea.jms.Message process(com.ea.jms.Message messageRequest);

	/**
	 * Send a Reply message to the given queue.
	 */
	private void sendReply(Queue queue, com.ea.jms.Message message) throws MessageException {
		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;

		try {
			log.trace("Creating JMS connection to REPLY. EJB MessageSender: " + hashCode());
			connection = connectionFactory.createConnection();
			log.trace("JMS connection created. EJB MessageSender: " + hashCode());	
		} catch (Exception e) {
			throw new MessageException("Error creating JMS REPLY connection", e);
		} 

		try {
			log.trace("Creating JMS session to REPLY. EJB MessageSender: " + hashCode());
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			log.trace("JMS session created. EJB MessageSender: " + hashCode() + ", jmsSession: "+session);	
		} catch (Exception e) {
			throw new MessageException("Error creating JMS REPLY session", e);
		} 
		
		try {
			log.trace("Creating JMS producer to REPLY. EJB MessageSender: " + hashCode());
			producer = session.createProducer(queue);
			log.trace("JMS producer created. EJB MessageSender: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS REPLY producer", e);
		} 
		
		try{
			log.trace("Creating JMS ObjectMessage to REPLY. EJB MessageSender: " + hashCode());
			ObjectMessage jmsMessage = session.createObjectMessage(message);
			log.trace("Sending reply message, MessageReceiver: " + hashCode());
			producer.send(jmsMessage);
		} catch (Exception e) {
			//log.error("An error occurred while sending reply message to TMP queue: "+queue, e);
			throw new MessageException("Exception sending REPLY message", e);
		} finally {
			if (producer != null) {
				try {
					producer.close();
				} catch (Exception e) {
					log.warn("Cannot close producer, MessageReceiver: " + hashCode(), e);
				}
			}
			if (session != null) {
				try {
					session.close();
				} catch (Exception e) {
					log.warn("Cannot close session, MessageReceiver: " + hashCode(), e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					log.warn("Cannot close connection, MessageReceiver: " + hashCode(), e);
				}
			}
		}
	}

}
