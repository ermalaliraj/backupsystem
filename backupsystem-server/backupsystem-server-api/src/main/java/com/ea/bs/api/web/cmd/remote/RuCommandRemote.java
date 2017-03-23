package com.ea.bs.api.web.cmd.remote;

import java.util.List;

import javax.ejb.Remote;

import com.ea.bs.api.web.cmd.exception.CannotStartServiceException;
import com.ea.bs.api.web.cmd.exception.CannotStopServiceException;
import com.ea.bs.api.web.cmd.exception.RuNotPresentInRegistryException;
import com.ea.bs.api.web.cmd.exception.ServiceAlreadyRunningException;
import com.ea.bs.api.web.cmd.exception.ServiceAlreadyStoppedException;
import com.ea.bs.api.web.exception.ServerException;
import com.ea.bs.api.web.ru.RuDTO;

@Remote
public interface RuCommandRemote {
	
	public Long crateNewRu(RuDTO ruDto) throws ServerException;
	
	public void removeRemoteUnit(Long id) throws ServerException;
	
	public RuDTO getStatus(long idRu) throws ServerException, RuNotPresentInRegistryException;
	
	public void avviaServizio(long idRu) throws ServerException, CannotStartServiceException, ServiceAlreadyRunningException, RuNotPresentInRegistryException;

	public void stopService(long idRu) throws ServerException, CannotStopServiceException, ServiceAlreadyStoppedException, RuNotPresentInRegistryException;

	public List<byte[]> getSampleFiles(long idRu) throws ServerException;
	
	public void pingAsynch(long idRu) throws ServerException;
	
	public String pingSynch(long idRu) throws ServerException;


}
