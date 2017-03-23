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
import com.ea.jms.exception.MessageException;
import com.ea.jms.sender.MessageSender;


@Stateless
public class FileSender extends MessageSender implements FileSenderLocal {

	private static final Log log = LogFactory.getLog(FileSender.class);
	
	@EJB(name = "RuConfig")
	private RuConfig ruConfig;

	@PostConstruct
	public void setDestinationName(InvocationContext ctx) {
		//super.destinationName = "jms/queue/scapture";
		super.destinationName = "scapture";
		super.messageConnection = ruConfig.getServerConnection();
	}

	public void sendFiles(List<FileMsgInfo> list) throws MessageException {
		log.info("Sending files to the Server");
		long idRu = ruConfig.getConfigDescriptor().getIdRu();
		SendFileMessage sendFileMsg = new SendFileMessage(idRu, list);
		sendAsynchMessage(sendFileMsg);
		log.info("[RU] "+list.size() + " files correctly sent to the server from RU:"+idRu);
	}

	
}
