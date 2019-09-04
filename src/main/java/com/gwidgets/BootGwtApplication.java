package com.gwidgets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import com.gwidgets.server.ExtractServiceImpl;
import com.gwidgets.server.GreetingServiceImpl;
import com.gwidgets.server.SettingsServiceImpl;

@ServletComponentScan
@SpringBootApplication
@Configuration
public class BootGwtApplication {
	
	public static void main(String[] args) {
		//SpringApplication.run(BootGwtApplication.class, args);
		SpringApplication.run(BootGwtApplication.class, args);
	}
	
	@Bean
	public ServletRegistrationBean exampleServletBean() {
	    ServletRegistrationBean bean = new ServletRegistrationBean(new GreetingServiceImpl(), "/module1/greet");
	    bean.setLoadOnStartup(1);
	    return bean;
	}
	
    @Bean
    public ServletRegistrationBean extractServletBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new ExtractServiceImpl(), "/module1/extract");
        bean.setLoadOnStartup(1);
        return bean;
    }
    
    @Bean
    public ServletRegistrationBean settingsServletBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new SettingsServiceImpl(), "/module1/settings");
        bean.setLoadOnStartup(1);
        return bean;
    }    
	

//    @Bean
//    public DispatcherServlet dispatcherServlet() {
//        return new DispatcherServlet();
//    }
//
//    @Bean
//    public ServletRegistrationBean dispatchServletRegistration() {
//        ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet(), "/module1/*");
//        registration.setName(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME);
//
//        return registration;
//    }
}
