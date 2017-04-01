package com.ea.bs.server.files.receiver;

import java.io.IOException;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.bs.protocol.files.message.FileMessageType;
import com.ea.bs.protocol.files.message.FileMsgInfo;
import com.ea.bs.protocol.files.message.ru.SendFileMessage;
import com.ea.bs.server.config.ServerConfig;
import com.ea.bs.server.files.assembler.FileAssembler;
import com.ea.bs.server.files.dto.FileServerDTO;
import com.ea.bs.server.files.facade.FileFacadeLocal;
import com.ea.db.DBException;
import com.ea.jms.Message;
import com.ea.jms.receiver.MessageReceiver;
import com.ea.util.IOUtils;


@MessageDriven(
		messageListenerInterface=MessageListener.class,
		mappedName = "bs/FileReceiver"
		, activationConfig = {
			@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
			@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
			@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/scapture"),
		})
public class FileReceiver extends MessageReceiver {

	private static final Log log = LogFactory.getLog(FileReceiver.class);

	@EJB(name = "FileBean")
	private FileFacadeLocal fileFacade;
	
	@EJB(name = "ServerConfig")
	private ServerConfig serverConfig;

	@Override
	protected Message process(Message message) {
		long startTime = System.currentTimeMillis();
		log.info("[SERVER] Processing message: " + message.getMessageType());
		long idRu = -1;
		try {
			
			if (message.getMessageType().equalsIgnoreCase(FileMessageType.RUMessageType.SEND_FILE.name())) {
				SendFileMessage sendFileMsg = (SendFileMessage) message;
				idRu = sendFileMsg.getIdRu();
				List<FileMsgInfo> filesMsgInfo = sendFileMsg.getFiles();
				log.debug("[SERVER] Received " + filesMsgInfo.size() + " files from RU: " + idRu);
	
				// For each file message
				int count = 0;
				for (FileMsgInfo fileMsgInfo : filesMsgInfo) {
					FileServerDTO fileDto = FileAssembler.getFileDTO(idRu, fileMsgInfo);
					log.debug("[SERVER] FileDTO: " + fileDto);
					
					//2. Save in DB
					fileFacade.saveFile(fileDto);
				
					//3. Save to filesystem
					log.debug("[SERVER] FileDTO (crypted) from RU: "+fileDto);
					String fileName = IOUtils.normalizePath(serverConfig.getServerDescriptor().getBackupPath()) + fileDto.getFileName();
					log.debug("[SERVER] Backuping: "+fileName);
					IOUtils.setFileContent(fileName, fileDto.getFileBytes());
					count ++;
				}
								
				long endTime = System.currentTimeMillis();
				log.debug("[SERVER] " + count + " files correctly backuped from RU " + idRu + " in " + (endTime - startTime) + " ms");
			} else {
				log.error("[SERVER] Received UNKNOWN message of messageType: " + message.getMessageType() + ", Receiver --> " + ctx.hashCode());
			}
		} catch (DBException e) {
			log.error("[SERVER] DBException backuping received file from  idRu: "+idRu, e);
		} catch (IOException e) {
			log.error("[SERVER] IOException backuping received file from  idRu: "+idRu, e);
		} catch (Exception e) {
			log.error("[SERVER] General Exception backuping received file from  idRu: "+idRu, e);
		}
		return null;
	}

	
}
