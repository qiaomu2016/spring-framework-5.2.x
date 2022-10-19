package com.example.dao.impl;

import com.example.dao.BaseDao;
import org.springframework.stereotype.Component;

//@Component
public class AccountDao implements BaseDao {
	@Override
	public void validate() {
		System.out.println("AccountDao----validate");
	}
}
