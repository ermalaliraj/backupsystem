package com.ea.bs.server.cmd.assembler;

import com.ea.bs.api.web.ru.RuDTO;
import com.ea.bs.api.web.ru.StatusRu;
import com.ea.bs.protocol.cmd.message.RuMsgInfo;

public class CommandAssembler {

	public static RuDTO getStatusRuDTO(long idRu, RuMsgInfo msg) {
		RuDTO dto = new RuDTO ();
		dto.setIdRu(idRu);
		StatusRu status = StatusRu.NON_DEFINITO;
		if(msg.getStatus() != null){
			switch (msg.getStatus()) {
			case AVAILABLE:
				status = StatusRu.AVAILABLE;
				break;
			case BUSY:
				status = StatusRu.BUSY;
				break;
			case OUT_OF_ORDER:
				status = StatusRu.OUT_OF_ORDER;
				break;
			default:
				status = StatusRu.NON_DEFINITO;
				break;
			}
		}
		dto.setStatus(status);
		return dto;
	}

}
