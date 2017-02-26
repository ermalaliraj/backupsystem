package com.ea.bs.server.config;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;

@Singleton
@Startup
@LocalBean
public class ServerConfig {
	
	private static final Logger log = Logger.getLogger(ServerConfig.class);

	@Resource
	private SessionContext ctx;

	@Resource(name = "SERVER_CONFIG_PATH")
	private String configPath; // if we want to use an external xml (when server in cluster)

	private boolean cryptedData;
	private byte[] keyStringSalt;

	@Resource(name = "BACKUP_PATH")
	final private String BACKUP_PATH = "D:/backupsystem/server/BACKUP_FOLDER";
	
	@PostConstruct
	public void init(){
		log.info("[SERVER] started with following configuration:");
		log.info("[SERVER] configPath: "+configPath);
		log.info("[SERVER] cryptedData: "+cryptedData);
		log.info("[SERVER] keyStringSalt.length: "+((keyStringSalt!=null ? keyStringSalt.length : 0)));
		log.info("[SERVER] BACKUP_PATH: "+BACKUP_PATH);
	}

	public String getBackupPath() {
		return BACKUP_PATH;
	}

	public boolean isCryptedData() {
		return cryptedData;
	}

	public void setCryptedData(boolean cryptedData) {
		this.cryptedData = cryptedData;
	}

	public byte[] getKeyStringSalt() {
		return keyStringSalt;
	}

	public void setKeyStringSalt(byte[] keyStringSalt) {
		this.keyStringSalt = keyStringSalt;
	}

}
