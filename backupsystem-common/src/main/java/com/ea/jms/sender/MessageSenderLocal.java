package com.ea.jms.sender;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.jms.Message;
import com.ea.jms.exception.MessageException;

/**
 * Abstract class for sending JMS Local messages.
 */
public abstract class MessageSenderLocal {

	private static final Log log = LogFactory.getLog(MessageSenderLocal.class);

	//@Resource(mappedName = "java:/LocalJMS")
	private ConnectionFactory connectionFactory;

	protected void sendMessage(Destination destination, Message message) throws MessageException {
		this.sendMessage(destination, message, true);
	}

	protected void sendMessage(Destination destination, Message message, boolean msgPersistence) throws MessageException {
		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;

		log.info("Sending message, LocalMessageSender -->" + this.hashCode() + " ...");
		try {
			log.trace("Creating JMS connection, LocalMessageSender -->" + this.hashCode());
			connection = connectionFactory.createConnection();

			log.trace("Creating JMS session, LocalMessageSender -->" + this.hashCode());
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			log.trace("Creating JMS producer, LocalMessageSender -->" + this.hashCode());
			producer = session.createProducer(destination);

			log.trace("Creating JMS Message, LocalMessageSender -->" + this.hashCode());
			ObjectMessage jmsMessage = session.createObjectMessage(message);
			jmsMessage.setStringProperty("messageType", message.getMessageType());
			
			if (!msgPersistence)
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			log.trace("Sending JMS message, LocalMessageSender -->" + this.hashCode());
			producer.send(jmsMessage);
		} catch (JMSException jmse) {
			throw new MessageException("Error occurred while sending asynchronous message, Exception --> " + jmse.getMessage(), jmse);
		} finally {
			closeJmsProducer(producer);
			closeJmsSession(session);
			closeJmsConnection(connection);
		}
		log.info("Message sent correctly, LocalMessageSender -->" + this.hashCode());
	}
	
	private void closeJmsProducer(MessageProducer producer) {
		if (producer != null) {
			try {
				producer.close();
			} catch (Exception e) {
				log.warn("JMS producer cannot be closed. EJB MessageSender -->" + hashCode(), e);
			}
		}
	}
	
	private void closeJmsSession(Session session) {
		if (session != null) {
			try {
				session.close();
			} catch (Exception e) {
				log.warn("JMS session cannot be closed. EJB MessageSender -->" + hashCode(), e);
			}
		}
	}
	
	private void closeJmsConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				log.warn("JMS connection cannot be closed. EJB MessageSender -->" + hashCode(), e);
			}
		}
	}

}
