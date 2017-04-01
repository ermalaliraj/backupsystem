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
import com.ea.util.StringUtils;

/**
 *
 */
public abstract class MessageSenderDynamic {

	private static final Log log = LogFactory.getLog(MessageSenderDynamic.class);
	public final static String CONNECTION_FACTOY_DEFAULT = "jms/RemoteConnectionFactory"; //if not specified in messageConnection

	protected String destinationName;

	abstract public void setDestinationName(InvocationContext ctx);

	protected void sendAsynchMessage(MessageConnection messageConnection, Message message) throws MessageException {
		sendAsynchMessage(messageConnection, message, true);
	}

	protected void sendAsynchMessage(MessageConnection messageConnection, Message message, boolean msgPersistence) throws MessageException {
		Context ctx = null;
		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;

		log.info("Sending ASYNCHronous message of type: " + message.getMessageType() + ". EJB SingleMessageDynamic: " + hashCode());
		try {
			log.debug("Creating JNDI context server [host: " + messageConnection.getHost() + ":" + messageConnection.getPort() + "]. EJB SingleMessageDynamic: " + hashCode());
			ctx = getContextEnvJboss7(messageConnection);
			log.trace("JNDI context created. Context: "+ctx.getEnvironment()+". EJB SingleMessageDynamic: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS context", e);
		}

		Destination destination = null;
		try {
			log.trace("Lookup Destination: " + destinationName + ". EJB SingleMessageDynamic: " + hashCode());
			destination = (Destination) ctx.lookup(destinationName);
			log.trace("Destination looked up: " + destination + ". EJB SingleMessageDynamic: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS destination", e);
		}

		ConnectionFactory connectionFactory = null;
		String cfName = CONNECTION_FACTOY_DEFAULT;
		try {
			if(!StringUtils.isEmptyString(messageConnection.getConnectionFactory())){
				cfName = messageConnection.getConnectionFactory();
			}
			log.trace("Lookup Connection Factory: "+cfName+". EJB SingleMessageDynamic: " + hashCode());
			connectionFactory = (ConnectionFactory) ctx.lookup(CONNECTION_FACTOY_DEFAULT);	
			log.trace("Connection Factory looked up: "+connectionFactory+". EJB SingleMessageDynamic: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS ConnectionFactory", e);
		}

		try {
			log.trace("Creating JMS connection. EJB SingleMessageDynamic: " + hashCode());
			connection = connectionFactory.createConnection();
			log.trace("JMS connection created. EJB SingleMessageDynamic: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS session", e);
		}

		try {
			log.trace("Creating JMS session. EJB SingleMessageDynamic: " + hashCode());
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			log.trace("JMS connection created. EJB SingleMessageDynamic: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS session", e);
		}

		try {
			log.trace("Creating JMS producer. EJB SingleMessageDynamic: " + hashCode());
			producer = session.createProducer(destination);
		} catch (Exception e) {
			throw new MessageException("Error creating JMS producer", e);
		}

		try {
			log.trace("Sending message of type: "+message.getMessageType()+". EJB MessageSenderServer: " + hashCode());
			ObjectMessage jmsMessage = null;
			jmsMessage = session.createObjectMessage(message);
			jmsMessage.setStringProperty("messageType", message.getMessageType());
			if (!msgPersistence) {
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			}

			log.debug("Sending asynchronous message. EJB SingleMessageDynamic: " + hashCode());
			producer.send(jmsMessage);
			log.info("Asynchronous message of type: " + message.getMessageType() + " successfully sent. EJB SingleMessageDynamic: " + hashCode());
		} catch (JMSException e) {
			log.error("JMSException while sending asynchronous message of type: " + message.getMessageType() + " in SingleMessageDynamic: " + hashCode() + ". Exception: " + e.getMessage(), e);
			throw new MessageException("JMSException occurred while sending asynchronous message. Exception:" + e.getMessage(), e);
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

		log.info("Sending SYNCHronous message of type: " + message.getMessageType() + ". EJB SingleMessageDynamic: " + hashCode());

		try {
			log.debug("Creating JNDI context server [host: " + messageConnection.getHost() + ":" + messageConnection.getPort() + "]. EJB SingleMessageDynamic: " + hashCode());
			ctx = getContextEnvJboss7(messageConnection);
			log.trace("JNDI context created. Context: "+ctx.getEnvironment()+". EJB SingleMessageDynamic: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS context", e);
		}

		ConnectionFactory connectionFactory = null;
		String cfName = CONNECTION_FACTOY_DEFAULT;
		try {
			if(!StringUtils.isEmptyString(messageConnection.getConnectionFactory())){
				cfName = messageConnection.getConnectionFactory();
			}
			log.trace("Lookup Connection Factory: "+cfName+". EJB SingleMessageDynamic: " + hashCode());
			connectionFactory = (ConnectionFactory) ctx.lookup(cfName);
			log.trace("Connection Factory looked up: "+connectionFactory+". EJB SingleMessageDynamic: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS ConnectionFactory", e);
		}

		Destination destination = null;
		try {
			log.trace("Lookup Destination: " + destinationName + ". EJB SingleMessageDynamic: " + hashCode());
			destination = (Destination) ctx.lookup(destinationName);
			log.trace("Destination looked up: " + destination + ". EJB SingleMessageDynamic: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS destination", e);
		}

		try {
			log.trace("Creating JMS connection. EJB SingleMessageDynamic: " + hashCode());
			connection = connectionFactory.createConnection();
			log.trace("JMS connection created. EJB SingleMessageDynamic: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS session", e);
		}

		try {
			log.trace("Creating JMS session. EJB SingleMessageDynamic: " + hashCode());
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			log.trace("JMS connection created. EJB SingleMessageDynamic: " + hashCode());
		} catch (Exception e) {
			throw new MessageException("Error creating JMS session", e);
		}

		try {
			log.trace("Creating JMS producer. EJB SingleMessageDynamic: " + hashCode());
			producer = session.createProducer(destination);
		} catch (Exception e) {
			throw new MessageException("Error creating JMS producer", e);
		}

		try {
			Queue replyQueue = session.createTemporaryQueue(); // where will wait the reply
			
			log.trace("Sending message of type: "+message.getMessageType()+". EJB SingleMessageDynamic: " + hashCode());
			ObjectMessage jmsMessage = session.createObjectMessage(message);
			jmsMessage.setStringProperty("messageType", message.getMessageType());
			jmsMessage.setJMSReplyTo(replyQueue);
			jmsMessage.setJMSExpiration(messageConnection.getTimeout());
			if (!msgPersistence) {
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			}
			producer.send(jmsMessage);
			connection.start();
			log.info("Synchronous message of type: " + message.getMessageType() + " successfully sent. EJB SingleMessageDynamic: " + hashCode());

			MessageConsumer consumer = session.createConsumer(replyQueue); // create consumer, waiting the reply
			ObjectMessage reply = (ObjectMessage) consumer.receive(messageConnection.getTimeout());
			if (reply != null) {
				log.debug("Received reply for synchronous message of type: " + message.getMessageType() + ". EJB SingleMessageDynamic: " + hashCode());
				response = (Message) reply.getObject();
			} else {
				log.error("Reply from the Destination '" + destinationName + "' is NULL! MessageConnection: " + messageConnection + ", EJB SingleMessageDynamic: " + hashCode());
				throw new NoReplyException("The Destination '" + destinationName + "' couldn't reply before timeout.");
			}
			log.info("Reply correctly received from Destination for synchronous message of type: " + message.getMessageType() + ". Reply: " + response.getMessageType() + ". EJB SingleMessageDynamic: "
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

	private static Context getContextEnvJboss7(MessageConnection messageConnection) throws NamingException {
		try {
			messageConnection.setHost("bs-ru");
			messageConnection.setPort(4447);
			
			String initialContext = "org.jboss.naming.remote.client.InitialContextFactory";
			String urlPkgPrefixes = "org.jboss.ejb.client.naming";
			String protocol = "remote://";
			if(!StringUtils.isEmptyString(messageConnection.getInitialContext())){
				initialContext =  messageConnection.getInitialContext();
			}
			if(!StringUtils.isEmptyString(messageConnection.getUrlPkgPrefixes())){
				urlPkgPrefixes = messageConnection.getUrlPkgPrefixes();;
			}
			if(!StringUtils.isEmptyString(messageConnection.getProtocol())){
				protocol =  messageConnection.getProtocol();
			}
			String host =  messageConnection.getHost();
			long port =  messageConnection.getPort();
			String username = null;
			String password = null;
			if (!StringUtils.isEmptyString(messageConnection.getUsername())) {
				username =  messageConnection.getUsername();
				password =  messageConnection.getPassword();
			}
			log.info("Lookup context dynamic mode with data [initialContext:"+initialContext+", urlPkgPrefixes: "+urlPkgPrefixes+", url:"+
					protocol + host + ":" + port+", username: "+username+", password: "+password+"]");
			
			Properties properties = new Properties();
			properties.put(Context.INITIAL_CONTEXT_FACTORY, initialContext);
			properties.put(Context.URL_PKG_PREFIXES, urlPkgPrefixes);
			properties.put(Context.PROVIDER_URL, protocol + host + ":" + port);
			properties.put("jboss.naming.client.ejb.context", "true");
			if (username != null) {
				properties.put(Context.SECURITY_PRINCIPAL, username);
				properties.put(Context.SECURITY_CREDENTIALS, password);
			}
			Context context = new InitialContext(properties);
			return context;
		} catch (NamingException e) {
			log.error("Error creating InitialContext with data: " + messageConnection, e);
			throw e;
		}
	}

	private void closeJmsProducer(MessageProducer producer) {
		if (producer != null) {
			try {
				producer.close();
			} catch (Exception e) {
				log.warn("JMS producer cannot be closed. EJB SingleMessageDynamic: " + hashCode(), e);
			}
		}
	}

	private void closeJmsSession(Session session) {
		if (session != null) {
			try {
				session.close();
			} catch (Exception e) {
				log.warn("JMS session cannot be closed. EJB SingleMessageDynamic: " + hashCode(), e);
			}
		}
	}

	private void closeJmsConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				log.warn("JMS connection cannot be closed. EJB SingleMessageDynamic: " + hashCode(), e);
			}
		}
	}

	private void closeInitialContext(Context ctx) {
		if (ctx != null) {
			try {
				ctx.close();
			} catch (Exception e) {
				log.warn("JNDI context cannot be closed. EJB SingleMessageDynamic: " + hashCode(), e);
			}
		}
	}

}
