package com.ea.jms.sender;

import java.util.Properties;

import javax.interceptor.InvocationContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.jms.Message;
import com.ea.jms.MessageConnection;
import com.ea.jms.exception.MessageException;
import com.ea.jms.exception.NoReplyException;

/**
 * Abstract class for sending JMS messages to a single Destination. The context of the destination has to be passed with MessageConnection object through the Send method.
 */
public abstract class MessageSenderSingle2 {

	private static final Log log = LogFactory.getLog(MessageSenderSingle.class);
	// public final static String CONNECTION_FACTOY_DEFAULT = "InVmConnectionFactory";
	public final static String CONNECTION_FACTOY_DEFAULT = "jms/RemoteConnectionFactory";
	//public final static String CONNECTION_FACTOY_DEFAULT = "java:/JmsXA";

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
		Context ctx = null;
		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;

		log.info("Sending ASYNCHronous message of type: " + message.getMessageType() + ". EJB SingleMessageSender: " + hashCode());
		try {
			log.debug("Creating JNDI context server [host: " + messageConnection.getHost() + ":" + messageConnection.getPort() + "]. EJB SingleMessageSender: " + hashCode());
			ctx = getContextEnvJboss7(messageConnection);
			log.trace("JNDI context created. EJB SingleMessageSender: " + hashCode()+", context: " + ctx.getEnvironment());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS context", e);
		}

		Destination destination = null;
		try {
			log.trace("Lookup Destination: " + destinationName + ". EJB SingleMessageSender: " + hashCode());
			destination = (Destination) ctx.lookup(destinationName);
			log.trace("Destination:" + destinationName + " looked up. EJB SingleMessageSender: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS destination", e);
		}

		ConnectionFactory connectionFactory = null;
		try {
			log.trace("Lookup Connection Factory. EJB SingleMessageSender: " + hashCode());
			connectionFactory = (ConnectionFactory) ctx.lookup(CONNECTION_FACTOY_DEFAULT);
			log.trace("Connection Factory looked up. EJB SingleMessageSender: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS ConnectionFactory", e);
		}

		try {
			log.trace("Creating JMS connection. EJB SingleMessageSender: " + hashCode());
			connection = connectionFactory.createConnection();
			log.trace("JMS connection created. EJB SingleMessageSender: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS session", e);
		}

		try {
			log.trace("Creating JMS session. EJB SingleMessageSender: " + hashCode());
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			log.trace("JMS connection created. EJB SingleMessageSender: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS session", e);
		}

		try {
			log.trace("Creating JMS producer. EJB SingleMessageSender: " + hashCode());
			producer = session.createProducer(destination);
		} catch (Exception e) {
			throw new MessageException("Error creating JMS producer", e);
		}

		try {
			ObjectMessage jmsMessage = null;
			jmsMessage = session.createObjectMessage(message);
			jmsMessage.setStringProperty("messageType", message.getMessageType());
			if (!msgPersistence) {
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			}

			log.debug("Sending asynchronous message. EJB SingleMessageSender: " + hashCode());
			producer.send(jmsMessage);
			log.info("Asynchronous message of type: " + message.getMessageType() + " successfully sent. EJB SingleMessageSender: " + hashCode());
		} catch (JMSException e) {
			log.error("JMSException while sending asynchronous message of type: " + message.getMessageType() + " in SingleMessageSender: " + hashCode() + ". Exception: " + e.getMessage(), e);
			throw new MessageException("JMSException occurred while sending asynchronous message. Exception:" + e.getMessage(), e);
		} finally {
			closeJmsProducer(producer);
			closeJmsSession(session);
			closeJmsConnection(connection);
			closeInitialContext(ctx);
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
		Context ctx = null;
		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;
		Message response = null;

		log.info("Sending SYNCHronous message of type: " + message.getMessageType() + ". EJB SingleMessageSender: " + hashCode());

		try {
			log.debug("Creating JNDI context server [host: " + messageConnection.getHost() + ":" + messageConnection.getPort() + "]. EJB SingleMessageSender: " + hashCode());
			ctx = getContextEnvJboss7(messageConnection);
			log.trace("JNDI context created. EJB SingleMessageSender: " + hashCode()+", context: "+ctx.getEnvironment());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS context", e);
		}

		ConnectionFactory connectionFactory = null;
		try {
			log.trace("Lookup Connection Factory. EJB SingleMessageSender: " + hashCode());
			connectionFactory = (ConnectionFactory) ctx.lookup(CONNECTION_FACTOY_DEFAULT);
			log.trace("Connection Factory looked up. EJB SingleMessageSender: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS ConnectionFactory", e);
		}

		Destination destination = null;
		try {
			log.trace("Lookup Destination: " + destinationName + ". EJB SingleMessageSender: " + hashCode());
			destination = (Destination) ctx.lookup(destinationName);
			log.trace("Destination:" + destinationName + " looked up. EJB SingleMessageSender: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS destination", e);
		}

		try {
			log.trace("Creating JMS connection. EJB SingleMessageSender: " + hashCode());
			connection = connectionFactory.createConnection();
			log.trace("JMS connection created. EJB SingleMessageSender: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS session", e);
		}

		try {
			log.trace("Creating JMS session. EJB SingleMessageSender: " + hashCode());
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			log.trace("JMS connection created. EJB SingleMessageSender: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS session", e);
		}

		try {
			log.trace("Creating JMS producer. EJB SingleMessageSender: " + hashCode());
			producer = session.createProducer(destination);
		} catch (Exception e) {
			throw new MessageException("Error creating JMS producer", e);
		}

		try {
			Queue replyQueue = session.createTemporaryQueue(); // where will wait the reply
			ObjectMessage jmsMessage = session.createObjectMessage(message);
			jmsMessage.setStringProperty("messageType", message.getMessageType());
			jmsMessage.setJMSReplyTo(replyQueue);
			jmsMessage.setJMSExpiration(messageConnection.getReadTimeout());
			if (!msgPersistence) {
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			}
			producer.send(jmsMessage);
			connection.start();
			log.info("Synchronous message of type: " + message.getMessageType() + " successfully sent. EJB SingleMessageSender: " + hashCode());

			MessageConsumer consumer = session.createConsumer(replyQueue); // create consumer, waiting the reply
			ObjectMessage reply = (ObjectMessage) consumer.receive(messageConnection.getReadTimeout());
			if (reply != null) {
				log.debug("Received reply for synchronous message of type: " + message.getMessageType() + ". EJB SingleMessageSender: " + hashCode());
				response = (Message) reply.getObject();
			} else {
				log.error("Reply from the Destination '" + destinationName + "' is NULL! MessageConnection: " + messageConnection + ", EJB SingleMessageSender: " + hashCode());
				throw new NoReplyException("The Destination '" + destinationName + "' couldn't reply before timeout.");
			}
			log.info("Reply correctly received from Destination for synchronous message of type: " + message.getMessageType() + ". Reply: " + response.getMessageType() + ". EJB SingleMessageSender: "
					+ hashCode());
			return response;
		} catch (JMSException e) {
			throw new MessageException("Exception occurred while sending synchronous message: " + e.getMessage(), e);
		} finally {
			closeJmsProducer(producer);
			closeJmsSession(session);
			closeJmsConnection(connection);
			closeInitialContext(ctx);
		}
	}

	protected static Context getContextEnvJboss7(MessageConnection messageConnection) throws NamingException {
		try {
			Properties properties = new Properties();
			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming"); //when .properties file is used
			//properties.put(Context.PROVIDER_URL, "http-remoting://" + messageConnection.getHost() + ":" + messageConnection.getPort());
			properties.put("jboss.naming.client.ejb.context", "true");
			//properties.put(Context.SECURITY_PRINCIPAL, messageConnection.getUsername());
			//properties.put(Context.SECURITY_CREDENTIALS, messageConnection.getPassword());
			Context context = new InitialContext(properties);
			return context;
		} catch (NamingException e) {
			log.error("Error creating InitialContext with data: " + messageConnection, e);
			throw e;
		} catch (Exception e) {
			log.error("General Error creating InitialContext with data: " + messageConnection, e);
			throw new NamingException("General Error creating InitialContext with data: " + messageConnection+", Exception: "+ e.getMessage());
		}
	}

	private void closeJmsProducer(MessageProducer producer) {
		if (producer != null) {
			try {
				producer.close();
			} catch (Exception e) {
				log.warn("JMS producer cannot be closed. EJB SingleMessageSender: " + hashCode(), e);
			}
		}
	}

	private void closeJmsSession(Session session) {
		if (session != null) {
			try {
				session.close();
			} catch (Exception e) {
				log.warn("JMS session cannot be closed. EJB SingleMessageSender: " + hashCode(), e);
			}
		}
	}

	private void closeJmsConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				log.warn("JMS connection cannot be closed. EJB SingleMessageSender: " + hashCode(), e);
			}
		}
	}

	private void closeInitialContext(Context ctx) {
		if (ctx != null) {
			try {
				ctx.close();
			} catch (Exception e) {
				log.warn("JNDI context cannot be closed. EJB SingleMessageSender: " + hashCode(), e);
			}
		}
	}

}
