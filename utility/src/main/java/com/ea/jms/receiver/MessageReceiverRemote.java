package com.ea.jms.receiver;

import java.util.Properties;

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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.jms.exception.MessageApplicationException;
import com.ea.jms.exception.MessageException;

/**
 * Abstract class as Receiver for JMS messages.
 */
public abstract class MessageReceiverRemote implements MessageListener {
	
	private static final Log log = LogFactory.getLog(MessageReceiverRemote.class);

	@Resource
	protected MessageDrivenContext ctx;

	//@Resource(mappedName = "java:/LocalJMS")
	//@Resource(mappedName = "java:/ConnectionFactory")
	//@Resource(mappedName = "java:/JmsXA")
	public final static String CONNECTION_FACTOY_DEFAULT = "jms/RemoteConnectionFactory";
	
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
			log.debug("Creating JMS connection to REPLY. EJB MessageSender: " + hashCode());
			//connection = connectionFactory.createConnection();
			Context context = getContextEnvJboss7();
			log.trace("Context for REPLY created. EJB MessageSender: " + hashCode() + ", context: "+context.getEnvironment());
			ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(CONNECTION_FACTOY_DEFAULT);
			log.trace("ConnectionFactory for REPLY created. EJB MessageSender: " + hashCode());
			
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
			log.error("Passed in destination class: " + queue.getClass().toString());
			log.error("Class loader for passed in destination: " + queue.getClass().getClassLoader().toString());
			//log.error("Class loader for HornetQDestination: " + HornetQDestination.class.getClassLoader().toString());
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
	
	protected static Context getContextEnvJboss7() throws  MessageException {
		String serverHost = "127.0.0.1";
		String serverPort = "4447";
		String serverUser = "adminapp";
		String serverPwd = "adminpwd";
		
		try {
			Properties properties = new Properties();
			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			properties.put(Context.PROVIDER_URL, "remote://" + serverHost + ":" + serverPort);
			// properties.put("jboss.naming.client.ejb.context", "true");
			properties.put(Context.SECURITY_PRINCIPAL, serverUser);
			properties.put(Context.SECURITY_CREDENTIALS, serverPwd);
			// deactivate authentication
			// properties.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT","false");
			// properties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS","false");
			Context context = new InitialContext(properties);
			return context;
		} catch (NamingException e) {
			log.error("Error creating InitialContext for server: "+serverHost+":"+serverPort, e);
			throw new  MessageException(e);
		}
	}

}
