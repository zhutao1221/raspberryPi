package com.aivc.voice.mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.dao.DataAccessException;

import com.aivc.voice.bean.MeetingBean;
import com.aivc.voice.bean.MeetingContentBean;

@Mapper
public interface VoiceMapper{
    @Select("insert  count(1) from dual")
    public int getCount();
    
	@Insert("insert into meeting_content(meeting_id,content,submit_time,update_time) values (#{meetingId},#{content},sysdate(),sysdate())")
    public int addMeetingContent(MeetingContentBean bean) throws DataAccessException;
	
	@Select("select meeting_id as meetingId, meeting_name as meetingName, status ,submit_time as submitTime,  update_time as updateTime from meeting_mng where meeting_id = #{meetingId}" )
	public MeetingBean getMeetingInfo(String meetingId) throws DataAccessException;
	
	@Select("select meeting_id as meetingId, meeting_name as meetingName, status ,submit_time as submitTime,  update_time as updateTime from meeting_mng where status = 2 order by id limit 1" )
	public MeetingBean getStartMeeting() throws DataAccessException;
	
}
