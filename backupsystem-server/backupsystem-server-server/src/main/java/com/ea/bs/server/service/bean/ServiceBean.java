package com.ea.bs.server.service.bean;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.ea.bs.api.web.ru.RuDTO;
import com.ea.bs.server.ru.dao.RuDAO;
import com.ea.bs.server.service.dao.ServiceDAO;
import com.ea.db.DBException;

@Stateless
public class ServiceBean implements ServiceBeanLocal {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ServiceBean.class);

	@EJB
	private RuDAO ruDAO; 
	
	@EJB
	private ServiceDAO serviceDAO; 
	
	@Resource
	SessionContext ctx;

	public Long creaNewService(Long idRu) throws DBException {
		return serviceDAO.creaNewService(idRu);
	}

	public void stopServiceForRu(Long idRu) throws DBException {
		RuDTO ruDto = ruDAO.getRemoteUnit(idRu);
		
		if(ruDto == null){
			throw new DBException("RU "+idRu+" not exists in server registry");
		}
		
		serviceDAO.stopServiceForRu(idRu);
	}
}
