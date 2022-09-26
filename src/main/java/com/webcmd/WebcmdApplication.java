package com.webcmd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "com.webcmd" })
@EnableAutoConfiguration(exclude = { 
        DataSourceAutoConfiguration.class, 
        DataSourceTransactionManagerAutoConfiguration.class, 
        HibernateJpaAutoConfiguration.class })
public class WebcmdApplication {
	public static void main(String[] args) {
		SpringApplication.run(WebcmdApplication.class, args);
	}
}
