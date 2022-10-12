package com.test.transaction.controller;

import com.test.transaction.service.PropagationUserService1;
import com.test.transaction.service.PropagationUserService2;
import org.springframework.beans.factory.annotation.Autowired;

//@Component
public class PropagationController {


	@Autowired
	PropagationUserService1 service1;
	@Autowired
	PropagationUserService2 service2;

	public void update() {
		try {
			//conn2
			//create transaction1
			service1.update();
			//create transaction2
			service2.update();
		} catch (Exception e) {

		}


	}
}
