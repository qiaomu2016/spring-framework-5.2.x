package com.test.batis.dao;

import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface UserMapper {

	@Select("select * from user")
	List<Map<String,Object>> queryList();

	//执行数据库查询
	@Select("select * from user where id =${id}")
	Map<String,Object> queryForMap(Integer id);
}
