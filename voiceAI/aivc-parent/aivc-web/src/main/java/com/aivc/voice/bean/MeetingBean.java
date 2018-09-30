package com.aivc.voice.bean;

import java.util.Date;

public class MeetingBean {
	String meetingId;
	String meetingName;
	String status;
	Date submitTime;
	Date updateTime;
	public String getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}
	public String getMeetingName() {
		return meetingName;
	}
	public void setMeetingName(String meetingName) {
		this.meetingName = meetingName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	@Override
	public String toString() {
		return "MeetingBean [meetingId=" + meetingId + ", meetingName=" + meetingName + ", status=" + status
				+ ", submitTime=" + submitTime + ", updateTime=" + updateTime + "]";
	}
	
}
