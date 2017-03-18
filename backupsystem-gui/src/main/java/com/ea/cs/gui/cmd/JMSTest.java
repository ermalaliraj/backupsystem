package com.ea.cs.gui.cmd;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
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
import javax.naming.NameClassPair;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.hornetq.jms.client.HornetQObjectMessage;


public class JMSTest {

	private static final Logger log = Logger.getLogger(JMSTest.class);
	protected String queueName = null;
	protected Context ctx = null;
	protected Queue queue = null;
	private static String jndiPrefix = null;

	public static void sendMessage(final Context ctx, final ConnectionFactory connectionFactory, final String queueName, final String text) {

		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;

		try {
			String lookupObj = jndiPrefix + queueName;
			// String lookupObj = "/jms/queue/test";
			log.debug("Looking up to " + lookupObj);

			Destination queue = (Destination) ctx.lookup(lookupObj);
			log.debug("Got remote object: " + queue);
			//ConnectionFactory connectionFactory = getConnectionFactory(ctx);
			log.debug("Got ConnectionFactory: " + connectionFactory);

			connection = connectionFactory.createConnection();
			log.debug("Creating session and producer...");
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			producer = session.createProducer(queue);
			ObjectMessage msg = session.createObjectMessage(text);
			msg.setStringProperty("messageType", "PROVA_ERMAL");
			//TextMessage msg = session.createTextMessage();
			//msg.setText(text);
			
			// jmsMessage.setStringProperty("messageType", message.getMessageType());
			log.debug("Sending message...");
			producer.send(msg);
			connection.start();
			log.debug("Message Sent.");

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			//drainQueue(ctx, queueName, 1);

			// try {
			// Thread.sleep(Integer.MAX_VALUE);
			// } catch (InterruptedException e) {}
		} catch (Exception e) {
			// System.out.println("Could not send message on queue: '" +
			// queueName + "' - "+e.getMessage());
			log.error("Error: ", e);
		} finally {
			// closeJmsConnection(con);
		}
	}

	public static boolean drainQueue(final Context ctx, final String queueName, int amount) {
		// int msgsOnQueue = getQueueSize(getMBeanServer(ic), queueName);
		int msgsOnQueue = 1;
		int msgsDrained = 0;
		if (msgsOnQueue <= 0) {
			log.info("Queue " + queueName + " is already empty");
		} else {
			Connection con = null;
			try {
				// Queue queue = (Queue)ctx.lookup("queue/" + queueName);
				Queue queue = (Queue) ctx.lookup(jndiPrefix + queueName);
				ConnectionFactory cf = getConnectionFactoryJboss7(ctx);
				con = cf.createConnection();
				Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
				MessageConsumer consumer = session.createConsumer(queue);
				con.start();
				boolean receivedMsg = true;
				//while (msgsDrained < amount && msgsDrained < msgsOnQueue && receivedMsg) {
				while (msgsDrained < amount) {
					Message msg = consumer.receive(1500);
					log.debug("Message from Queue: " + msg);
					if (msg != null) {
						System.out.println("msg: "+msg);
						if(msg instanceof org.hornetq.jms.client.HornetQObjectMessage){
							org.hornetq.jms.client.HornetQObjectMessage msgHornt = (HornetQObjectMessage) msg;
							log.debug("Message (Java object) from Queue: " + msgHornt.getObject());							
						} else {
							TextMessage text = (TextMessage)msg;
							log.debug("Message TEXTfrom Queue: " + text.getText());
						}

					}
					//receivedMsg = (msg != null);
					msgsDrained++;
				}
 			} catch (Exception e) {
				log.error("Clearing queue failed.", e);
			} finally {
				closeConnection(con);
			}
		}
		return (msgsDrained == msgsOnQueue);
	}

	private static ConnectionFactory getConnectionFactoryJboss4(final Context ctx) {
		ConnectionFactory cf = null;
		try {
			cf = (ConnectionFactory)ctx.lookup("SecureConnectionFactory");
		} catch (NamingException e) {
			System.out.println("Could not retrieve ConnectionFactory: " + e.getMessage());
		}
		return cf;
	}
	private static ConnectionFactory getConnectionFactoryJboss7(final Context ctx) {
		ConnectionFactory cf = null;
		try {
			//jboss7
			cf = (ConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
			//cf = (ConnectionFactory) ctx.lookup("java:/JmsXA");
			log.debug("CF created: "+cf);
		} catch (NamingException e) {
			System.out.println("Could not retrieve ConnectionFactory: " + e.getMessage());
		}
		return cf;
	}
	
	private static Context getContextEnvJboss7() throws NamingException {
		String host = "192.168.1.2";
		//String host = "127.0.0.1";
		String port = "4447";  //remoting
		//String port = "5445"; //messaging
		try {
			Properties properties = new Properties();
			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			properties.put(Context.PROVIDER_URL, "remote://" + host + ":" + port);
			//properties.put("jboss.naming.client.ejb.context", "true");
			properties.put(Context.SECURITY_PRINCIPAL, "testuser");//adminapp
			properties.put(Context.SECURITY_CREDENTIALS, "testpwd");//adminpwd
			// deactivate authentication
//			 properties.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT","false");
//			 properties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS","false");
			Context context = new InitialContext(properties);
			log.debug("Context created: "+context.getEnvironment());
			return context;
		} catch (NamingException e) {
			log.error("Error creating InitialContext", e);
			throw e;
		}
	}

	private static Context getContextEnvJboss4() throws NamingException {
		try {
			String host = "127.0.0.1";
			String port = "60015";
			long timeout = 5000;

			Properties properties = new Properties();
			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.NamingContextFactory");
			properties.put(Context.URL_PKG_PREFIXES, "org.jnp.interfaces");
			properties.put(Context.PROVIDER_URL, "jnp://" + host + ":" + port);
			// properties.put(TimedSocketFactory.JNP_TIMEOUT,
			// Long.valueOf(timeout).toString());
			// properties.put(TimedSocketFactory.JNP_SO_TIMEOUT,
			// Long.valueOf(timeout).toString());

			properties.put(Context.SECURITY_PRINCIPAL, "ADMIN1");
			properties.put(Context.SECURITY_CREDENTIALS, "ADMIN1ADMIN1");
			Context context = new InitialContext(properties);
			return context;
		} catch (NamingException e) {
			log.error("Error creating InitialContext", e);
			throw e;
		}
	}

	public static void closeConnection(final Connection con) {
		try {
			if (con != null)
				con.close();
		} catch (JMSException jmse) {
			log.error("Could not close connection " + con + ": ", jmse);
		}
	}

	public static void main(String[] args) {
		//String jbossVersion = "jboss4";
		String jbossVersion = "jboss7";
		testQueue(jbossVersion);
	}

	private static void testQueue(String jbossVersion) {
		try {
			Context ctx;
			ConnectionFactory connectionFactory;
			
			String queueName = null;
			String textToSend = null;
			if(jbossVersion.equals("jboss7")){
				//queueName = "ermalQueue";
				//queueName = "scapture";
				queueName = "remoteCMD";
				textToSend = "GET_STATUS";
				//textToSend = "Just a text sent to JMS queue";
				jndiPrefix = "jms/queue/";
				ctx = getContextEnvJboss7();
				connectionFactory = getConnectionFactoryJboss7(ctx);
			} else if(jbossVersion.equals("jboss4")){
				queueName = "messagerQueueTUTOR";
				textToSend = "object serializzato da mandare";
				jndiPrefix = "queue/";
				ctx = getContextEnvJboss4();
				connectionFactory = getConnectionFactoryJboss7(ctx);
			} else{
				System.out.println("Jboss7 oppure jboss4. nessun altro valore");
				return;
			}
		
			sendMessage(ctx, connectionFactory, queueName, textToSend);

		} catch (NamingException e) {
			log.error("NamingException");
		}
	}

	public static List<String> getQueueNames(final InitialContext ic) {
		if (ic == null)
			return null;
		LinkedList<String> l = new LinkedList<String>();
		try {
			Enumeration<NameClassPair> qnames = ic.list("queue");
			while (qnames.hasMoreElements())
				l.add(qnames.nextElement().getName());
		} catch (NamingException e) {
			log.error("Could not retrieve queue names: ", e);
			l = null;
		}
		return l;
	}
}
