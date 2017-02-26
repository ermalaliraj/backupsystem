package com.ea.bs.protocol.cmd.message;

import java.io.Serializable;

public class RuMsgInfo implements Serializable {
	
	private static final long serialVersionUID = -6824654504113928179L;
	
	public enum StatusRuType {
		AVAILABLE, BUSY, OUT_OF_ORDER
	}

	private long idRu;
	private StatusRuType status;
	
	public long getIdRu() {
		return idRu;
	}
	public void setIdRu(long idRu) {
		this.idRu = idRu;
	}

	public StatusRuType getStatus() {
		return status;
	}
	public void setStatus(StatusRuType status) {
		this.status = status;
	}
	
}
