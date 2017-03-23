package com.ea.bs.server.cmd.sender;

import java.util.List;

import javax.ejb.Local;

import com.ea.bs.protocol.cmd.message.RuMsgInfo;
import com.ea.bs.protocol.files.message.FileMsgInfo;
import com.ea.jms.Message;
import com.ea.jms.exception.MessageException;

@Local
public interface CommandSenderLocal {

	public RuMsgInfo getRuDetail(long idRu) throws MessageException;
	
	public void startService(long idRu) throws MessageException;

	public void stopService(long idRu) throws MessageException;

	public List<FileMsgInfo> getSampleFiles(long idRu) throws MessageException;

	public void pingAsynch(long idRu) throws MessageException;
	
	public Message pingSynch(long idRu) throws MessageException;

}
