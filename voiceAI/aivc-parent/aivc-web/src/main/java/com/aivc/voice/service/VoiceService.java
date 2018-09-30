package com.aivc.voice.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aivc.voice.bean.MeetingContentBean;
import com.aivc.voice.mapper.VoiceMapper;
  
@Service
public class VoiceService{
	private static Logger logger = LogManager.getLogger(VoiceService.class.getName());
	
    @Autowired
    private VoiceMapper voiceMapper;
    
    public int addMeetingContent(MeetingContentBean bean){
    	int res = 0;
    	logger.info("VoiceService->addMeetingContent start",bean);
    	try {
    		res = voiceMapper.addMeetingContent(bean);
    	}catch(Exception e) {
    		logger.error("VoiceService->addMeetingContent error",e);
    	}
    	logger.info("VoiceService->addMeetingContent end");
        return res;
    }
    
    
}