package com.ea.bs.server.service.dao;

import com.ea.db.DBException;

public interface ServiceDAO {

	public Long creaNewService(Long idRu) throws DBException;
	
	public void stopServiceForRu(Long idR) throws DBException;

}
