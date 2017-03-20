package com.ea.cs.gui.cmd;

import java.util.HashMap;
import java.util.Hashtable;
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
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.jms.client.HornetQObjectMessage;
import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;


public class JMSTestStandalone {

	private static final Logger log = Logger.getLogger(JMSTestStandalone.class);
	protected String queueName = null;
	protected Context ctx = null;
	protected Queue queue = null;
	private static String jndiPrefix = null;

	public static void main(String[] args) {
		JMSTestStandalone app = new JMSTestStandalone();
		app.sendQueue();
	}

	public void sendQueue() {
		Connection connection = null;
		try {
			//String host = "10.151.4.162";
			String host = "192.168.1.9";
			String port = "4447";
			//String port = "5445"; 
			
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
			
//			Properties properties = new Properties();
//			properties.put(Context.INITIAL_CONTEXT_FACTORY, org.jboss.naming.remote.client.InitialContextFactory.class.getName());
//			properties.put(Context.PROVIDER_URL, "remote://" + host + ":" + port);
//			properties.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming");
//			properties.put(Context.SECURITY_PRINCIPAL, "testuser");//adminapp
//			properties.put(Context.SECURITY_CREDENTIALS, "testpwd");//adminpwd
//            Context ctx = new InitialContext(properties);
//            log.debug("Context created: "+ctx.getEnvironment());
//            
//            HashMap map = new HashMap();
//            map.put("host", host);
//            map.put("port", 5445);
//            TransportConfiguration transportConfiguration = new TransportConfiguration("org.hornetq.core.remoting.impl.netty.NettyConnectorFactory", map);
//            ConnectionFactory connectionFactory = (ConnectionFactory) HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.TOPIC_CF, transportConfiguration);
            
			
			log.debug("Got ConnectionFactory: " + connectionFactory);
		
			String jndiPrefix = "jms/queue/";
			String queueName = "remoteCMD";
			String textToSend = "GET_STATUS";
			//textToSend = "Just a text sent to JMS queue";
			String lookupObj = jndiPrefix + queueName;
			// String lookupObj = "/jms/queue/test";
			log.debug("Looking up to " + lookupObj);
	
			Destination queue = (Destination) ctx.lookup(lookupObj);
			log.debug("Got remote queue: " + queue);
	
			connection = connectionFactory.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(queue);
			ObjectMessage msg = session.createObjectMessage(textToSend);
			msg.setStringProperty("messageType", "PROVA_ERMAL");
			//TextMessage msg = session.createTextMessage();
			//msg.setText(text);
			
			log.debug("Sending message...");
			producer.send(msg);
			connection.start();
			log.debug("Message Sent.");
	
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			//drainQueue(ctx, queueName, 5);
		} catch (NamingException e) {
			log.error("NamingException", e);
		} catch (Exception e) {
			log.error("General Exception", e);
		} finally{
			closeConnection(connection);
		}
	}

	private static boolean drainQueue(final Context ctx, final String queueName, int amount) {
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
				ConnectionFactory cf = (ConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
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
	
	private Context getDynamicContext(){
		Context context = null;
		try {	
			String host = "192.168.1.8";
			//String host = "127.0.0.1";
			String port = "4447"; 
			
			// Create a EJB client configuration  
			//final EJBClientConfiguration ejbClientConfiguration = .... // somehow create the EJBClientConfiguration depending on the user application's necessity.  
			Properties properties = new Properties();
			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			properties.put(Context.PROVIDER_URL, "remote://" + host + ":" + port);
			properties.put("jboss.naming.client.ejb.context", "true");
			properties.put(Context.SECURITY_PRINCIPAL, "testuser");//adminapp
			properties.put(Context.SECURITY_CREDENTIALS, "testpwd");//adminpwd
			
			final EJBClientConfiguration ejbClientConfiguration = new PropertiesBasedEJBClientConfiguration(properties);
			final ContextSelector<EJBClientContext> ejbClientContextSelector = new ConfigBasedEJBClientContextSelector(ejbClientConfiguration);  
			final ContextSelector<EJBClientContext> previousSelector = EJBClientContext.setSelector(ejbClientContextSelector);  
			  
			// Now that we have setup the EJB client context (backed by our client configuration and receiver information), let's now do the JNDI lookup and  		invoke on the proxies  
			final Hashtable props = new Hashtable();  
			// setup the ejb: namespace URL factory  
			props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");  
		  
			context = new javax.naming.InitialContext(props);
		} catch (NamingException e) {
			log.error("NamingException", e);
		}
		return context;
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
