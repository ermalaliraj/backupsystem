package com.ea.bs.server.cmd.sender;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.bs.protocol.cmd.message.CommandMessageType;
import com.ea.bs.protocol.cmd.message.RuMsgInfo;
import com.ea.bs.protocol.cmd.message.ru.RuDetailMessage;
import com.ea.bs.protocol.cmd.message.ru.SendSampleFileMessage;
import com.ea.bs.protocol.cmd.message.server.StartServiceMessage;
import com.ea.bs.protocol.cmd.message.server.StopServiceMessage;
import com.ea.bs.protocol.files.message.FileMsgInfo;
import com.ea.bs.server.ru.bean.RuBeanLocal;
import com.ea.db.DBException;
import com.ea.jms.Message;
import com.ea.jms.MessageConnection;
import com.ea.jms.exception.MessageException;
import com.ea.jms.sender.MessageSenderSingle;


@Stateless
public class CommandSender extends MessageSenderSingle implements CommandSenderLocal {

	private static final Log log = LogFactory.getLog(CommandSender.class);

	@EJB(name = "RemoteUnitBean")
	private RuBeanLocal ruBean;

	@PostConstruct
	public void setDestinationName(InvocationContext ctx) {
		super.destinationName = "jms/queue/remoteCMD";
	}

	public RuMsgInfo getRuDetail(long idRu) throws MessageException {
		Message request = null;
		Message response = null;
		try {
			log.debug("[SERVER] Send command GET_STATUS to idRU: " + idRu);
			MessageConnection messageConnection = ruBean.getMessageConnectionRu(idRu);
			request = new Message(CommandMessageType.ServerMessageType.GET_STATUS.name());
			response = this.sendSynchMessage(messageConnection, request, false);
			if (response instanceof RuDetailMessage) {
				RuDetailMessage ruDetailMessage = (RuDetailMessage) response;
				return ruDetailMessage.getRuMsgInfo();
			} else {
				Exception e = null;
				if (response.getMessageObject() instanceof Exception){
					e = (Exception) response.getMessageObject();
				} else {
					e = new Exception("Wrong response from RU: " + idRu+"! Not compatible type RuDetailMessage");
				}
				log.warn("[SERVER] Wrong response from RU: " + idRu + ". Error: " + e.getMessage());
				throw new MessageException(e);
			}
		} catch (DBException e) {
			log.error("[SERVER] Cannot get MessageConnection for RU: " + idRu);
			throw new MessageException("Cannot get MessageConnection for RU "+idRu, e);
		}
	}
	
	public void startService(long idRu) throws MessageException {
		log.info("[SERVER] Send command " + CommandMessageType.ServerMessageType.START_SERVICE + " to idRu: "+idRu);
		try {
			MessageConnection messageConnection = ruBean.getMessageConnectionRu(idRu);
			StartServiceMessage request = new StartServiceMessage(idRu, new Date());
			Message response = this.sendSynchMessage(messageConnection, request, false);
			if (!CommandMessageType.RUMessageType.ACK.name().equalsIgnoreCase(response.getMessageType())){
				Throwable e = (Throwable) response.getMessageObject();
				log.error("[SERVER] RU "+idRu+" didn't replied with ACK, instead replied: "+response.getMessageType()+" Full reply: ", e);
			}
			log.info("[SERVER] Command " + CommandMessageType.ServerMessageType.START_SERVICE+ " finished processing for idRu: "+idRu);
		} catch (DBException e) {
			log.error("[SERVER] Cannot get MessageConnection from DB for idRu: " + idRu);
			throw new MessageException("Cannot get MessageConnection from DB for idRu: " + idRu);
		} 
	}


	public void stopService(long idRu) throws MessageException {
		log.info("[SERVER] Send command " + CommandMessageType.ServerMessageType.STOP_SERVICE + " to idRu: "+idRu);
		try {
			MessageConnection messageConnection = ruBean.getMessageConnectionRu(idRu);
			StopServiceMessage request = new StopServiceMessage(idRu, new Date());
			Message response = this.sendSynchMessage(messageConnection, request, false);
			if (!CommandMessageType.RUMessageType.ACK.name().equalsIgnoreCase(response.getMessageType())){
				Throwable e = (Throwable) response.getMessageObject();
				log.error("[SERVER] RU "+idRu+" didn't replied with ACK. RU reply: ", e);
				throw new MessageException(e);
			}
			log.info("[SERVER] Command " + CommandMessageType.ServerMessageType.STOP_SERVICE+ " finished processing for idRu: "+idRu);
		} catch (DBException e) {
			log.error("[SERVER] Cannot get MessageConnection from DB for idRu: " + idRu);
			throw new MessageException("Cannot get MessageConnection from DB for idRu: " + idRu);
		}
	}

	public List<FileMsgInfo> getSampleFiles(long idRu) throws MessageException {
		Message request = null;
		Message response = null;
		try {
			log.info("[SERVER] Send command " + CommandMessageType.ServerMessageType.GET_SAMPLE_FILE + " to idRu: "+idRu);
			MessageConnection messageConnection = ruBean.getMessageConnectionRu(idRu);
			request = new Message(CommandMessageType.ServerMessageType.GET_SAMPLE_FILE.name());
			response = this.sendSynchMessage(messageConnection, request, false);
			if (response instanceof SendSampleFileMessage) {
				SendSampleFileMessage sendFileMsg = (SendSampleFileMessage) response;
				List<FileMsgInfo> SampleFiles = sendFileMsg.getFiles();
				log.info("[SERVER] Command " + CommandMessageType.ServerMessageType.GET_SAMPLE_FILE+ " finished processing for idRu: "+idRu+" with "+SampleFiles.size()+" SampleFiles");
				return SampleFiles;
			} else {
				log.error("[SERVER] Response: " + response);
				Exception e = null;
				if (response.getMessageObject() instanceof Exception) {
					e = (Exception) response.getMessageObject();
				} else {
					e = new Exception("Cannot get SampleFiles from RU "+idRu+". Error not specified by RU.");
				}
				throw new MessageException(e);
			}
		} catch (DBException e) {
			log.error("[SERVER] Cannot get MessageConnection from DB for idRu: " + idRu);
			throw new MessageException("Cannot get MessageConnection from DB for idRu: " + idRu);
		} 
	}

}
