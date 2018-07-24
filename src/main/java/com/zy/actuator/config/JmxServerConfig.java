package com.zy.actuator.config;

import javax.management.MBeanServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zy.actuator.server.MonitorServer;

@Configuration
public class JmxServerConfig {
	
	@Autowired
	private MBeanServer mBeanServer;
	
	@Bean
	public MonitorServer monitorServer(){
		MonitorServer monitorServer = new MonitorServer(this.mBeanServer);
		return monitorServer;
	}

}
