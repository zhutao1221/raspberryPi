package com.aivc.demo.mapper;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;


@Mapper
public interface DemoMapper{
	// 获取当前预测人趋势值
    @Select("select count(1) from dual")
    public int getCount();
    
    @Select("select t.id,t.`content` from meeting_content t where meeting_id=#{meetingId} and t.id > #{id}  order by id limit 3")
    public List<Map> getInfo(@Param("id") long id, @Param("meetingId") String meetingId);
    
    @Select("select t.id,t.`content` from meeting_content t where meeting_id=#{meetingId} order by id limit 3")
    public List<Map> getInfoByTime(@Param("meetingId") String meetingId);
    
    @Update("update meeting_mng t set t.`status` = #{status} where t.meeting_id = #{meetingId}")
    public int updateMeetingMng(@Param("status") String status,@Param("meetingId") String meetingId);
    
    @Update("update meeting_mng t set t.`status` = '3' ")
    public int updateMeetingMngStop();
    
    @Select("select meeting_id from meeting_mng t where t.`status` != '3' limit 1 ")
    public String selectMeetingMngOn();
    
    @Insert("INSERT INTO `meeting_mng` (`meeting_id`, `meeting_name`, `status`, `submit_time`, `update_time`) VALUES (DATE_FORMAT(SYSDATE(),'%Y%m%d%H%i%s'), #{meetingName}, '0',SYSDATE(), SYSDATE())")
    public int insertMeetingMng(String meetingName);
}
