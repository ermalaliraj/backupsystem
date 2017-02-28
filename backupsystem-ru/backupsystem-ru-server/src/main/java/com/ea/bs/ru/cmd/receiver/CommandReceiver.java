package com.ea.bs.ru.cmd.receiver;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.interceptor.InvocationContext;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.bs.protocol.cmd.message.CommandMessageType;
import com.ea.bs.protocol.cmd.message.RuMsgInfo;
import com.ea.bs.protocol.cmd.message.ru.RuDetailMessage;
import com.ea.bs.protocol.cmd.message.ru.SendSampleFileMessage;
import com.ea.bs.protocol.files.message.FileMsgInfo;
import com.ea.bs.ru.cmd.assembler.RuMsgInfoAssembler;
import com.ea.bs.ru.cmd.dto.RuDTO;
import com.ea.bs.ru.cmd.exception.ServiceAlreadyRunningException;
import com.ea.bs.ru.cmd.exception.ServiceAlreadyStoppedException;
import com.ea.bs.ru.cmd.facade.RuFacadeLocal;
import com.ea.bs.ru.files.SendFileManager;
import com.ea.bs.ru.files.SendFileTimer;
import com.ea.db.DBException;
import com.ea.jms.Message;
import com.ea.jms.exception.MessageApplicationException;
import com.ea.jms.exception.MessageException;
import com.ea.jms.receiver.MessageReceiver;


@MessageDriven(
		messageListenerInterface=MessageListener.class,
		mappedName = "cs/CommandReceiver"
		, activationConfig = {
			@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
			@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
			@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/remoteCMD")
		})
public class CommandReceiver extends MessageReceiver {

	private static final Log log = LogFactory.getLog(CommandReceiver.class);

	@EJB(name = "RuFacade")
	private RuFacadeLocal ruFacade;
	
	@EJB(name = "SendFileTimer")
	private SendFileTimer sendFileTimer;
	
	@EJB(name = "SendFileManager")
	private SendFileManager sendFileManager;
	
	private Long idRu;

	@PostConstruct
	public void init(InvocationContext ctx) {
		idRu = 1L;// in a real app, Identity data of RU will be in xml config file.
	}

	@Override
	protected Message process(Message message) {
		log.info("[RU] Processing message: " + message.getMessageType());
		Message response = null;
		if (message.getMessageType().equals(CommandMessageType.ServerMessageType.GET_STATUS.name())) {
			return getStatus();
		} else if (message.getMessageType().equals(CommandMessageType.ServerMessageType.START_SERVICE.name())) {
			return startService();
		} else if (message.getMessageType().equals(CommandMessageType.ServerMessageType.STOP_SERVICE.name())) {
			return stopService();
		} else if (message.getMessageType().equals(CommandMessageType.ServerMessageType.GET_SAMPLE_FILE.name())) {
			return getSampleFile();
		} else {
			// do not throw exception. We do not want same message to be re-send from the container
			String msgErr = "[RU] Command " + message.getMessageType() + " is UNKNOWN for RU " + idRu;
			log.debug(msgErr);
			response = new Message(CommandMessageType.RUMessageType.ERROR.name());
			response.setMessageObject(msgErr);
		}
		return response;
	}

	private Message getStatus() {
		log.debug("[RU] Getting status for RU "+idRu);
		Message response = null;
		try {
			RuDTO ruDto = ruFacade.getRuDetail(idRu);
			log.debug("[RU] Ru details from DB: "+ruDto);
			RuMsgInfo ruMsgInfoInfo = null;
			if(ruDto == null) {
				String msgErr = "[RU] Cannot get details from DB for RU: "+idRu;
				response = new Message(CommandMessageType.RUMessageType.ERROR.name());
				MessageException me = new MessageException(msgErr + "; Ru not present in SERVER registry.");
				response.setMessageObject(me);
			} else {
				ruMsgInfoInfo = RuMsgInfoAssembler.getStatusMsgFromDTO(ruDto);
				response = new RuDetailMessage(ruDto.getIdRu(), ruMsgInfoInfo);
			}
			log.debug("[RU] Status reply prepared in RU "+idRu);
		} catch (DBException e) {
			String msgErr = "[RU] Cannot get details from DB for RU: "+idRu;
			log.error(msgErr, e);
			response = new Message(CommandMessageType.RUMessageType.ERROR.name());
			MessageException me = new MessageException(msgErr + "; " + e.getMessage());
			response.setMessageObject(me);
		}
		return response;
	}
	
	private Message startService() {
		log.debug("[RU] Starting service in RU: "+idRu);
		Message response = null;
		try {
			if(ruFacade.isAvailable(idRu)){
				sendFileTimer.startSending();
				log.debug("[RU] Service started in RU: "+idRu);
				
				ruFacade.updateRuAsBusy(idRu);
				log.debug("[RU] Status updated in BUSY for RU: "+idRu);
				response = new Message(CommandMessageType.RUMessageType.ACK.name());
			} else {
				String msg = "[RU] Service not started! RU "+idRu+" is not in AVAILABLE status. Maybe BUSY or OUT_OF_ORDER";
				log.warn(msg);
				response = new Message(CommandMessageType.RUMessageType.ERROR.name());
				ServiceAlreadyRunningException e = new ServiceAlreadyRunningException(msg);
				response.setMessageObject(e);
			}
		} catch (DBException e) {
			String msgErr = "[RU] Cannot change status to BUSY in DB of RU: "+idRu;
			log.error(msgErr, e);
			response = new Message(CommandMessageType.RUMessageType.ERROR.name());
			MessageException me = new MessageException(msgErr + "; " + e.getMessage());
			response.setMessageObject(me);
		} catch (Exception e) {
			String msgErr = "[RU] Cannot start service on RU: "+idRu;
			log.error(msgErr, e);
			response = new Message(CommandMessageType.RUMessageType.ERROR.name());
			MessageException me = new MessageException(msgErr + "; " + e.getMessage());
			response.setMessageObject(me);
		}
		return response;
	}
	
	private Message stopService() {
		log.debug("[RU] Stopping service in RU: "+idRu);
		Message response = null;
		try {
			if(!ruFacade.isAvailable(idRu)){
				sendFileTimer.stopSending();
				log.debug("[RU] Service stopped in RU: "+idRu);
				
				ruFacade.updateRuAsAvailable(idRu);
				log.debug("[RU] Status updated in AVAILABLE for RU: "+idRu);
				response = new Message(CommandMessageType.RUMessageType.ACK.name());
			} else {
				String msg = "[RU] RU "+idRu+" is already in AVAILABLE status. No STOP command executed.";
				log.warn(msg);
				response = new Message(CommandMessageType.RUMessageType.ERROR.name());
				ServiceAlreadyStoppedException e = new ServiceAlreadyStoppedException(msg);
				response.setMessageObject(e);
			}
		} catch (DBException e) {
			String msgErr = "[RU] Cannot change status to AVAILABLE in DB of RU: "+idRu;
			log.error(msgErr, e);
			response = new Message(CommandMessageType.RUMessageType.ERROR.name());
			MessageException me = new MessageException(msgErr + "; " + e.getMessage());
			response.setMessageObject(me);
		} catch (Exception e) {
			String msgErr = "[RU] Cannot stop service on RU: "+idRu;
			log.error(msgErr, e);
			response = new Message(CommandMessageType.RUMessageType.ERROR.name());
			MessageException me = new MessageException(msgErr + "; " + e.getMessage());
			response.setMessageObject(me);
		}
		return response;
	}
	
	private Message getSampleFile(){
		log.debug("[RU] Getting Sample File from RU: "+idRu);
		Message response = null;
		try {
			sendFileManager.sendSampleFileLogic();
			log.debug("[RU] Sample Command correctly executed. Check Folder for incoming files from RU: "+idRu);
			List<FileMsgInfo> list = new ArrayList<FileMsgInfo>();
			response = new SendSampleFileMessage(idRu, list);
		} catch (Exception e) {
			String msgErr = "[RU] Cannot send Sample files from RU: "+idRu;
			//log.error(msgErr, e);
			response = new Message(CommandMessageType.RUMessageType.ERROR.name());
			MessageException me = new MessageApplicationException(msgErr + "; " + e.getMessage());
			response.setMessageObject(me);
		}
		return response;
	}

}
