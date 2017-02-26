package com.ea.bs.protocol.cmd.message.server;

import java.util.Date;

import com.ea.bs.protocol.cmd.message.CommandMessageType;
import com.ea.jms.Message;

public class StartServiceMessage extends Message{

	private static final long serialVersionUID = -484757747233472644L;

	private long idRu;
	private  Date startDate;
	
	public StartServiceMessage(long idRu, Date startDate) {
		super(CommandMessageType.ServerMessageType.START_SERVICE.name());
		this.idRu = idRu;
		this.startDate = startDate;
	}
	
	public long getIdRu() {
		return idRu;
	}
	public void setIdRu(long idRu) {
		this.idRu = idRu;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
