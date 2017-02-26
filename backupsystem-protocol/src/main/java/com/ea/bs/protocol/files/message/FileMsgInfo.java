package com.ea.bs.protocol.files.message;

import java.io.Serializable;
import java.util.Date;

public class FileMsgInfo implements Serializable {

	private static final long serialVersionUID = -5596588601522184224L;
	
	private long idService;
	private Date date;
	private String fileName;
	private byte[] fileBytes;
	
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
}
