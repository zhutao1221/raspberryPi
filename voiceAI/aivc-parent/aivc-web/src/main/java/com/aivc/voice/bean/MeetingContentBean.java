package com.aivc.voice.bean;

import java.util.Date;

public class MeetingContentBean {
	
	String meetingId;
	String content;
	String status;
	Date submitTime;
	Date updateTime;
	public String getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public void setCreateTime(Date createTime) {
		this.submitTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	@Override
	public String toString() {
		return "meetingContentBean [meetingId=" + meetingId + ", content=" + content + ", status=" + status
				+ ", submitTime=" + submitTime + ", updateTime=" + updateTime + "]";
	}

}
