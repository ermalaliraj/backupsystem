package com.ea.jms.sender;

import javax.annotation.Resource;
import javax.interceptor.InvocationContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.jms.Message;
import com.ea.jms.MessageConnection;
import com.ea.jms.exception.MessageException;
import com.ea.jms.exception.NoReplyException;

/**
 * Abstract class for sending JMS messages from RU to Server
 */
public abstract class MessageSenderServer {

	private static final Log log = LogFactory.getLog(MessageSenderServer.class);

	@Resource(mappedName = "java:/ServerConnectionFactory")
	private ConnectionFactory connectionFactory;
	
	protected String destinationName;
	
	abstract public void setDestinationName(InvocationContext ctx);

	protected void sendAsynchMessage(Message message) throws MessageException {
		sendAsynchMessage(message, true);
	}
	protected void sendAsynchMessage(Message message, boolean msgPersistence) throws MessageException {
		Context ctx = null;
		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;

		log.info("Sending ASYNCHronous message of type: "+message.getMessageType()+". EJB MessageSenderServer: " + hashCode());
		try {
			log.trace("Creating JMS connection. EJB MessageSenderServer: " + hashCode());
			connection = connectionFactory.createConnection();
			log.trace("JMS connection created. EJB MessageSenderServer: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS connection", e);
		} 

		try {
			log.trace("Creating JMS session. EJB MessageSender: " + hashCode());
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			log.trace("JMS session created. EJB MessageSender: " + hashCode());	
		} catch (Exception e) {
			throw new MessageException("Error creating JMS session", e);
		} 
		
		Queue destination = null;
		try {
			log.trace("Creating JMS queue '"+destinationName+"'. EJB MessageSender: " + hashCode());
			destination = session.createQueue(destinationName);
			log.trace("JMS queue created. EJB MessageSender: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS queue '"+destinationName+"'", e);
		}
		
		try {
			log.trace("Creating JMS producer. EJB MessageSender: " + hashCode());
			producer = session.createProducer(destination);
			log.trace("JMS producer created. EJB MessageSender: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS producer", e);
		}	

		try {
			log.trace("Sending message of type: "+message.getMessageType()+". EJB MessageSenderServer: " + hashCode());
			ObjectMessage jmsMessage = null;
			jmsMessage = session.createObjectMessage(message);
			jmsMessage.setStringProperty("messageType", message.getMessageType());
			if (!msgPersistence){
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			}
			producer.send(jmsMessage);
			log.info("Asynchronous message of type: "+message.getMessageType()+", persistenceMode: "+msgPersistence+", successfully sent. EJB MessageSenderServer: " + hashCode());
		}  catch (JMSException e) {
			log.error("JMSException while sending asynchronous message of type: "+message.getMessageType()+" in MessageSenderServer: " + hashCode()+". Exception: "+ e.getMessage(), e);
			throw new MessageException("JMSException occurred while sending asynchronous message. Exception:"+ e.getMessage(), e);
		} finally {
			closeJmsProducer(producer);
			closeJmsSession(session);
			closeJmsConnection(connection);
			closeInitialContext(ctx);
		}
	}
	
	protected Message sendSynchMessage(MessageConnection messageConnection, Message message) throws MessageException {
		return sendSynchMessage(messageConnection, message, true);
	}
	protected Message sendSynchMessage(MessageConnection messageConnection, Message message, boolean msgPersistence) throws MessageException {
		Context ctx = null;
		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;
		Message response = null;
		
		log.info("Sending SYNCHronous message of type: "+message.getMessageType()+". EJB MessageSenderServer: " + hashCode());
		try {
			log.trace("Creating JMS connection. EJB MessageSenderServer: " + hashCode());
			connection = connectionFactory.createConnection();
			log.trace("JMS connection created. EJB MessageSenderServer: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS connection", e);
		} 
		
		try {
			log.trace("Creating JMS session. EJB MessageSender: " + hashCode());
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			log.trace("JMS session created. EJB MessageSender: " + hashCode());	
		} catch (Exception e) {
			throw new MessageException("Error creating JMS session", e);
		} 
		
		Queue destination = null;
		try {
			log.trace("Creating JMS queue '"+destinationName+"'. EJB MessageSender: " + hashCode());
			destination = session.createQueue(destinationName);
			log.trace("JMS queue created. EJB MessageSender: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS queue '"+destinationName+"'", e);
		}

		try {
			log.trace("Creating JMS producer. EJB MessageSender: " + hashCode());
			producer = session.createProducer(destination);
			log.trace("JMS producer created. EJB MessageSender: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS producer", e);
		}	
		
		try {
			Queue replyQueue = session.createTemporaryQueue(); // where will wait the reply
			
			log.trace("Sending message of type: "+message.getMessageType()+". EJB MessageSenderServer: " + hashCode());
			ObjectMessage jmsMessage = session.createObjectMessage(message);
			jmsMessage.setStringProperty("messageType", message.getMessageType());
			jmsMessage.setJMSReplyTo(replyQueue);
			jmsMessage.setJMSExpiration(messageConnection.getTimeout());
			if (!msgPersistence){
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			}
			producer.send(jmsMessage);
			connection.start();
			log.info("Synchronous message of type: "+message.getMessageType()+", persistenceMode: "+msgPersistence+", successfully sent. EJB MessageSenderServer: " + hashCode());
			
			MessageConsumer consumer = session.createConsumer(replyQueue); //create consumer, waiting the reply
			ObjectMessage reply = (ObjectMessage) consumer.receive(messageConnection.getTimeout());
			if (reply != null) {
				log.debug("Received reply for synchronous message of type: "+message.getMessageType()+". EJB MessageSenderServer: " + hashCode());
				response = (Message) reply.getObject();
			} else {
				log.error("Reply from the Destination '"+destinationName+"' messageType: "+message.getMessageType()+" is NULL! MessageConnection: "+messageConnection+", EJB MessageSenderServer: " + hashCode());
				throw new NoReplyException("The Destination '"+destinationName+"' couldn't reply before timeout.");
			}
			log.info("Reply correctly received from Destination for synchronous message of type: "+message.getMessageType()+". Reply: "+response.getMessageType()+". EJB MessageSenderServer: " + hashCode());
			return response;
		}
		catch (JMSException e) {
			throw new MessageException("Exception occurred while sending synchronous message: "+e.getMessage(), e);
		} 
		finally {
			closeJmsProducer(producer);
			closeJmsSession(session);
			closeJmsConnection(connection);
			closeInitialContext(ctx);
		}
	}

	private void closeJmsProducer(MessageProducer producer) {
		if (producer != null) {
			try {
				producer.close();
			} catch (Exception e) {
				log.warn("JMS producer cannot be closed. EJB MessageSenderServer: " + hashCode(), e);
			}
		}
	}
	
	private void closeJmsSession(Session session) {
		if (session != null) {
			try {
				session.close();
			} catch (Exception e) {
				log.warn("JMS session cannot be closed. EJB MessageSenderServer: " + hashCode(), e);
			}
		}
	}
	
	private void closeJmsConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				log.warn("JMS connection cannot be closed. EJB MessageSenderServer: " + hashCode(), e);
			}
		}
	}
	
	private void closeInitialContext(Context ctx) {
		if (ctx != null) {
			try {
				ctx.close();
			} catch (Exception e) {
				log.warn("JNDI context cannot be closed. EJB MessageSenderServer: " + hashCode(), e);
			}
		}
	}

}
