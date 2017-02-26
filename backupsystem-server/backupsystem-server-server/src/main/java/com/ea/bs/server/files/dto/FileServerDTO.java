package com.ea.bs.server.files.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.ea.bs.server.files.type.StateFileType;

public class FileServerDTO {

	private long idService;
	private long idRu;
	private Date date;
	private String fileName;
	private byte[] fileBytes;
	private StateFileType stateFile;
	
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
	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}
	public StateFileType getStateFile() {
		return stateFile;
	}
	public void setStateFile(StateFileType stateFile) {
		this.stateFile = stateFile;
	}
	public long getIdRu() {
		return idRu;
	}
	public void setIdRu(long idRu) {
		this.idRu = idRu;
	}
	
	public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        	.appendSuper(super.toString())
        	.append("fileName", fileName)
        	.append("hasContent?", ((fileBytes!=null && fileBytes.length > 0) ? true : false))
        	.append("stateFile", stateFile)
        	.append("idService", idService)
        	.append("idRu", idRu)
        	.append("date", date)
        	.toString();
    }
	
}
