package com.ea.bs.ru.files.facade;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.ea.bs.ru.files.dao.FileDAO;
import com.ea.bs.ru.files.dto.FileRuDTO;
import com.ea.db.DBException;

@Stateless
public class FileFacade implements FileFacadeLocal {

	private static final Logger log = Logger.getLogger(FileFacade.class);

	@Resource
	SessionContext ctx;

	@EJB(name = "FileDAO")
	private FileDAO fileDAO;

	public void saveFile(FileRuDTO fileDTO, String path, boolean isCrypted) throws DBException, IOException, GeneralSecurityException {
		log.debug("[RU] Saving fileDTO: "+fileDTO);
		
	}

	public void saveFile(List<FileRuDTO> fileList, String path, boolean isCrypted) throws DBException, IOException, GeneralSecurityException {
		log.debug("[RU] Saving " + fileList.size() + " files in DB");
		for (FileRuDTO fileDTO : fileList) {
			saveFile(fileDTO, path, isCrypted);
		}
	}

	public void deleteFile(List<FileRuDTO> dtoList) throws DBException {
	}

}
