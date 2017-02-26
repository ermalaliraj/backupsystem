package com.ea.bs.ru.files.bean;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.ejb.Local;

import com.ea.bs.ru.files.dto.FileRuDTO;
import com.ea.db.DBException;

@Local
public interface FileBeanLocal {

	public void saveFile(FileRuDTO dto, String path, boolean isCrypted) throws DBException, IOException, GeneralSecurityException;

	public void saveFile(List<FileRuDTO> dtoList, String path, boolean isCrypted) throws DBException, IOException, GeneralSecurityException;
	
	public void deleteFile(List<FileRuDTO> dtoList) throws DBException;

}
