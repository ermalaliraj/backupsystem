package com.ea.bs.server.ru.dao;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.bs.api.web.ru.RuDTO;
import com.ea.bs.server.ru.entity.RuEntity;
import com.ea.db.DBException;

@Stateless
public class RuDAOImpl extends GenericDAO implements RuDAO {
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(RuDAOImpl.class);

	public Long creaRemoteUnit(RuDTO dto) throws DBException {
		try{
			RuEntity e = new RuEntity();
			e.setIdRu(dto.getIdRu());
			e.setName(dto.getName());
			e.setDescription(dto.getDescription());
			e.setStatus(dto.getStatus());
			em.persist(e);
			return e.getIdRu();
		} catch (Exception e) {
			throw new DBException("[SERVER] Exception inserting new RU", e);
		}
	}
	
	public void removeRemoteUnit(Long id) throws DBException {
		try{
			RuEntity e = new RuEntity();
			e.setIdRu(id);
			em.remove(e);
		} catch (Exception e) {
			throw new DBException("[SERVER] Exception removing RU: "+id, e);
		}
	}

	public RuDTO getRemoteUnit(long idRu) throws DBException {
		try{
			RuEntity eDb = em.find(RuEntity.class, idRu);
			RuDTO dto = null;
			if(eDb != null){
				dto = new RuDTO();
				dto.setIdRu(eDb.getIdRu());
				dto.setName(eDb.getDescription());
				dto.setStatus(eDb.getStatus());
			}
			return dto;
		} catch (Exception e) {
			throw new DBException("[SERVER] Exception getting RU from from DB", e);
		}
	}
	
	public void updateRemoteUnit(RuDTO dto) throws DBException {
		try{
			RuEntity e = new RuEntity();
			e.setIdRu(dto.getIdRu());
			e.setName(dto.getName());
			e.setDescription(dto.getDescription());
			e.setStatus(dto.getStatus());
			em.merge(e);
		} catch (Exception e) {
			throw new DBException("[SERVER] Exception updating RU in DB", e);
		}
	}
	
	public void delete(long idRu) throws DBException {
		try{
			RuEntity toBeRemoved = em.getReference(RuEntity.class, idRu);
			em.remove(toBeRemoved);
		} catch (Exception e) {
			throw new DBException("[SERVER] Exception deleting RU", e);
		}
	}

}
