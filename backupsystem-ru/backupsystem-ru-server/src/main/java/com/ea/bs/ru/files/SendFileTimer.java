package com.ea.bs.ru.files;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ea.bs.ru.config.RuConfig;

@Singleton
@LocalBean
public class SendFileTimer {

	private static final Log log = LogFactory.getLog(SendFileTimer.class);

	@Resource
	private TimerService timerService;
	
	@EJB(name = "SendFileManager")
	private SendFileManager sendFileManager;
	
	@EJB(name = "RuConfig")
	private RuConfig ruConfig;
	
	private Long idService;

	@PostConstruct
	private void init() {
		log.debug("[RU] PostCostructor of Timer SendFileTimer");
	}

	public void startSending() {
		idService = 1L; //serviceBean.ceateService();
		TimerConfig tf = new TimerConfig();
		tf.setPersistent(false);
		tf.setInfo(ruConfig.getSendFilesTimerInfo());
		timerService.createIntervalTimer(ruConfig.getSendFilesFirstFire(), ruConfig.getSendFilesTimeout(), tf);

		log.debug("[RU] **Started timer "+ruConfig.getSendFilesTimerInfo()+" with following config:**");
		log.debug("[RU] getPathWithFileToBeSend:" + ruConfig.getPathWithFileToBeSend());
		log.debug("[RU] getNrMaxFilesPerMsg:" + ruConfig.getNrMaxFilesPerMsg());
		log.debug("[RU] getNrMaxSizePerFile:" + ruConfig.getNrMaxSizePerFile());
		log.debug("[RU] isCryptedData:" + ruConfig.isCryptedData());
		log.debug("[RU] getSendFilesFirstFire:" + ruConfig.getSendFilesFirstFire());
		log.debug("[RU] getSendFilesTimeout:" + ruConfig.getSendFilesTimeout());
		
		log.info("[RU] Timer "+ruConfig.getSendFilesTimerInfo()+" created for service "+idService+". First run will be after "+(ruConfig.getSendFilesFirstFire()/1000)+" seconds");
	}

	public void stopSending() {
		int i = 1;
		for (Timer timer : timerService.getTimers()) {
			log.debug("[RU] "+i+" - Timer found: "+timer.getInfo());
			timer.cancel();
		}
		log.info("[RU] Stopped all active timers on SendFileTimer.");
	}
	
	@Timeout
	public void doTask(Timer timer) {
		log.info("[RU] [START] Timer: "+timer.getInfo());
		sendFileManager.sendFileLogic();
		log.info("[RU] [END  ] Timer: "+timer.getInfo()+". Next run: "+timer.getNextTimeout()+", (after "+(timer.getTimeRemaining()/1000)+" seconds)");
	}
}
