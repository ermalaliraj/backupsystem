package com.ea.bs.ru.dao;

import javax.ejb.Stateless;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.bs.ru.cmd.dto.RuDTO;
import com.ea.bs.ru.cmd.dto.StatusRu;
import com.ea.bs.ru.entity.RuEntity;
import com.ea.db.DBException;

@Stateless
public class RuDAOImpl extends GenericDAO implements RuDAO {
	
	private static final Log log = LogFactory.getLog(RuDAOImpl.class);
	
	public boolean isAvailable(Long idRu) throws DBException {
		boolean isAvaliable = false;
		try {
			RuEntity eDb = em.find(RuEntity.class, idRu);
			if (eDb != null) {
				if (eDb.getStatus().equals(StatusRu.AVAILABLE)) {
					isAvaliable = true;
				}
			}
			return isAvaliable;
		} catch (Exception e) {
			String msg = "[RU] Exception reading availability from DB for RU: "+idRu;
			log.error(msg, e);
			throw new DBException(msg, e);
		}
	}

	public void updateRuAsBusy(Long idRu) throws DBException {
		try{	
			RuEntity eDb = em.find(RuEntity.class, idRu);
			eDb.setStatus(StatusRu.BUSY);
			em.merge(eDb);
		} catch(Exception e){
			String msg = "[RU] Exception updating STATUS in DB for RU: "+idRu+" to BUSY";
			log.error(msg, e);
			throw new DBException(msg, e);
		}
	}

	public void updateRuAsAvailable(Long idRu) throws DBException {
		try{	
			RuEntity eDb = em.find(RuEntity.class, idRu);
			eDb.setStatus(StatusRu.AVAILABLE);
			em.merge(eDb);
		} catch(Exception e){
			String msg = "[RU] Exception updating STATUS in DB for RU: "+idRu+" to AVAILABLE";
			log.error(msg, e);
			throw new DBException(msg, e);
		}
	}

	public RuDTO getRuDetail(Long idRu) throws DBException {
		try {
			RuDTO dto = null;
			RuEntity e = em.find(RuEntity.class, idRu);
			if (e != null) {
				dto = new RuDTO();
				dto.setIdRu(e.getIdRu());
				dto.setName(e.getName());
				dto.setDescription(e.getDescription());
				StatusRu status = null ;
				if(e.getStatus() == null){
					status = StatusRu.NOT_DEFINED;
				} else {
					if (e.getStatus().equals(StatusRu.AVAILABLE)) {
						status = StatusRu.AVAILABLE;
					} else if (e.getStatus().equals(StatusRu.BUSY)) {
						status = StatusRu.BUSY;
					} else if (e.getStatus().equals(StatusRu.NOT_DEFINED)) {
						status = StatusRu.NOT_DEFINED;
					} else if (e.getStatus().equals(StatusRu.OUT_OF_ORDER)) {
						status = StatusRu.OUT_OF_ORDER;
					}
				}
				dto.setStatus(status);
			}
			return dto;
		} catch (Exception e) {
			String msg = "[RU] Exception getting details from DB for RU: "+idRu;
			log.error(msg+ ", Error: " + e.getMessage());
			throw new DBException(msg, e);
		}
	}

	public void insertRemoteUnit(RuDTO dto) throws DBException {
		try{
			RuEntity e = new RuEntity();
			e.setIdRu(dto.getIdRu());
			e.setName(dto.getName());
			e.setDescription(dto.getDescription());
			e.setStatus(dto.getStatus());
			em.persist(e);
		} catch(PersistenceException e){
			log.error("[RU] PersistenceException inserting in DB RU: " + dto, e);
			throw new DBException("[RU] PersistenceException inserting new RU", e);
		} catch(Exception e){
			log.error("[RU] Exception inserting in DB new RU: " + dto, e);
			throw new DBException("[RU] Exception inserting new RU", e);
		}
	}

}
