package com.ea.bs.ru.config;

import java.io.File;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.ea.jms.MessageConnection;
import com.ea.util.IOUtils;
import com.ea.util.StringUtils;

@Singleton
@Startup
@LocalBean
public class RuConfig {
	
	private static final Logger log = Logger.getLogger(RuConfig.class);

	@Resource
	private SessionContext ctx;

	@Resource(name = "RU_CONFIG_PATH")
	private String ruConfigPath;
	
	private RuConfigDescriptor configDescriptor;

	private MessageConnection serverConnection;
		
	@PostConstruct
	public void init() throws Exception {
		log.info("[RU] INIT...loading xml config file");
		String configPath = null; 
		try {
			String xmlFile = "ru.xml";
			String path = System.getProperty("ru.configpath.dir");//bs/ru/config in standalone.xml
			if(StringUtils.isEmptyString(path)){
				log.warn("'ru.configpath.dir' not configured in standalone.xml. Adding windows test prefix");
				path = "D:/bs/ru/config";
			} else{
				log.debug("Config path configured in standalone.xml: "+path);
			}
			configPath = path + "/" + xmlFile;
			
			if(!IOUtils.existsFile(configPath)){
				log.warn("Path: "+configPath +" don't exists!");
				log.warn("Trying to get xml config file location from value of ejb-jar.xml=>RU_CONFIG_PATH: '"+ruConfigPath+"'");
				
				if(!StringUtils.isEmptyString(ruConfigPath)){
					log.debug("RU_CONFIG_PATH found in ejb-jar.xml");
					if(!IOUtils.existsFile(ruConfigPath)){
						log.fatal("XML config file didn't found at path:"+ruConfigPath+". Fatal situation! Exiting...");
						throw new Exception("RU Configuration didn't found at path: "+ruConfigPath); 
						//buildFakeConfigDescriptor();
					}
					configPath = ruConfigPath;
				} else {
					log.fatal("RU_CONFIG_PATH not configured in ejb-jar.xml. Fatal situation! Exiting...");
					throw new Exception("RU Configuration didn't found, at path: "+ruConfigPath); 
					//buildFakeConfigDescriptor();
				}
			}
		} catch (Exception e) {
			log.fatal("Fatal situation! Failed to get xml config path. configPath: "+configPath, e);
			throw new Exception("RU Configuration didn't found at path: "+ruConfigPath); 
			//buildFakeConfigDescriptor();
		}
		
		log.debug("XML config file: "+configPath);
		try {
			File configFile = new File(configPath);
			JAXBContext jaxbContext = JAXBContext.newInstance(RuConfigDescriptor.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			configDescriptor = (RuConfigDescriptor) jaxbUnmarshaller.unmarshal(configFile);
		} catch (Exception e) {
			log.fatal("Couldn't create a RuDescriptor from xml config file: "+configPath, e);
			throw new Exception("Couldn't create a RuDescriptor from xml config file: "+configPath);
			//buildFakeConfigDescriptor();
		}
		log.info("RU configDescriptor builded from xml file: "+configDescriptor);
		
		buildServerConnection();
		log.info("[RU] serverConnection: "+serverConnection);
	}


//ru.xml file content	
//<RuConfigDescriptor>
//	<idRu>1</idRu>
//	<name>RU 1</name>
//	<description>RU prova 1</description>
//	<enabled>true</enabled>
//	<cryptedData>false</cryptedData>
//	<backupSent>false</backupSent>
//	<pkcs11ImplementationPath></pkcs11ImplementationPath>
//		
//	<pathWithFilesToBeSend>D:/backupsystem/ru/FILES_TO_SEND</pathWithFilesToBeSend>
//	<maxNrFilesPerMsg>5</maxNrFilesPerMsg>
//	<maxSizeBytePerFile>500</maxSizeBytePerFile>
//	<sendFilesTimeout>30 * 1000L</sendFilesTimeout>
//	<sendFilesFirstFire>10 * 1000L</sendFilesFirstFire>
//	<sendFilesTimerInfo>SEND_FILE_TO_SERVER_TIMER</sendFilesTimerInfo>
//</RuConfigDescriptor>
	
	private void buildServerConnection() {
		serverConnection = new MessageConnection();
//		serverConnection.setInitialContext("org.jboss.naming.remote.client.InitialContextFactory");
//		serverConnection.setUrlPkgPrefixes("org.jboss.ejb.client.naming");
//		serverConnection.setProtocol("remote://");
		serverConnection.setHost("bs-server");
		serverConnection.setPort(4447);
		serverConnection.setTimeout(10*1000);
//		serverConnection.setReadTimeout(10*1000);
		//serverConnection.setConnectionFactory("ConnectionFactory");
		serverConnection.setUsername("adminapp");
		serverConnection.setPassword("adminpwd");		
		log.info("[RU] serverConnection: "+serverConnection);
	}
	
	
//	String propFile = "/ruMapConfig.properties";
//	String path = System.getProperty("ru.configpath.dir") + propFile ;//bs/ru/config
//	
//	// Get ru.xml path from .properties file 
//	Properties prop = new Properties();
//	prop.load(RuConfig.class.getClassLoader().getResourceAsStream("/ruMapConfig.properties"));
//	log.debug("/ruMapConfig.properties found inside classloader. Reading 'ruConfigPath' value which must contains xml config path/file");
//	configPath = prop.getProperty("ruConfigPath");

	public MessageConnection getServerConnection() {
		return serverConnection;
	}

	public void setServerConnection(MessageConnection serverConnection) {
		this.serverConnection = serverConnection;
	}

	public RuConfigDescriptor getConfigDescriptor() {
		return configDescriptor;
	}

	public void setConfigDescriptor(RuConfigDescriptor configDescriptor) {
		this.configDescriptor = configDescriptor;
	}

}
