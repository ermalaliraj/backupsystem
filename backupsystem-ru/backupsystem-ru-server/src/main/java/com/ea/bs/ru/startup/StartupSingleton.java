package com.ea.bs.ru.startup;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.bs.ru.cmd.dto.RuDTO;
import com.ea.bs.ru.cmd.dto.StatusRu;
import com.ea.bs.ru.cmd.facade.RuFacadeLocal;
import com.ea.db.DBException;

@Startup
@Singleton
public class StartupSingleton {
	
	private static final Log log = LogFactory.getLog(StartupSingleton.class);

	@EJB(name = "RuFacade")
	private RuFacadeLocal ruFacade;
	
	private Long idRu;

	@PostConstruct
	void init() {
		idRu = 1L;
		populateTestData();
		
		loadCertificates();
		log.info("[RU] Security Certifications loaded for idRU: "+idRu);
	}

	public Long getIdRu() {
		return idRu;
	}
	
	private void populateTestData() {
		try {
			log.info("[RU] Populating DB with test DATA for idRU: "+idRu);
			RuDTO dto = new RuDTO();
			dto.setIdRu(1L);
			dto.setDescription("Remote Unit id:1, IP:127.0.0.1, address: Via ponte di mezzo, 82 ");
			dto.setName("RemoteUnit Firenze");
			dto.setStatus(StatusRu.AVAILABLE);
			ruFacade.insertRemoteUnit(dto);
			
			dto = new RuDTO();
			dto.setIdRu(2L);
			dto.setDescription("Remote Unit id:2, IP:127.0.0.2, address: ");
			dto.setName("RemoteUnit Prato");
			dto.setStatus(StatusRu.NOT_DEFINED);
			
			dto = new RuDTO();
			dto.setIdRu(3L);
			dto.setDescription("Remote Unit id:3, IP:127.0.0.3, address: xxxx");
			dto.setName("RemoteUnit Siena");
			dto.setStatus(StatusRu.OUT_OF_ORDER);
			log.info("[RU] Test DATA inserted in DB for idRU: "+idRu);
		} catch (DBException e) {
			log.error("[RU] Error populating TEST data in DB", e);
		}
	}
	
	
	public void loadCertificates(){
		
	}

	
}
