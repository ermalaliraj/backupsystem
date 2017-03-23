package com.ea.bs.server.web.cmd;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.bs.api.web.cmd.exception.CannotStartServiceException;
import com.ea.bs.api.web.cmd.exception.CannotStopServiceException;
import com.ea.bs.api.web.cmd.exception.RuNotPresentInRegistryException;
import com.ea.bs.api.web.cmd.exception.ServiceAlreadyRunningException;
import com.ea.bs.api.web.cmd.exception.ServiceAlreadyStoppedException;
import com.ea.bs.api.web.cmd.remote.RuCommandRemote;
import com.ea.bs.api.web.exception.ServerException;
import com.ea.bs.api.web.ru.RuDTO;
import com.ea.bs.protocol.cmd.message.RuMsgInfo;
import com.ea.bs.protocol.files.message.FileMsgInfo;
import com.ea.bs.server.cmd.assembler.CommandAssembler;
import com.ea.bs.server.cmd.sender.CommandSenderLocal;
import com.ea.bs.server.files.assembler.FileAssembler;
import com.ea.bs.server.files.dto.FileServerDTO;
import com.ea.bs.server.ru.bean.RuBeanLocal;
import com.ea.bs.server.service.bean.ServiceBeanLocal;
import com.ea.bs.server.web.session.WebAccessInterceptor;
import com.ea.db.DBException;
import com.ea.jms.Message;
import com.ea.jms.exception.MessageException;

@Stateless
@Remote(RuCommandRemote.class)
@Interceptors({ WebAccessInterceptor.class })
@RolesAllowed({ "ADMIN" })
public class RuCommand implements RuCommandRemote {
	
	private static final Log log = LogFactory.getLog(RuCommand.class);
	
	@Resource
	private SessionContext ctx;

	@EJB(name = "CommandSender")
	private CommandSenderLocal commandSender;
	
	@EJB(name = "RuBean")
	private RuBeanLocal ruBean;
	
	@EJB(name = "ServiceBean")
	private ServiceBeanLocal serviceBean;

	public Long crateNewRu(RuDTO ruDTO) throws ServerException {
		log.debug("[SERVER] Create new RemoteUnit");
		try {
			Long id = ruBean.createRemoteUnit(ruDTO);
			return id;
		} catch (Exception e) {
			log.error("[SERVER] Cannot insert in DB new RemoteUnit: "+ruDTO, e);
			throw new ServerException("Cannot create new RemoteUnit. Error: " + e.getMessage());
		}
	}
	
	public void removeRemoteUnit(Long id) throws ServerException{
		log.debug("[SERVER] Remove RemoteUnit "+id);
		try {
			ruBean.removeRemoteUnit(id);
		} catch (Exception e) {
			log.error("[SERVER] Cannot remove from DB RemoteUnit: "+id, e);
			throw new ServerException("Cannot remove RemoteUnit from DB. Error: " + e.getMessage());
		}
	}
	
	public RuDTO getStatus(long idRu) throws ServerException, RuNotPresentInRegistryException {
		log.debug("[SERVER] Get Status for ru: " + idRu);
		try {
			RuDTO ruDTO = ruBean.getRemoteUnitServer(idRu);
			log.debug("[SERVER] RU from DB: "+ruDTO); 
			if(ruDTO != null){
				RuMsgInfo ruMsgInfo = commandSender.getRuDetail(idRu);
				RuDTO res = CommandAssembler.getStatusRuDTO(idRu, ruMsgInfo);
				log.debug("[SERVER] Status from RU reply: "+res);
				return res;
			} else {
				throw new RuNotPresentInRegistryException("RU "+idRu+" not present in SERVER registry");
			}
		}
		catch (RuNotPresentInRegistryException e) {
			log.warn("[SERVER] No RU present in server DB with id: "+idRu);
			throw e;
		} catch (DBException e) {
			log.error("[SERVER] RemoteUnit with id '"+idRu+"'not present in DB", e);
			throw new ServerException("RemoteUnit with id '"+idRu+"' not present in DB. Error: "+e.getMessage());
		} catch (MessageException e) {
			log.error("[SERVER] MessageException GETTING status from ru: "+idRu+". Error: "+e.getMessage());
			throw new ServerException("Cannot GET status from ru: "+idRu+". Error: "+e.getMessage());
		} catch (Exception e) {
			log.error("[SERVER] General Exception GETTING status from ru: "+idRu+". Error: "+e.getMessage(), e);
			throw new ServerException("Cannot GET status from ru: "+idRu+". Error: "+e.getMessage());
		}
	}
	
	@Interceptors({ WebAccessInterceptor.class })
	public void avviaServizio(long idRu) throws ServerException, ServiceAlreadyRunningException, CannotStartServiceException, RuNotPresentInRegistryException {
		log.info("[SERVER] Starting service for idRu: "+idRu);
		try {
			RuDTO ruDTO = ruBean.getRemoteUnitServer(idRu);
			if(ruDTO != null){
				Long idService = serviceBean.creaNewService(idRu);
				log.debug("[SERVER] New service "+idService + " created in DB for ru: "+idRu);
				
				commandSender.startService(idRu);
				log.info("[SERVER] Successfully started service for idRu: "+idRu);
			} else {
				throw new RuNotPresentInRegistryException("RU "+idRu+" not present in SERVER registry");
			}
		} catch(RuNotPresentInRegistryException e) {
			log.warn("[SERVER] No RU present in server DB with id: "+idRu);
			throw e;
		} catch(DBException e) {
			ctx.setRollbackOnly();
			log.error("[SERVER] DBException, cannot start service for RU: "+idRu, e);
			throw new CannotStartServiceException("Cannot start service for RU: " + idRu);
		} catch(MessageException e) {
			if(e.getMessage()!=null && e.getMessage().contains(ServiceAlreadyRunningException.class.getSimpleName())){
				log.error("[SERVER] RemoteUnit is non in AVAILABLE status: "+e.getMessage());
				throw new ServiceAlreadyRunningException("RemoteUnit is non in AVAILABLE status");
			}else{
				ctx.setRollbackOnly();
				log.error("[SERVER] MessageException while starting service for idRu: " + idRu, e);
				throw new CannotStartServiceException("MessageException while starting service for idRu: " + idRu);
			}
		} catch(Exception e){
			//NOT ServiceAlreadyRunningException not CannotStartServiceException already handled in MessageException block
			//if(!(e instanceof ServiceAlreadyRunningException || e instanceof CannotStartServiceException)){
				ctx.setRollbackOnly();
				log.error("[SERVER] STRANGE SITUATION!!! This should not happen. If happens, check and handle similar situations");
				log.error("[SERVER] General Exception while starting service for idRu: " + idRu);
				throw new CannotStartServiceException("General Exception while starting service for idRu: " + idRu);
			//}
		}
	}

	@Interceptors({ WebAccessInterceptor.class })
	public void stopService(long idRu) throws ServerException, CannotStopServiceException, ServiceAlreadyStoppedException, RuNotPresentInRegistryException {
		log.info("[SERVER] Stopping service for idRu: "+idRu);
		try {
			RuDTO ruDTO = ruBean.getRemoteUnitServer(idRu);
			if(ruDTO != null){
				serviceBean.stopServiceForRu(idRu);
				log.debug("[SERVER] Service set as STOPPED in DB-SERVER for ru: "+idRu);
				
				commandSender.stopService(idRu);
				log.info("[SERVER] Successfully stopped service for idRu: "+idRu);
			} else {
				throw new RuNotPresentInRegistryException("RU "+idRu+" not present in SERVER registry");
			}
		} catch(RuNotPresentInRegistryException e) {
			log.warn("[SERVER] No RU present in server DB with id: "+idRu);
			throw e;
		} catch(DBException e) {
			ctx.setRollbackOnly();
			log.error("[SERVER] DBException, Cannot stop service for idRu:" + idRu);
			throw new CannotStopServiceException("Error stopping service for idRu: " + idRu);
		}
		catch(MessageException e) {
			if(e.getMessage()!=null && e.getMessage().contains(ServiceAlreadyRunningException.class.getSimpleName())){
				log.error("[SERVER] RemoteUnit is already stopped (AVAILABLE status)", e);
				throw new ServiceAlreadyStoppedException("RemoteUnit is non in AVAILABLE status");
			}else{
				ctx.setRollbackOnly();
				log.error("[SERVER] MessageException while stopping service for idRu: " + idRu);
				throw new CannotStopServiceException("MessageException while stopping service for idRu: " + idRu);
			}
		} catch(Exception e){
			//NOT ServiceAlreadyRunningException not CannotStartServiceException already handled in MessageException block
			if(!(e instanceof ServiceAlreadyRunningException || e instanceof CannotStartServiceException)){
				ctx.setRollbackOnly();
				log.error("[SERVER] STRANGE SITUATION!!! This should not happen. If happens, check and handle similar situations");
				log.error("[SERVER] General Exception while stopping service for idRu: " + idRu);
				throw new CannotStopServiceException("General Exception while stopping service for idRu: " + idRu);
			}
		} 
	}
	
	@RolesAllowed({"USER"})
	public List<byte[]> getSampleFiles(long idRu) throws ServerException {
		List<byte[]> filesList = new ArrayList<byte[]>();
		try {
			log.info("[SERVER] SampleFiles for idRu: " + idRu);
			List<FileMsgInfo> filesMsgList = commandSender.getSampleFiles(idRu);
			
			List<FileServerDTO> fileDTOList = new ArrayList<FileServerDTO>();
			for (FileMsgInfo fileMsg : filesMsgList) {
				fileDTOList.add(FileAssembler.getFileDTO(idRu, fileMsg));
			}
			
			for (FileServerDTO fileDTO : fileDTOList) {
				filesList.add(fileDTO.getFileBytes());
				log.debug("[SERVER] Size single sample file received: " + fileDTO.getFileBytes().length);
			}
			log.info("[SERVER] SampleFiles finished for idRu: " + idRu);
		} catch (MessageException e) {
			log.error("[SERVER] Exception sending GET_SAMPLE_FILE to idRu: " + idRu, e);
			throw new ServerException("Exception getting sample file for idRu: " + idRu);
		} catch (Exception e) {
			log.error("[SERVER] General Exception sending GET_SAMPLE_FILE to idRu: " + idRu, e);
			throw new ServerException("General Exception getting sample file for idRu: " + idRu);
		}	
		return filesList;
	}
	
	@RolesAllowed({"USER"})
	public void pingAsynch(long idRu) throws ServerException {
		try {
			log.info("[SERVER] Ping Asynch for idRu: " + idRu);
			commandSender.pingAsynch(idRu);
			log.info("[SERVER] Ping Asynch finished for idRu: " + idRu);
		} catch (MessageException e) {
			log.error("[SERVER] MessageException sending PING_ASYNCH to idRu: " + idRu, e);
			throw new ServerException("Exception sending PING_ASYNCH for idRu: " + idRu, e);
		} catch (Exception e) {
			log.error("[SERVER] General Exception sending PING_ASYNCH to idRu: " + idRu, e);
			throw new ServerException("General Exception sending PING_ASYNCH to idRu: " + idRu, e);
		}	
	}
	
	@RolesAllowed({"USER"})
	public String pingSynch(long idRu) throws ServerException {
		try {
			log.info("[SERVER] Ping Asynch for idRu: " + idRu);
			Message response = commandSender.pingSynch(idRu);
			log.debug("[SERVER] Response from RU: "+response);
			log.info("[SERVER] Ping Synch finished for idRu: " + idRu);
			return response.getMessageType();
		} catch (MessageException e) {
			log.error("[SERVER] MessageException sending PING_SYNCH to idRu: " + idRu, e);
			throw new ServerException("MessageException sending PING_SYNCH to idRu: " + idRu);
		} catch (Exception e) {
			log.error("[SERVER] General Exception sending PING_SYNCH to idRu: " + idRu, e);
			throw new ServerException("General Exception sending PING_SYNCH to idRu: " + idRu);
		}	
	}

}
