package com.example.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
@Slf4j(topic = "e")
public class H implements ImportSelector {
	public H(){
		log.debug("create h  不会存在单例池当中");
	}
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[]{"com.shadow.beans.selector.Z","com.shadow.beans.selector.X"};
	}
}
