package com.ea.bs.server.config;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.ea.util.IOUtils;
import com.ea.util.StringUtils;

@Singleton
@Startup
@LocalBean
public class ServerConfig {
	
	private static final Logger log = Logger.getLogger(ServerConfig.class);

	@Resource
	private SessionContext ctx;

	@Resource(name = "SERVER_CONFIG_PATH")
	private String serverConfigPath;
	
	private ServerConfigDescriptor serverDescriptor;
	
	@PostConstruct
	public void init() throws Exception{
		log.info("[SERVER] INIT...loading xml config file");
		String configPath = null; 
		try {
			String xmlFile = "server.xml";
			String path = System.getProperty("server.configpath.dir");
			if(StringUtils.isEmptyString(path)){
				log.warn("[SERVER] 'server.configpath.dir' not configured in standalone.xml. Adding windows test path");
				path = "D:/bs/server/config";
			} else{
				log.debug("[SERVER] Config path configured in standalone.xml: "+path);
			}
			configPath = path + "/" + xmlFile;
			
			if(!IOUtils.existsFile(configPath)){
				log.warn("[SERVER] Path: "+configPath +" don't exists!");
				log.warn("[SERVER] Trying to get xml config file location from value of ejb-jar.xml=>SERVER_CONFIG_PATH: '"+serverConfigPath+"'");
				
				if(!StringUtils.isEmptyString(serverConfigPath)){
					log.debug("[SERVER] SERVER_CONFIG_PATH found in ejb-jar.xml");
					if(!IOUtils.existsFile(serverConfigPath)){
						log.fatal("[SERVER] XML config file didn't found at path:"+serverConfigPath+". Fatal situation! Exiting...");
						throw new Exception("SERVER Configuration didn't found at path: "+serverConfigPath); 
					}
					configPath = serverConfigPath;
				} else {
					log.fatal("[SERVER] SERVER_CONFIG_PATH not configured in ejb-jar.xml. Fatal situation! Exiting...");
					throw new Exception("SERVER Configuration didn't found, at path: "+serverConfigPath); 
				}
			}
		} catch (Exception e) {
			log.fatal("[SERVER] Fatal situation! Failed to get xml config path. configPath: "+configPath, e);
			throw new Exception("SEERVER Configuration didn't found at path: "+serverConfigPath); 
		}
		
		log.debug("[SERVER] XML config file: "+configPath);
		try {
			File configFile = new File(configPath);
			JAXBContext jaxbContext = JAXBContext.newInstance(ServerConfigDescriptor.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			serverDescriptor = (ServerConfigDescriptor) jaxbUnmarshaller.unmarshal(configFile);
		} catch (Exception e) {
			log.fatal("[SERVER] Couldn't create a ServerDescriptor from xml config file: "+configPath, e);
			throw new Exception("Couldn't create a ServerDescriptor from xml config file: "+configPath);
		}
		log.info("[SERVER] SERVER configDescriptor builded from xml file: "+serverDescriptor);
	}

	public ServerConfigDescriptor getServerDescriptor() {
		return serverDescriptor;
	}

}
