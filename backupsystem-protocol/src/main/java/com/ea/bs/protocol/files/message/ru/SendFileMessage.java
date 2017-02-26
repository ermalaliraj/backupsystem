package com.ea.bs.protocol.files.message.ru;

import java.util.List;

import com.ea.bs.protocol.files.message.FileMessageType;
import com.ea.bs.protocol.files.message.FileMsgInfo;
import com.ea.jms.Message;

public class SendFileMessage extends Message{
	
	private static final long serialVersionUID = 7324962302846807601L;
	
	private long idRu;
	private List<FileMsgInfo> files = null;
	
	public SendFileMessage(long idRu, List<FileMsgInfo> files) {
		super(FileMessageType.RUMessageType.SEND_FILE.name());
		this.idRu = idRu;
		this.files = files;
	}

	public long getIdRu() {
		return idRu;
	}
	public void setIdRu(long idRu) {
		this.idRu = idRu;
	}
	public List<FileMsgInfo> getFiles() {
		return files;
	}
	public void setFiles(List<FileMsgInfo> files) {
		this.files = files;
	}

}
