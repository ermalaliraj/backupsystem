package com.ea.bs.server.service.dao;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.bs.server.ru.dao.GenericDAO;
import com.ea.bs.server.service.entity.ServiceEntity;
import com.ea.db.DBException;


@Stateless
public class ServiceDAOImpl extends GenericDAO implements ServiceDAO {
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(ServiceDAOImpl.class);

	public Long creaNewService(Long idRu) throws DBException{
		ServiceEntity e = new ServiceEntity();
		//e.setIdRu(dto.getIdRu());
		
		//em.persist(e);
		return e.getIdService();
	}
	
	public void stopServiceForRu(Long idRu) throws DBException{
		// Save in SERVER service as stopped
	}

}
