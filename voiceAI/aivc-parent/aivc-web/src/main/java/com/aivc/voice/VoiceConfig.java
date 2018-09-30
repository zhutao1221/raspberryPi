package com.aivc.voice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/***
 * 
 * @author xjl
 * 录音控制类
 */
@Component
public class VoiceConfig {
	// 录音开启标志
	@Value("${voice.record.flag}")
	Boolean recordFlag;
	// ai分析模式 1-百度 2-科大讯飞
	@Value("${voice.aitype}")
	String AIType;
	// 分贝基数，用于分辨记录标准
	@Value("${voice.dbbase}")
	int dbBase;
	// 静音间隔，达到间隔就开始中断分析
	@Value("${voice.interval}")
	int interval;
	// 文件大小限制，达到上限就开始中断分析
	@Value("${voice.file.size}")
	int fileSize;
	// 文件保存路径
	@Value("${voice.file.path}")
	String filePath;
	// 会议编号
	@Value("${voice.meeting.id}")
	String meetingId;
	// 当前音量
	String currentDbLevel;
	// 录音文件上限号
	int fileOutputCount = 0;
	int fileUploadputCount =0;
	// 当前音量检测间隔 ，单位毫秒
	@Value("${voice.db.check.interval}")
	int dbCheckInterval;
	public Boolean getRecordFlag() {
		return recordFlag;
	}
	public void setRecordFlag(Boolean recordFlag) {
		this.recordFlag = recordFlag;
	}
	public String getAIType() {
		return AIType;
	}
	public void setAIType(String aIType) {
		AIType = aIType;
	}
	public int getDbBase() {
		return dbBase;
	}
	public void setDbBase(int dbBase) {
		this.dbBase = dbBase;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}
	public String getCurrentDbLevel() {
		return currentDbLevel;
	}
	public void setCurrentDbLevel(String currentDbLevel) {
		this.currentDbLevel = currentDbLevel;
	}
	public int getDbCheckInterval() {
		return dbCheckInterval;
	}
	public void setDbCheckInterval(int dbCheckInterval) {
		this.dbCheckInterval = dbCheckInterval;
	}
	public int getFileOutputCount() {
		return fileOutputCount;
	}
	public void setFileOutputCount(int fileOutputCount) {
		this.fileOutputCount = fileOutputCount;
	}
	public int getFileUploadputCount() {
		return fileUploadputCount;
	}
	public void setFileUploadputCount(int fileUploadputCount) {
		this.fileUploadputCount = fileUploadputCount;
	}
	
}
