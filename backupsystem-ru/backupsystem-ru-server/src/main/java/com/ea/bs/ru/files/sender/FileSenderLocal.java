package com.ea.bs.ru.files.sender;

import java.util.List;

import javax.ejb.Local;

import com.ea.bs.protocol.files.message.FileMsgInfo;
import com.ea.jms.exception.MessageException;

@Local
public interface FileSenderLocal {

	void sendFiles(List<FileMsgInfo> listMsg) throws MessageException ;


}
