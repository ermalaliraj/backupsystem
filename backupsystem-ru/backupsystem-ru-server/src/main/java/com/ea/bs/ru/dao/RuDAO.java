package com.ea.bs.ru.dao;

import com.ea.bs.ru.cmd.dto.RuDTO;
import com.ea.db.DBException;


public interface RuDAO {

	public boolean isAvailable(Long idRu) throws DBException;

	public void updateRuAsBusy(Long idRu) throws DBException;

	public void updateRuAsAvailable(Long idRu) throws DBException;

	public RuDTO getRuDetail(Long idRu) throws DBException;

	public void insertRemoteUnit(RuDTO dto) throws DBException;

}
