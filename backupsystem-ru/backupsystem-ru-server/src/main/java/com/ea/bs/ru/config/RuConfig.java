package com.ea.bs.ru.config;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;

import com.ea.jms.MessageConnection;
import com.ea.util.IOUtilities;

@Singleton
@Startup
@LocalBean
public class RuConfig {
	
	private static final Logger log = Logger.getLogger(RuConfig.class);

	@Resource
	private SessionContext ctx;
	
	private String pkcs11ImplementationPath;

	@Resource(name = "RU_CONFIG_PATH")
	private String configPath; //if we want to use an external xml (when server in cluster) 
	
	private long idRu;
	private String name;
	private String description;
	private boolean enabled;
	private boolean cryptedData;
	private boolean backupSent;
	
	private MessageConnection serverConnection;
	
	@Resource(name = "PATH_FILES_TO_SEND")
	final private String PATH_FILES_TO_BE_SEND = "D:/backupsystem/ru/FILES_TO_SEND";
	//final private String PATH_FILES_SOURCE = "D:/backupsystem/ru/SOURCEFILES";
	final private long MAX_NR_FILES_PER_MSG = 5;
	final private long MAX_SIZE_BYTE_PER_FILE = 500;
	final private long SEND_FILES_TIMEOUT = 30 * 1000L; // x secondi
	final private long SEND_FILES_FIRST_FIRE = 10 * 1000L; // x secondi
	final private String SEND_FILES_TIMER_INFO = "SEND_FILE_TO_SERVER_TIMER";
	
	@PostConstruct
	public void init() throws Exception {
		//read config data from xml file in pah configPath, and build config
		idRu = 1L;
		name = "First RU";
		description = "RU created with mock data only for test purpose";
		enabled = true;
		cryptedData = false;
		
		serverConnection = new MessageConnection();
		serverConnection.setInitialContext("org.jboss.naming.remote.client.InitialContextFactory");
		serverConnection.setUrlPkgPrefixes("org.jboss.ejb.client.naming");
		serverConnection.setProtocol("remote://");
		serverConnection.setHost("127.0.0.1");
		serverConnection.setPort(4447);
		serverConnection.setTimeout(10*1000);
		serverConnection.setReadTimeout(10*1000);
		//serverConnection.setConnectionFactory("ConnectionFactory");
		serverConnection.setUsername("adminapp");
		serverConnection.setPassword("adminpwd");		
		
		log.info("[RU] started with following configuration:");
		log.info("[RU] idRu: "+idRu);
		log.info("[RU] name: "+name);
		log.info("[RU] description: "+description);
		log.info("[RU] isEnabled: "+enabled);
		log.info("[RU] isCryptedData: "+cryptedData);

		log.info("[RU] PATH_FILES_TO_BE_SEND: "+PATH_FILES_TO_BE_SEND);
		log.info("[RU] MAX_NR_FILES_PER_MSG: "+MAX_NR_FILES_PER_MSG);
		log.info("[RU] MAX_SIZE_BYTE_PER_FILE: "+MAX_SIZE_BYTE_PER_FILE);
		log.info("[RU] SEND_FILES_TIMEOUT: "+SEND_FILES_TIMEOUT);
		log.info("[RU] SEND_FILES_FIRST_FIRE: "+SEND_FILES_FIRST_FIRE);
		log.info("[RU] SEND_FILES_TIMER_INFO: "+SEND_FILES_TIMER_INFO);
		
		log.info("[RU] serverConnection: "+serverConnection);
		
		
	}
	
	public String getPathWithFileToBeSend(){
		return IOUtilities.normalizePath(PATH_FILES_TO_BE_SEND);
	}
	
	public long getNrMaxFilesPerMsg(){
		return MAX_NR_FILES_PER_MSG;
	}
	
	public long getNrMaxSizePerFile(){
		return MAX_SIZE_BYTE_PER_FILE;
	}
	
	public long getSendFilesFirstFire(){
		return SEND_FILES_FIRST_FIRE;
	}
	
	public long getSendFilesTimeout(){
		return SEND_FILES_TIMEOUT;
	}
	
	public String getSendFilesTimerInfo(){
		return SEND_FILES_TIMER_INFO;
	}
	
	
	public MessageConnection getServerConnection() {
		return serverConnection;
	}

	public void setServerConnection(MessageConnection serverConnection) {
		this.serverConnection = serverConnection;
	}

	public long getIdRu() {
		return idRu;
	}

	public void setIdRu(long idRu) {
		this.idRu = idRu;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isCryptedData() {
		return cryptedData;
	}

	public void setCryptedData(boolean cryptedData) {
		this.cryptedData = cryptedData;
	}

	public boolean isBackupSent() {
		return backupSent;
	}

	public String getPkcs11ImplementationPath() {
		return pkcs11ImplementationPath;
	}

	public void setPkcs11ImplementationPath(String pkcs11ImplementationPath) {
		this.pkcs11ImplementationPath = pkcs11ImplementationPath;
	}
	
}
