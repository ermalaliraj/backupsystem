package com.ea.bs.server.ru.dao;

import com.ea.bs.api.web.ru.RuDTO;
import com.ea.db.DBException;

public interface RuDAO {

	public Long creaRemoteUnit(RuDTO dto) throws DBException;
	
	public void removeRemoteUnit(Long id) throws DBException;

	public RuDTO getRemoteUnit(long idRu) throws DBException;

	public void updateRemoteUnit(RuDTO dto) throws DBException;

	public void delete(long idRu) throws DBException;

}
