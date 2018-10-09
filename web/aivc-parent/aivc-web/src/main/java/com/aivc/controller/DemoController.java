package com.aivc.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.aivc.demo.mapper.DemoMapper;
import com.aivc.demo.service.DemoService;
import com.alibaba.fastjson.JSONObject;

@RestController
public class DemoController {
	@Autowired
	private DemoService demoService;
	/***
	 * 获取实时信息
	 * @param request
	 * @return
	 */
	@Autowired
    private DemoMapper demoMapper;
	
	@RequestMapping("/getInfo")
	@ResponseBody
	public String getRealtimeInfo(HttpServletRequest request) {

    	JSONObject result = new JSONObject();
    	int id;
    	try {
    		id = Integer.parseInt(request.getParameter("id"));
    		String  updateTime = request.getParameter("updateTime");
    		String  meetingId = request.getParameter("meetingId");
    		List<Map> result11 = null;
    		if(id > 0) {
    			result11 = demoMapper.getInfo(id,meetingId);
    		}else{
    			result11 = demoMapper.getInfoByTime(meetingId);
    		}
    		//System.out.println(JSONObject.toJSONString(result11));
    		return JSONObject.toJSONString(result11);
    	}catch(Exception e) {
    		return "";
    	}	    
	}
	@RequestMapping("/insertMeetingMng")
	@ResponseBody
	public String insertMeetingMng(HttpServletRequest request) {
		demoMapper.updateMeetingMngStop();
		String  meetingName = request.getParameter("meetingName");
		demoMapper.insertMeetingMng(meetingName);
		return demoMapper.selectMeetingMngOn();
	}
	
	@RequestMapping("/updateMeetingMng")
	@ResponseBody
	public int updateMeetingMng(HttpServletRequest request) {
		String  meetingId = request.getParameter("meetingId");
		String  status = request.getParameter("status");
		return demoMapper.updateMeetingMng(status,meetingId);
	}

}