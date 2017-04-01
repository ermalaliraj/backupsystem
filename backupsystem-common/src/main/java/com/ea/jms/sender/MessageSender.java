package com.ea.jms.sender;

import java.util.Properties;

import javax.annotation.Resource;
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
 * Abstract class for sending JMS messages.
 * The context of the destination can be configured once and used many times to send messages to the same destination.
 */
public abstract class MessageSender {

	private static final Log log = LogFactory.getLog(MessageSender.class);
	
//	@Resource(mappedName = "java:/RemoteConnectionFactory")
	@Resource(mappedName = "java:/JmsXA")
	private ConnectionFactory connectionFactory;
	protected MessageConnection messageConnection = null;
	protected String destinationName = null;
	private Destination destination = null;

	abstract public void setDestinationName(InvocationContext ctx);
	
	private void init() throws MessageException {
		Context ctx = null;
		log.info("Initializing EJB MessageSender: " + hashCode());
//		try {
//			log.debug("Creating JNDI context server [host: "+messageConnection.getHost()+":"+messageConnection.getPort()+"],  Destination: " + destinationName+". EJB MessageSender: " + hashCode());
//			//ctx = getContextEnvJboss7(messageConnection);
//			log.trace("Lookup Destination: " + destinationName + ". EJB SingleMessageSender: " + hashCode());
//			destination = (Destination) ctx.lookup(destinationName);
//			log.trace("Destination:" + destinationName + " looked up. EJB MessageSender: " + hashCode());
//		} catch (NamingException e) {
//			log.error("NamingException while initializing MessageSender--> " + hashCode()+". Exception: "+ e.getMessage(), e);
//			throw new MessageException("NamingException occurred while sending asynchronous message. Exception:"+ e.getMessage(), e);
//		} finally {
//			closeInitialContext(ctx);
//		} 
//		log.info("Initialization of EJB MessageSender: " + hashCode() + " correctly occurred");
	}

	/**
	 * Send an Asynchronous message in persistent mode
	 */
	protected void sendAsynchMessage(Message message) throws MessageException {
		sendAsynchMessage(message, true);
	}

	/**
	 * Send an Asynchronous message
	 */
	protected void sendAsynchMessage(Message message, boolean msgPersistence) throws MessageException {
		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;

		if (destination == null){
			init();
		}
		
		log.info("Sending asynchronous message. EJB MessageSender: " + hashCode());
		try {
			log.trace("Creating JMS connection. EJB MessageSender: " + hashCode());
			String CONNECTION_FACTOY_DEFAULT = "jms/RemoteConnectionFactory";
		//	Context ctx = getContextEnvJboss7(messageConnection);
		//	ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup(CONNECTION_FACTOY_DEFAULT);
			connection = connectionFactory.createConnection();
			log.trace("JMS connection created. EJB MessageSender: " + hashCode());
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
		
		try{
			ObjectMessage jmsMessage = null;
			jmsMessage = session.createObjectMessage(message);
			jmsMessage.setStringProperty("messageType", message.getMessageType());
			if (!msgPersistence){
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			}
			producer.send(jmsMessage);
			connection.start();
			log.info("Asynchronous message successfully sent. EJB MessageSender: " + hashCode());
		} catch (JMSException jmse) {
			throw new MessageException("Error occurred while sending asynchronous message, Exception:  " + jmse.getMessage(), jmse);
		} 
//		catch (NamingException e) {
//			log.error("sssssssssssssssssssss:", e);
//		} 
		finally {
			closeJmsProducer(producer);
			closeJmsSession(session);
			closeJmsConnection(connection);
		}
	}

	/**
	 * Send an Synchronous message
	 */
	protected Message sendSynchMessage(Message message) throws MessageException {
		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;
		MessageConsumer consumer = null;
		Message response = null;

		if (destination == null){
			init();
		}
		
		log.info("Sending Synchronous message. EJB MessageSender: " + hashCode() + " ...");
		try {
			log.trace("Creating JMS connection. EJB MessageSender: " + hashCode());
			//ConnectionFactory connectionFactory = null;
			connection = connectionFactory.createConnection();

			log.trace("Creating session and producer... EJB MessageSender: " + hashCode());
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(destinationName);
			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			Queue replyQueue = session.createTemporaryQueue();
			consumer = session.createConsumer(replyQueue);

			ObjectMessage jmsMessage = session.createObjectMessage(message);
			jmsMessage.setJMSReplyTo(replyQueue);
			jmsMessage.setJMSExpiration(messageConnection.getTimeout());
			jmsMessage.setStringProperty("messageType", message.getMessageType());
			producer.send(jmsMessage);
			connection.start();
			log.info("Synchronous message successfully sent. EJB MessageSender: " + hashCode());
			
			ObjectMessage reply = (ObjectMessage) consumer.receive(messageConnection.getTimeout());
			if (reply != null) {
				log.trace("Reply received for synchronous message. EJB MessageSender: " + hashCode());
				response = (Message) reply.getObject();
			} else {
				log.error("Reply from the Destination is NULL!!! EJB MessageSender: " + hashCode());
				throw new NoReplyException("The Destination couldn't reply before timeout");
			}
			
			log.info("Reply correctly received from Destination for synchronous message. EJB MessageSender: " + hashCode());
			return response;
		} catch (JMSException e) {
			log.error("JMSException while sending synchronous message in MessageSender--> " + hashCode()+". Exception: "+ e.getMessage(), e);
			throw new MessageException("JMSException occurred while sending synchronous message. Exception:"+ e.getMessage(), e);
		} finally {
			closeJmsConsumer(consumer);
			closeJmsProducer(producer);
			closeJmsSession(session);
			closeJmsConnection(connection);
			log.debug("JMS connection closure terminated. EJB MessageSender: " + hashCode());
		}
	}
	
//	protected static Context getContextEnvJboss7(MessageConnection messageConnection) throws NamingException {
//		try {
//			Properties properties = new Properties();
//			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
//			properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
//			properties.put(Context.PROVIDER_URL, "remote://" + messageConnection.getHost() + ":" + messageConnection.getPort());
//			properties.put(Context.SECURITY_PRINCIPAL, messageConnection.getUsername());
//			properties.put(Context.SECURITY_CREDENTIALS, messageConnection.getPassword());
//			// deactivate authentication
//			// properties.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT","false");
//			// properties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS","false");
//			Context context = new InitialContext(properties);
//			return context;
//		} catch (NamingException e) {
//			log.error("Error creating InitialContext with data: " + messageConnection, e);
//			throw e;
//		}
//	}

	private void closeJmsConsumer(MessageConsumer consumer) {
		if (consumer != null) {
			try {
				consumer.close();
			} catch (Exception e) {
				log.warn("JMS consumer cannot be closed. EJB MessageSender: " + hashCode(), e);
			}
		}
	}

	private void closeJmsProducer(MessageProducer producer) {
		if (producer != null) {
			try {
				producer.close();
			} catch (Exception e) {
				log.warn("JMS producer cannot be closed. EJB MessageSender: " + hashCode(), e);
			}
		}
	}
	
	private void closeJmsSession(Session session) {
		if (session != null) {
			try {
				session.close();
			} catch (Exception e) {
				log.warn("JMS session cannot be closed. EJB MessageSender: " + hashCode(), e);
			}
		}
	}
	
	private void closeJmsConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				log.warn("JMS connection cannot be closed. EJB MessageSender: " + hashCode(), e);
			}
		}
	}
	
	private void closeInitialContext(Context ctx) {
		if (ctx != null) {
			try {
				ctx.close();
			} catch (Exception e) {
				log.warn("JNDI context cannot be closed. EJB MessageSender: " + hashCode(), e);
			}
		}
	}
}
