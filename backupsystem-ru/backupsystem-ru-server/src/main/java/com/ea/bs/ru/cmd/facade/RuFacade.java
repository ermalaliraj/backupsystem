package com.ea.bs.ru.cmd.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.bs.ru.cmd.dto.RuDTO;
import com.ea.bs.ru.dao.RuDAO;
import com.ea.db.DBException;

@Stateless
public class RuFacade implements RuFacadeLocal {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(RuFacade.class);

	@EJB(name = "RuDAO")
	private RuDAO ruDAO;
	
	public RuDTO getRuDetail(Long idRu) throws DBException {
		return ruDAO.getRuDetail(idRu);
	}

	public boolean isAvailable(Long idRu) throws DBException {
		return ruDAO.isAvailable(idRu);
	}

	public void updateRuAsBusy(Long idRu) throws DBException {
		ruDAO.updateRuAsBusy(idRu);
	}

	public void updateRuAsAvailable(Long idRu) throws DBException {
		ruDAO.updateRuAsAvailable(idRu);
	}

	public void insertRemoteUnit(RuDTO dto) throws DBException {
		ruDAO.insertRemoteUnit(dto);
	}

}
