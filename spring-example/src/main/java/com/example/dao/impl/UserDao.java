package com.example.dao.impl;

import com.example.dao.BaseDao;
import org.springframework.stereotype.Component;

@Component
public class UserDao implements BaseDao {

	@Override
	public void validate() {
		System.out.println("UserDao---validate");
	}
}
