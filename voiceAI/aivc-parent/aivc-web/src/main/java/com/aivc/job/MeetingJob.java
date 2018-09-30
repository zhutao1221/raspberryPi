package com.aivc.job;

import org.springframework.beans.factory.annotation.Autowired;
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
public class MeetingJob {
	@Autowired
	VoiceMapper aoiceMapper;

	@Autowired
	VoiceConfig voiceConfig;
	@Scheduled(cron = "0/2 * * * * ?")
	public void getInfo() {
		MeetingBean bean = aoiceMapper.getStartMeeting();
		if(null != bean) {
			if(!voiceConfig.getMeetingId().equals(bean.getMeetingId())) {
				voiceConfig.setMeetingId(bean.getMeetingId());
				voiceConfig.setRecordFlag(true);
				voiceConfig.setFileOutputCount(0);
				voiceConfig.setFileUploadputCount(0);
			}
		} else {
			voiceConfig.setMeetingId("0");
			voiceConfig.setRecordFlag(false);
			voiceConfig.setFileOutputCount(0);
			voiceConfig.setFileUploadputCount(0);
		}
	}
}
