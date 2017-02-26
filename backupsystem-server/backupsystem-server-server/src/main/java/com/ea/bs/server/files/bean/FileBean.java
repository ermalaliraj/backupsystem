package com.ea.bs.server.files.bean;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ea.bs.server.files.dto.FileServerDTO;
import com.ea.db.DBException;

@Stateless
public class FileBean implements FileBeanLocal {

	private static final Logger log = Logger.getLogger(FileBean.class);

	//@Resource(mappedName = "java:/jdbc/BSServerDS")
	private DataSource myDS;

//	@Resource(mappedName = "jms/queue/scapture")
//	protected Destination destination;

	@Resource
	SessionContext ctx;

//	@EJB(name = "ServerConfigurator")
//	private ServerConfiguratorLocal serverConfigurator;

	public void saveFile(FileServerDTO dto) throws DBException {
	}

	public void saveFile(List<FileServerDTO> fileDtoList) throws DBException {
		log.debug("Savind " + fileDtoList.size() + " files in DB");

//		if (fileList.size() > 0) {
//			FileDAO FileDAO = new FileDAO(myDS);
//
//			byte[] KeyStringSaltByte = serverConfigurator.getSecurityConfig().getKeyStringSalt();
//			Key key = CryptUtilities.getKeySpec(KeyStringSaltByte);
//
//			for (FileDTO file : fileList) {
//				try {
//					byte[] encryptedTarga = CryptUtilities.encryptAES(key, Base64.encode(file.getFileName()).getBytes());
//					file.setFileName(new String(Base64.encode(encryptedTarga)));
//					FileDAO.inserisciTransitoDaAccoppiare(file);
//				} catch (Exception e) {
//					log.error("Impossibile inserire il transito da accoppiare --> " + file.toString(), e);
//				}
//			}
//		}
//		log.debug("Inseriti " + fileList.size() + " transiti da accoppiare");
	}

	public void deleteFile(List<FileServerDTO> fileDtoList) throws DBException {
	}

	public FileServerDTO decryptFile(FileServerDTO dto) throws Exception {
		return null;
	}

}
