package com.ea.bs.server.service.bean;

import javax.ejb.Local;

import com.ea.db.DBException;

@Local
public interface ServiceBeanLocal {
	
	public Long creaNewService(Long idRu) throws DBException;
	
	public void stopServiceForRu(Long idRu) throws DBException;
	
}
