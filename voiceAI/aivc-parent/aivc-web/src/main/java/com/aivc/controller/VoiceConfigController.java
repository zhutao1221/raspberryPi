package com.aivc.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.aivc.voice.VoiceConfig;

@RestController
public class VoiceConfigController {
	@Autowired
	private VoiceConfig voiceConfig;
	/***
	 * 获取实时信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/param/update/{type}/{value}")
	@ResponseBody
	public String getRealtimeInfo(HttpServletRequest request, @PathVariable String type, @PathVariable String value) {
		// 如果更新分贝基数
		if(null != type && "dbbase".equals(type)) {
			voiceConfig.setDbBase(Integer.parseInt(value));
			return ""+voiceConfig.getDbBase();
		}
		// 如果更新ai引擎
		else if(null != type && "aitype".equals(type)) {
			voiceConfig.setAIType(value);
			return ""+voiceConfig.getDbBase();
		}
		// 如果更新ai引擎
		else if(null != type && "recordflag".equals(type)) {
			if(value.equals("true")) {
				voiceConfig.setRecordFlag(true);
			} else {
				voiceConfig.setRecordFlag(false);
			}
			return ""+voiceConfig.getRecordFlag();
		}
		return "ERROR";
	}
	
}