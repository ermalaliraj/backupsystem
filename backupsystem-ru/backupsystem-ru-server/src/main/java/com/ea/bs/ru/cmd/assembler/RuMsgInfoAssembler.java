package com.ea.bs.ru.cmd.assembler;

import com.ea.bs.protocol.cmd.message.RuMsgInfo;
import com.ea.bs.ru.cmd.dto.RuDTO;


public class RuMsgInfoAssembler {

	public static RuMsgInfo getStatusMsgFromDTO(RuDTO dto) {
		RuMsgInfo msgInfo = new RuMsgInfo();
		msgInfo.setIdRu(dto.getIdRu());
		
		RuMsgInfo.StatusRuType status = null;
		switch (dto.getStatus()) {
		case AVAILABLE:
			status = RuMsgInfo.StatusRuType.AVAILABLE;
			break;
		case BUSY:
			status = RuMsgInfo.StatusRuType.BUSY;
			break;
		case NOT_DEFINED:
			//status = StatusRuMsgDTO.StatusRuType.OUT_OF_ORDER;
			break;
		case OUT_OF_ORDER:
			//status = StatusRuMsgDTO.StatusRuType.OUT_OF_ORDER;
			break;
		default:
			break;
		}
		msgInfo.setStatus(status);
		return msgInfo;
	}
}
