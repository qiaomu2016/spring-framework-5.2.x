package com.test.batis.service;

import java.util.List;
import java.util.Map;

public interface UserService {
	List<Map<String,Object>> queryUserList();
	Map<String,Object> queryFroMap(Integer id);
}
