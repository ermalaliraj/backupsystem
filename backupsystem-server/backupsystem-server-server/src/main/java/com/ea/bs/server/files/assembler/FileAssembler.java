package com.ea.bs.server.files.assembler;

import com.ea.bs.protocol.files.message.FileMsgInfo;
import com.ea.bs.server.files.dto.FileServerDTO;
import com.ea.bs.server.files.type.StateFileType;

public class FileAssembler {

	public static FileServerDTO getFileDTO(long idRu, FileMsgInfo fileMsg) {
		FileServerDTO fileDTO = new FileServerDTO();
		fileDTO.setIdRu(idRu);
		fileDTO.setFileName(fileMsg.getFileName());
		fileDTO.setFileBytes(fileMsg.getFileBytes());
		fileDTO.setStateFile(StateFileType.DOWNLOADED);
		fileDTO.setIdService(fileMsg.getIdService());
		fileDTO.setDate(fileMsg.getDate());
		return fileDTO;
	}

}
