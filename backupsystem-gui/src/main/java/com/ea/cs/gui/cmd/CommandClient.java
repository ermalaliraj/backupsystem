package com.ea.cs.gui.cmd;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.ea.bs.api.web.cmd.remote.RuCommandRemote;
import com.ea.bs.api.web.ru.RuDTO;
import com.ea.bs.api.web.ru.StatusRu;

public class CommandClient {

	private static final Logger log = Logger.getLogger(CommandClient.class);

	public static void main(String[] args) {
		log.debug("Starting Client...");
		try {
			Context context = getContextEnvJboss7();
			String lookupString = getLookupString("RuCommand", RuCommandRemote.class.getName());
			log.debug("remote ejb: "+lookupString);
	        RuCommandRemote remote  = (RuCommandRemote) context.lookup(lookupString);
	        
	        Long id = 1L;
   	//createRu(remote, id);
	        //remote.removeRemoteUnit(id);
//			log.debug("RU: " + id + " removed!");
	        
	  remote.getSampleFiles(id);
	        
	        RuDTO status = remote.getStatus(id);
	        log.debug("RU Status from server: "+status);
	        
//	       remote.avviaServizio(id);
//	        log.debug("Servizio avviato per RU "+id);
//	        RuDTO status2 = remote.getStatus(id);
//	        log.debug("RU Status (2) from server: "+status2);
	        
	  //      remote.stopService(id);
//	        log.debug("STOPPED service in RU "+id);
//	        RuDTO status3 = remote.getStatus(id);
//	     	log.debug("RU Status (3) from server: "+status3);
	        
		//remote.avviaServizio(id);
//			log.debug("Servizio avviato (2) per RU " + id);
//			RuDTO status2a = remote.getStatus(id);
//			log.debug("RU Status (2a) from server: " + status2a);
		} catch (NamingException e) {
			log.error("NamingException: "+e.getMessage(), e);
		} catch (Exception e) {
			log.error("General Exception: "+e.getMessage());
		}
	}

	protected static void createRu(RuCommandRemote remote, Long id) {
		RuDTO ru = new RuDTO();
		ru.setIdRu(id);
		ru.setName("Ru1");
		ru.setDescription("RU adress street xxx, nr. 5, Florence");
		ru.setStatus(StatusRu.AVAILABLE);
		
        remote.crateNewRu(ru);
        log.debug("Inserted RU: "+id);
	}

	
	
	// Get string to lookup for Remote interface in Jboss7
	// ejb:<app-name>/<module-name>/<distinct-name>/<bean-name>!<fully-qualified-classname-of-the-remote-interface>
	protected static String getLookupString(String beanName, String fullRemoteName) {
		//final String prefix = "ejb:";
		final String prefix = "/";
		final String appName = "backupsystem-server-ear";
		//final String appName = "backupsystem-ear-1.0.0-SNAPSHOT";
		final String moduleName = "backupsystem-server-server";
		final String distinctName = "";
//		final String beanName = "RuCommand";
//		final String viewClassName = RuCommandRemote.class.getName();
		String lookupString = prefix + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + fullRemoteName;
		return lookupString;
	}
	
	protected static Context getContextEnvJboss7() throws NamingException {
		String host = "127.0.0.1";
		String port = "8080";
		try {
			Properties properties = new Properties();
			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			//properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			properties.put(Context.PROVIDER_URL, "http-remoting://" + host + ":" + port);
			properties.put("jboss.naming.client.ejb.context", "true");
			properties.put(Context.SECURITY_PRINCIPAL, "adminapp");
			properties.put(Context.SECURITY_CREDENTIALS, "adminpwd");
			
			// deactivate authentication
			// properties.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT","false");
			// properties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS","false");
			Context context = new InitialContext(properties);
			log.debug("context: "+context.getEnvironment());
			return context;
		} catch (NamingException e) {
			log.error("Error creating InitialContext", e);
			throw e;
		}
	}
}
