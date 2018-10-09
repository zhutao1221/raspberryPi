package com.aivc.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/***
 *  
 * 
 * @author xjl
 *
 */
@Component
public class DemoJob {
	private static Logger logger = LogManager.getLogger(DemoJob.class.getName());


	@Scheduled(cron = "0/30 * * * * ?")
	public void demo() {
	
	}
}
