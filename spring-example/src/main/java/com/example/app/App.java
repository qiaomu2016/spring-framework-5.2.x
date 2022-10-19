package com.example.app;
import com.example.beans.importBeanDefinitionRegistrar.util.X;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("com.example.beans.importBeanDefinitionRegistrar")
//@ComponentScan("com.shadow")
//@Import({D.class, E.class,H.class, K.class})
@Import({X.class})
@MapperScan
public class App {


//	@Import(C.class)
//	class MemberApp{
//
//	}




//	@Bean
//	public DataSource dataSource(){
//		DriverManagerDataSource driverManagerDataSource
//				= new DriverManagerDataSource();
//		driverManagerDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//		driverManagerDataSource.setPassword("aaa111");
//		driverManagerDataSource.setUsername("root");
//		driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/shadow?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
//		return driverManagerDataSource;
//	}
//
//	@Bean
//	@Autowired
//	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
//		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
//		factoryBean.setDataSource(dataSource());
//		return factoryBean.getObject();
//	}


//	@Bean
//	public ADao aDao(){
//		ADao aDao = (ADao) SessionSql.getMapper(ADao.class);
//		return aDao;
//	}
}
