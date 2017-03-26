package com.ea.cs.gui.cmd;

import java.util.Properties;

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
//import javax.jms.Connection;
//import javax.jms.ConnectionFactory;
//import javax.jms.DeliveryMode;
//import javax.jms.MessageConsumer;
//import javax.jms.MessageProducer;
//import javax.jms.Queue;
//import javax.jms.Session;
//import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.ea.bs.protocol.cmd.message.CommandMessageType;

public class JMSTestMessageStandalone {

	private static final Logger log = Logger.getLogger(JMSTestMessageStandalone.class);
	protected String queueName = null;
	protected Context ctx = null;
	protected Queue queue = null;

	public static void main(String[] args) {
		JMSTestMessageStandalone app = new JMSTestMessageStandalone();
		app.sendQueue();
	}

	public void sendQueue() {
		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;
		try {
			//String host = "10.151.4.162";
			//String host = "192.168.1.8";
			String host = "bs-ru";
			String port = "4447";
			
			Properties properties = new Properties();
			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			//properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			//properties.put(Context.URL_PKG_PREFIXES, "org.jboss.naming");
			properties.put(Context.PROVIDER_URL, "remote://" + host + ":" + port);
			properties.put("jboss.naming.client.ejb.context", "true");
			properties.put(Context.SECURITY_PRINCIPAL, "testuser");//adminapp
			properties.put(Context.SECURITY_CREDENTIALS, "testpwd");//adminpwd
			Context ctx = new InitialContext(properties);

			final ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
			log.debug("Got ConnectionFactory: " + connectionFactory);
		
			String queueName = "jms/queue/remoteCMD";
			//com.ea.jms.Message cmdMsg = new com.ea.jms.Message(CommandMessageType.ServerMessageType.GET_SAMPLE_FILE.name());
			com.ea.jms.Message cmdMsg = new com.ea.jms.Message(CommandMessageType.ServerMessageType.PING_ASYNCH.name());
//			com.ea.jms.Message cmdMsg = new com.ea.jms.Message(CommandMessageType.ServerMessageType.PING_SYNCH.name());
//			com.ea.jms.Message cmdMsg = new com.ea.jms.Message(CommandMessageType.ServerMessageType.GET_STATUS.name());
			
			// String lookupObj = "/jms/queue/test";
			log.debug("Looking up to " + queueName);
	
			Destination queue = (Destination) ctx.lookup(queueName);
			log.debug("Got remote queue: " + queue);
	
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue replyQueue = session.createTemporaryQueue(); // where will wait the reply
			producer = session.createProducer(queue);
			ObjectMessage jmsMessage= session.createObjectMessage(cmdMsg);
			jmsMessage.setJMSReplyTo(replyQueue);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			jmsMessage.setJMSExpiration(5000);

			log.debug("Sending message...");
			producer.send(jmsMessage);
			connection.start();
			log.debug("Message Sent.");
			

			MessageConsumer consumer = session.createConsumer(replyQueue); 
			ObjectMessage reply = (ObjectMessage) consumer.receive(5000);
			log.debug("Received reply for synchronous message "+reply);
			if (reply != null) {
				com.ea.jms.Message response = (com.ea.jms.Message) reply.getObject();
				log.debug("Message from server received: " + response);
			} else {
				log.debug("NO reply message from server");
			}
	
		} catch (NamingException e) {
			log.error("NamingException", e);
		} catch (Exception e) {
			log.error("General Exception", e);
		} finally{
			closeConnection(connection);
			closeSession(session);
			closeProducer(producer);
		}
	}
	
	private void closeProducer(MessageProducer producer) {
		try {
			if (producer != null)
				producer.close();
		} catch (JMSException jmse) {
			log.error("Could not close producer " + producer + ": ", jmse);
		}
	}

	private void closeSession(Session session) {
		try {
			if (session != null)
				session.close();
		} catch (JMSException jmse) {
			log.error("Could not close connection " + session + ": ", jmse);
		}
	}

	private static void closeConnection(final Connection con) {
		try {
			if (con != null)
				con.close();
		} catch (JMSException jmse) {
			log.error("Could not close connection " + con + ": ", jmse);
		}
	}
}
