package com.test.batis.dao;

import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface AMapper {

	@Select("select * from a")
	List<Map<String,Object>> queryFroList();

	//执行数据库查询
	@Select("select * from a where id =${id}")
	Map<String,Object> queryFroMap(Integer id);
}
