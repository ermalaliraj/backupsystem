package com.ea.cs.gui.cmd;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.ea.bs.api.web.cmd.exception.CannotStartServiceException;
import com.ea.bs.api.web.cmd.exception.CannotStopServiceException;
import com.ea.bs.api.web.cmd.exception.RuNotPresentInRegistryException;
import com.ea.bs.api.web.cmd.exception.ServiceAlreadyRunningException;
import com.ea.bs.api.web.cmd.exception.ServiceAlreadyStoppedException;
import com.ea.bs.api.web.cmd.remote.RuCommandRemote;
import com.ea.bs.api.web.exception.ServerException;
import com.ea.bs.api.web.ru.RuDTO;
import com.ea.bs.api.web.ru.StatusRu;

public class CommandClient {

	private static final Logger log = Logger.getLogger(CommandClient.class);

	public static void main(String[] args) {
		log.debug("Starting Client...");
		try {
			Context context = getContextEnvJboss7();
			String lookupString = getLookupString("RuCommand", RuCommandRemote.class.getName());
	        RuCommandRemote remote  = (RuCommandRemote) context.lookup(lookupString);
	        
	        Long ruId = 1L;
	        
	   //     ping(remote, ruId);
//	        pingSynch(remote, ruId);
//	        getSample(remote, ruId);
	        
	        createRu(remote, ruId);
//	        removeRu(remote, ruId);
			avviaServizio(remote, ruId);
//	        stopService(remote, ruId);
	        
	     //   getStatus(remote, ruId);
	        
	      log.debug("End client");
		} catch (NamingException e) {
			log.error("NamingException: "+e.getMessage(), e);
		} catch (Exception e) {
			log.error("General Exception: "+e.getMessage(), e);
		}
	}
	
	private static void getStatus(RuCommandRemote remote, Long ruId) throws ServerException, CannotStopServiceException, ServiceAlreadyStoppedException, RuNotPresentInRegistryException {
		RuDTO status = remote.getStatus(ruId);
     	log.debug("RU Status from server: "+status);
	}

	private static void stopService(RuCommandRemote remote, Long ruId) throws ServerException, CannotStopServiceException, ServiceAlreadyStoppedException, RuNotPresentInRegistryException {
		remote.stopService(ruId);
        log.debug("STOPPED service in RU "+ruId);
        RuDTO status3 = remote.getStatus(ruId);
     	log.debug("RU Status (3) from server: "+status3);
	}

	private static void avviaServizio(RuCommandRemote remote, Long ruId) throws ServerException, CannotStartServiceException, ServiceAlreadyRunningException, RuNotPresentInRegistryException {
	    remote.avviaServizio(ruId);
        log.debug("Servizio avviato per RU "+ruId);
        RuDTO status2 = remote.getStatus(ruId);
        log.debug("RU Status (2) from server: "+status2);
	}

	private static void getSample(RuCommandRemote remote, Long ruId) {
		remote.getSampleFiles(ruId);
        log.debug("SampleFiles sent to RU: "+ruId+" .Check backup folder for new files from RU");
	}

	private static void removeRu(RuCommandRemote remote, Long ruId) {
        remote.removeRemoteUnit(ruId);
		log.debug("RU: " + ruId + " removed!");
	}

	private static void pingSynch(RuCommandRemote remote, Long ruId) {
        String ret = remote.pingSynch(ruId);
        log.debug("PING SYNCH sent to RU: "+ruId+" returned from RU: "+ret);
	}

	private static void ping(RuCommandRemote remote, Long ruId) {
        remote.pingAsynch(ruId);
        log.debug("PING sent to RU: "+ruId);
	}

	protected static void createRu(RuCommandRemote remote, Long id) throws ServerException, RuNotPresentInRegistryException {
		RuDTO ru = new RuDTO();
		ru.setIdRu(id);
		ru.setName("Ru1");
		ru.setDescription("RU adress street xxx, nr. 5, Florence");
		ru.setStatus(StatusRu.AVAILABLE);
		
        remote.crateNewRu(ru);
        log.debug("Inserted RU: "+id);
        
        RuDTO status2 = remote.getStatus(id);
        log.debug("RU Status (2) from server: "+status2);
	}

	
	
	// Get string to lookup for Remote interface in Jboss7
	// ejb:<app-name>/<module-name>/<distinct-name>/<bean-name>!<fully-qualified-classname-of-the-remote-interface>
	protected static String getLookupString(String beanName, String fullRemoteName) {
		final String appName = "backupsystem-server-ear";
		//final String appName = "backupsystem-ear-1.0.0-SNAPSHOT";
		final String moduleName = "backupsystem-server-server";
		final String distinctName = "";
//		final String beanName = "RuCommand";
//		final String viewClassName = RuCommandRemote.class.getName();
		// se specificato  "org.jboss.ejb.client.naming"
		String lookupString = "ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + fullRemoteName;
		//String lookupString = "" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + fullRemoteName;
		return lookupString;
	}
	
	protected static Context getContextEnvJboss7() throws NamingException {
		//String host = "127.0.0.1";
		String host = "bs-server";
		String port = "4447";
		try {
			Properties properties = new Properties();
			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");  //senza questa riga, lookup: "" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + fullRemoteName;
			properties.put(Context.PROVIDER_URL, "remote://" + host + ":" + port);
			properties.put("jboss.naming.client.ejb.context", "true");
			properties.put(Context.SECURITY_PRINCIPAL, "testuser");
			properties.put(Context.SECURITY_CREDENTIALS, "testpwd");
			// deactivate authentication
			// properties.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT","false");
			// properties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS","false");
			Context context = new InitialContext(properties);
			return context;
		} catch (NamingException e) {
			log.error("Error creating InitialContext", e);
			throw e;
		}
	}
}
