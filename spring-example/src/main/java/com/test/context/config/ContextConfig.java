package com.test.context.config;

import com.test.context.condition.MyCondition;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ComponentScan("com.test.context")
//@ComponentScan("com.test.context.bfpp")
@ComponentScan("com.test.context.bean")
//@Import(TestImportBeanDefinitionRegistart.class)
@Conditional(MyCondition.class)
public class ContextConfig {
}
