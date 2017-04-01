package com.ea.bs.server.ru.facade;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.ea.bs.api.web.ru.RuDTO;
import com.ea.bs.server.ru.dao.RuDAO;
import com.ea.db.DBException;
import com.ea.jms.MessageConnection;

@Stateless
public class RuFacade implements RuFacadeLocal {

	private static final Logger log = Logger.getLogger(RuFacade.class);

	@EJB
	private RuDAO ruDAO; 

	@Resource
	private SessionContext ctx;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Long createRemoteUnit(RuDTO dto) throws DBException {
		return ruDAO.creaRemoteUnit(dto);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void removeRemoteUnit(Long id) throws DBException{
		ruDAO.removeRemoteUnit(id);
	}
	
	public RuDTO getRemoteUnitServer(long idRu) throws DBException {
		return ruDAO.getRemoteUnit(idRu);
	}
	
	public MessageConnection getMessageConnectionRu(long idRu) throws DBException {
		MessageConnection conn = buildMessageConnection();
		log.trace("MessageConnection for idRu: "+idRu+" from DB: "+conn);
		return conn;
	}
	
	private MessageConnection buildMessageConnection() {
		MessageConnection conn = new MessageConnection();
//		conn.setInitialContext("org.jboss.naming.remote.client.InitialContextFactory");
//		conn.setUrlPkgPrefixes("org.jboss.ejb.client.naming");
		//conn.setProtocol("remote://");
		//conn.setHost("192.168.1.5");
		//conn.setPort(5445);
		//conn.setPort(4447);
		conn.setTimeout(10*1000);
		//conn.setReadTimeout(10*1000);
//		conn.setConnectionFactory("ConnectionFactory");
		conn.setUsername("testuser");
		conn.setPassword("testpwd");		
		return conn;
	}

}