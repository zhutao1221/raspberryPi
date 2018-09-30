package com.aivc.demo.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aivc.demo.mapper.DemoMapper;
import com.alibaba.fastjson.JSONObject;
  
@Service
public class DemoService{
	private static Logger logger = LogManager.getLogger(DemoService.class.getName());
	
    @Autowired
    private DemoMapper demoMapper;
    
    public JSONObject getDemoBean(){
    	JSONObject result = new JSONObject();
   
		result.put("id",demoMapper.getCount());

        return result;
    }
    
    
}