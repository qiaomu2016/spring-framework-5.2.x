package com.test.batis.util;

import com.test.batis.mybatis.MySqlSession;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * 1、MyFactoryBean自己必须生效 存在spring容器当中
 * 2、mapperInterface必须有值（合理）
 */
@Component
public class MyFactoryBean implements FactoryBean {

	Class<?> mapperInterface;

	public void setMapperInterface(Class<?> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	@Override
	public Object getObject() throws Exception {
		return MySqlSession.getMapper(mapperInterface);
	}

	@Override
	public Class<?> getObjectType() {
		return mapperInterface;
	}

}
