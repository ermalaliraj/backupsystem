package com.ea.bs.protocol.cmd.message.ru;

import com.ea.bs.protocol.cmd.message.RuMsgInfo;
import com.ea.bs.protocol.cmd.message.CommandMessageType.RUMessageType;
import com.ea.jms.Message;

public class RuDetailMessage extends Message{
	
	private static final long serialVersionUID = -1788335321313017196L;
	private Long idRu;
	private RuMsgInfo ruMsgInfo;
	
	public RuDetailMessage(Long idRu, RuMsgInfo ruMsgInfo) {
		super(RUMessageType.SEND_STATUS.name());
		this.idRu = idRu;
		this.ruMsgInfo = ruMsgInfo;
	}
	
	public Long getIdRu() {
		return idRu;
	}
	public void setIdRu(Long idRu) {
		this.idRu = idRu;
	}
	public RuMsgInfo getRuMsgInfo() {
		return ruMsgInfo;
	}
	public void setRuMsgInfo(RuMsgInfo ruMsgInfo) {
		this.ruMsgInfo = ruMsgInfo;
	}

}
