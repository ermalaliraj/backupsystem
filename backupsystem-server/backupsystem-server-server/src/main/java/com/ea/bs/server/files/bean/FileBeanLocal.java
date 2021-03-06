package com.ea.bs.server.files.bean;

import java.util.List;

import javax.ejb.Local;

import com.ea.bs.server.files.dto.FileServerDTO;
import com.ea.db.DBException;

@Local
public interface FileBeanLocal {

	public void saveFile(FileServerDTO dto) throws DBException;

	public void saveFile(List<FileServerDTO> fileDtoList) throws DBException;
	
	public void deleteFile(List<FileServerDTO> fileDtoList) throws DBException;

	public FileServerDTO decryptFile(FileServerDTO dto) throws Exception;

}
