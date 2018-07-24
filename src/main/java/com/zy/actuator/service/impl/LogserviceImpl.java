package com.zy.actuator.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zy.actuator.service.LogService;

@Service
public class LogserviceImpl implements LogService{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void testLog() {
		this.logger.debug("this is debug");
		this.logger.info("this is info");
		this.logger.warn("this is warn");
		this.logger.error("this is error");
	}

}
