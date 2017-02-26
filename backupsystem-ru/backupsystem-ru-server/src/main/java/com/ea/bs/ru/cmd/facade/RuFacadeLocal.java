package com.ea.bs.ru.cmd.facade;

import javax.ejb.Local;

import com.ea.bs.ru.cmd.dto.RuDTO;
import com.ea.db.DBException;

@Local
public interface RuFacadeLocal {

	public boolean isAvailable(Long idRu) throws DBException;

	public void updateRuAsBusy(Long idRu) throws DBException;

	public void updateRuAsAvailable(Long idRu) throws DBException;

	public RuDTO getRuDetail(Long idRu) throws DBException;

	public void insertRemoteUnit(RuDTO dto) throws DBException;

}
