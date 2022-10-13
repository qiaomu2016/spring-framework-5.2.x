package com.test.autoModel.lookup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component("lc")
@Slf4j(topic = "e")
public abstract class LC {

	public void printInfo(){
		LB lb = createLB();
		log.debug("lb-[{}]",lb);
		lb = createLB();
		log.debug("lb-[{}]",lb);
	}

	@Lookup
	public abstract LB createLB();
}
