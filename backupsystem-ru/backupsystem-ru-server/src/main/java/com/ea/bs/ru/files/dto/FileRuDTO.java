package com.ea.bs.ru.files.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.ea.bs.ru.files.type.StateFileType;

public class FileRuDTO {

	private long idService;
	private Date date;
	private String filePath;
	private String fileName;
	private byte[] fileBytes;
	private StateFileType stateFile;
	private String cryptedKey;
	private String description;
	
	public long getIdService() {
		return idService;
	}
	public void setIdService(long idService) {
		this.idService = idService;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public byte[] getFileBytes() {
		return fileBytes;
	}
	public void setFileBytes(byte[] fileData) {
		this.fileBytes = fileData;
	}
	public StateFileType getStateFile() {
		return stateFile;
	}
	public void setStateFile(StateFileType stateFile) {
		this.stateFile = stateFile;
	}
	public String getCryptedKey() {
		return cryptedKey;
	}
	public void setCryptedKey(String cryptedKey) {
		this.cryptedKey = cryptedKey;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        	.appendSuper(super.toString())
        	.append("fileName", fileName)
        	.append("filePath", filePath)
        	.append("hasContent?", ((fileBytes!=null && fileBytes.length > 0) ? true : false))
        	.append("stateFile", stateFile)
        	.append("cryptedKey", cryptedKey)
        	.append("description", description)
        	.append("idService", idService)
        	.append("date", date)
        	.toString();
    }

}
