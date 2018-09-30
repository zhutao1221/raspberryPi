package com.aivc.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.aivc.demo.service.DemoService;

@RestController
public class DemoController {
	@Autowired
	private DemoService demoService;
	/***
	 * 获取实时信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/test")
	@ResponseBody
	public String getRealtimeInfo(HttpServletRequest request) {

		return demoService.getDemoBean().toString();
	}
	
}