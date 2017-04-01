package com.ea.bs.server.service.facade;

import javax.ejb.Local;

import com.ea.db.DBException;

@Local
public interface ServiceFacadeLocal {
	
	public Long creaNewService(Long idRu) throws DBException;
	
	public void stopServiceForRu(Long idRu) throws DBException;
	
}
