package com.test.context.config;

import com.test.context.importBeanDefinitionRegistart.TestImportBeanDefinitionRegistart;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Configuration
//@ComponentScan("com.test.context")
//@ComponentScan("com.test.context.bfpp")
@ComponentScan("com.test.context.bean")
//@Import(TestImportBeanDefinitionRegistart.class)
public class ContextConfig {
}
