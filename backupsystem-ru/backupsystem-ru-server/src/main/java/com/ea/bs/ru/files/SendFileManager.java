package com.ea.bs.ru.files;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.bs.protocol.files.message.FileMsgInfo;
import com.ea.bs.ru.config.RuConfig;
import com.ea.bs.ru.files.assembler.FileAssembler;
import com.ea.bs.ru.files.dto.FileRuDTO;
import com.ea.bs.ru.files.facade.FileFacadeLocal;
import com.ea.bs.ru.files.sender.FileSenderLocal;
import com.ea.db.DBException;
import com.ea.jms.exception.MessageApplicationException;
import com.ea.jms.exception.MessageException;
import com.ea.util.IOUtils;

@Stateless
@LocalBean
public class SendFileManager {

	private static final Log log = LogFactory.getLog(SendFileManager.class);

	@Resource
	private SessionContext ctx;
	
	@EJB(name = "FileSender")
	private FileSenderLocal fileSender;
	@EJB(name = "FileFacade")
	private FileFacadeLocal fileFacade;
	@EJB(name = "RuConfig")
	private RuConfig ruConfig;
	
	private Long idService;
	
	public void sendFileLogic() {
		String pathWithFiles = ruConfig.getRuDescriptor().getPathWithFilesToBeSend();
		log.debug("[RU] Checking files ready to be sent on path: "+pathWithFiles+", isEncrypted?: "+ruConfig.getRuDescriptor().isCryptedData());
		try {
			this.idService = 1L; //get from serviceBean  
			List<File> files = IOUtils.getFilesFromPath(pathWithFiles, ruConfig.getRuDescriptor().getMaxNrFilesPerMsg(), ruConfig.getRuDescriptor().getMaxSizeBytePerFile());
			log.debug("[RU] Tot files to send: "+files.size());
			
			List<FileRuDTO> dtoList = new ArrayList<FileRuDTO>(); 
			List<FileMsgInfo> listMsg = new ArrayList<FileMsgInfo>();
			for (File f : files) {
				log.debug("[RU] file - " + f);
				FileRuDTO fileDTO = new FileRuDTO();
				fileDTO.setDate(new Date());
				fileDTO.setFileName(f.getName());
				fileDTO.setIdService(idService);
				fileDTO.setFileBytes(IOUtils.getFileContent(f));

				dtoList.add(fileDTO);
				log.debug("[RU] fileDTO - " + fileDTO);
				
				FileMsgInfo fileMsg = FileAssembler.getFileMsg(fileDTO);
				listMsg.add(fileMsg);	
			}
			
			if(files.size() > 0){
				//1. Save in DB
				fileFacade.saveFile(dtoList, pathWithFiles, ruConfig.getRuDescriptor().isCryptedData());
				
				//2. Send files
				fileSender.sendFiles(listMsg);
				
				//3. Delete already sent files
				String fileBackuped = null;
				for (FileRuDTO fileDTO : dtoList) {
					try{
						fileBackuped = pathWithFiles + fileDTO.getFileName();
						if(ruConfig.getRuDescriptor().isBackupSent()){
							//save in backup folder
						}
						log.debug("[RU] Deleting from filesystem, file: "+fileBackuped);
						IOUtils.deleteFile(fileBackuped);
					} catch (Exception e) {
						log.error("[RU] Exception deleting from filesystem file: " + fileBackuped, e);
					} 
				}
				log.info("[RU] " + files.size() + " files correctly sent to the server");
			} else {
				log.info("[RU] No file present to be sent to the server");
			}
		} catch (IOException e) {
			log.error("[RU] IOException occured when Timer fired. Check all paths!!!", e);
		} catch (DBException e) {
			log.error("[RU] DBException", e);
		} catch (MessageException e) {
			log.error("[RU] MessageException", e);
		} catch (GeneralSecurityException e) {
			log.error("[RU] MessageException", e);
		}catch (Exception e) {
			log.error("[RU] General Exception on sendFileLogic ", e);
		}
	}
	
	public void sendSampleFileLogic() throws MessageApplicationException {
		String pathWithFiles = ruConfig.getRuDescriptor().getPathWithFilesToBeSend();
		log.debug("[RU] FileManager - Checking files ready to be sent on path: "+pathWithFiles+", isEncrypted?: "+ruConfig.getRuDescriptor().isCryptedData());
		try {
			this.idService = 1L; //get from serviceBean  
			List<File> files = IOUtils.getFilesFromPath(pathWithFiles, ruConfig.getRuDescriptor().getMaxNrFilesPerMsg(), ruConfig.getRuDescriptor().getMaxSizeBytePerFile());
			log.debug("[RU] Tot files to send: "+files.size());
			
			List<FileRuDTO> dtoList = new ArrayList<FileRuDTO>(); 
			List<FileMsgInfo> listMsg = new ArrayList<FileMsgInfo>();
			for (File f : files) {
				log.debug("[RU] file - " + f);
				FileRuDTO fileDTO = new FileRuDTO();
				fileDTO.setDate(new Date());
				fileDTO.setFileName(f.getName());
				fileDTO.setIdService(idService);
				fileDTO.setFileBytes(IOUtils.getFileContent(f));

				dtoList.add(fileDTO);
				log.debug("[RU] fileDTO - " + fileDTO);
				
				FileMsgInfo fileMsg = FileAssembler.getFileMsg(fileDTO);
				listMsg.add(fileMsg);	
			}
			
			if(files.size() > 0){
				fileSender.sendFiles(listMsg);
				log.info("[RU] " + files.size() + " files correctly sent to the server");
			} else {
				log.info("[RU] No file present to be sent to the server");
			}
		} catch (IOException e) {
			log.error("[RU] IOException occured (exit with MessageApplicationException). Check all paths!!!", e);
			throw new MessageApplicationException(e);
		} catch (MessageException e) {
			log.error("[RU] MessageException (exit with MessageApplicationException)", e);
			throw new MessageApplicationException(e);
		} catch (Exception e) {
			log.error("[RU] General Exception (exit with MessageApplicationException) on sendSampleFileLogic ", e);
			throw new MessageApplicationException(e);
		} catch (Throwable e) {
			log.error("[RU] RUNTIME Exception on sendFileLogic (will exit as application exception. No need for rollback) ", e);
			throw new MessageApplicationException(e);
		}
	}

}
