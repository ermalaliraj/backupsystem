package com.ea.bs.server.ru.bean;

import javax.ejb.Local;

import com.ea.bs.api.web.ru.RuDTO;
import com.ea.db.DBException;
import com.ea.jms.MessageConnection;

@Local
public interface RuBeanLocal {
	
	public Long createRemoteUnit(RuDTO dto) throws DBException;
	
	public void removeRemoteUnit(Long id) throws DBException;
	
	public RuDTO getRemoteUnitServer(long idRu) throws DBException;
	
	public MessageConnection getMessageConnectionRu(long idRu) throws DBException;

}
