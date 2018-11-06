package com.aivc.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aivc.voice.VoiceConfig;
import com.aivc.voice.bean.MeetingBean;
import com.aivc.voice.mapper.VoiceMapper;

/***
 * 
 * 
 * @author xjl
 *
 */
@Component
@EnableAsync
public class MeetingJob {
	@Autowired
	VoiceMapper aoiceMapper;

	@Autowired
	VoiceConfig voiceConfig;

	private static Logger logger = LogManager.getLogger(MeetingJob.class.getName());

	@PostConstruct
	public void getInfo() {

		GetInfoThread getInfoThread = new GetInfoThread();
		Thread t = new Thread(getInfoThread);
		t.start();
		logger.info("getInfo thread started");
	}
	// 定时获取数据库配置，设置当前录音会议id
	@Async
	@Scheduled(cron = " 0/2 * * * * ?")
	public void getMeeting() {
		MeetingBean bean = aoiceMapper.getStartMeeting();
		if (null != bean) {
			if (!voiceConfig.getMeetingId().equals(bean.getMeetingId())) {
				voiceConfig.setMeetingId(bean.getMeetingId());
				voiceConfig.setFileOutputCount(0);
				voiceConfig.setFileUploadputCount(0);
			}
		} else {
			voiceConfig.setMeetingId("0");
			voiceConfig.setFileOutputCount(0);
			voiceConfig.setFileUploadputCount(0);
		}
	}

	// 上传类，因为要用到MyRecord类中的变量，所以将其做成内部类
	class GetInfoThread implements Runnable {

		public void run() {

			BufferedReader bufferedReader = null;
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(System.in));
				while (true) {
					String val = bufferedReader.readLine();
					if (voiceConfig.getRecordFlag()) {
						voiceConfig.setRecordFlag(false);
						logger.info("now recording stop");
					} else {
						voiceConfig.setRecordFlag(true);
						logger.info("now recording start ");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					bufferedReader = null;
				}
			}
		}
	}
}
