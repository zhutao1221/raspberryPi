package com.aivc.demo.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DemoMapper{
	// 获取当前预测人趋势值
    @Select("select count(1) from dual")
    public int getCount();
    
}
