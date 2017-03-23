package com.ea.bs.ru.files.sender;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.bs.protocol.files.message.FileMsgInfo;
import com.ea.bs.protocol.files.message.ru.SendFileMessage;
import com.ea.bs.ru.config.RuConfig;
import com.ea.jms.MessageConnection;
import com.ea.jms.exception.MessageException;
import com.ea.jms.sender.MessageSenderSingle;


@Stateless
public class FileSender extends MessageSenderSingle implements FileSenderLocal {

	private static final Log log = LogFactory.getLog(FileSender.class);
	
	@EJB(name = "RuConfig")
	private RuConfig ruConfig;
	
	MessageConnection messageConnection;

	@PostConstruct
	public void setDestinationName(InvocationContext ctx) {
		//super.destinationName = "jms/queue/scapture";
		super.destinationName = "scapture";
		messageConnection = ruConfig.getServerConnection();
	}

	public void sendFiles(List<FileMsgInfo> list) throws MessageException {
		log.info("Sending files to the Server");
		long idRu = ruConfig.getConfigDescriptor().getIdRu();
		SendFileMessage sendFileMsg = new SendFileMessage(idRu, list);
		//sendAsynchMessage(messageConnection, sendFileMsg);
		sendAsynchMessage(messageConnection, sendFileMsg, false);
		log.info("[RU] "+list.size() + " files correctly sent to the server from RU:"+idRu);
	}

	
}
