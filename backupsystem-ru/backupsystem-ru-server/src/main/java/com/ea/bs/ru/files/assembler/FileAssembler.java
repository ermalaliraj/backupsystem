package com.ea.bs.ru.files.assembler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ea.bs.protocol.files.message.FileMsgInfo;
import com.ea.bs.ru.files.dto.FileRuDTO;
import com.ea.util.IOUtilities;

public class FileAssembler {

	public static FileRuDTO getFileDTO(long idRu, FileMsgInfo fileMsgDTO) {
		FileRuDTO fileRuDTO = new FileRuDTO();
		
		return fileRuDTO;
	}

	public static List<FileRuDTO> getFileDTOFromPhysicalFile(List<File> files, Long idService) throws IOException {
		List<FileRuDTO> list = new ArrayList<FileRuDTO>();
		for (File f : files) {
			FileRuDTO dto = new FileRuDTO();
			dto.setDate(new Date());
			dto.setFileName(f.getName());
			dto.setIdService(idService);
			dto.setFileBytes(IOUtilities.getFileContent(f));
			list.add(dto);
		}
		return list;
	}

	public static FileMsgInfo getFileMsg(FileRuDTO fileDTO) {
		FileMsgInfo msg = new FileMsgInfo();
		msg.setFileName(fileDTO.getFileName());
		msg.setFileBytes(fileDTO.getFileBytes());
		msg.setIdService(fileDTO.getIdService());
		msg.setDate(fileDTO.getDate());
		return msg;
	}

}
