package com.ea.bs.ru.files.dao;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.bs.ru.dao.GenericDAO;

@Stateless
public class FileDAOImpl extends GenericDAO implements FileDAO {
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(FileDAOImpl.class);


}
