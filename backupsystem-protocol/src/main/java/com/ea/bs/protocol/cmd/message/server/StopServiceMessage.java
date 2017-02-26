package com.ea.bs.protocol.cmd.message.server;

import java.util.Date;

import com.ea.bs.protocol.cmd.message.CommandMessageType;
import com.ea.jms.Message;

public class StopServiceMessage extends Message {

	private static final long serialVersionUID = 4505091123280106664L;
	private long idRu;
	private  Date stopDate;
	
	public StopServiceMessage(long idRu, Date stopDate) {
		super(CommandMessageType.ServerMessageType.STOP_SERVICE.name());
		this.idRu = idRu;
		this.stopDate = stopDate;
	}
	
	public long getIdRu() {
		return idRu;
	}
	public void setIdRu(long idRu) {
		this.idRu = idRu;
	}
	public Date getStopDate() {
		return stopDate;
	}
	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}
}
