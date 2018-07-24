package com.zy.actuator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.zy.actuator.server.MonitorServer;
import com.zy.actuator.service.LogService;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.jmx.enabled=true")
public class ActuatorApplicationTests {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MonitorServer monitorServer;
	
	@Autowired
	private LogService logService;
	
	/**
	 * SpringBoot 2.x测试
	 * @throws Exception
	 */
	@Test
	public void test2() throws Exception{
		this.logger.info("test start....");
		monitorServer.start();
		
		String serverHost = "localhost";
		int serverPort = 9999;
		StringBuffer urlSb = new StringBuffer("service:jmx:rmi:///jndi/rmi://").append(serverHost).append(":")
				.append(serverPort).append("/jmxrmi");
		JMXServiceURL jMXServiceURL = new JMXServiceURL(urlSb.toString());
		JMXConnector jMXConnector = JMXConnectorFactory.connect(jMXServiceURL);
		MBeanServerConnection mBeanServerConnection = jMXConnector.getMBeanServerConnection();
		String[] domains = mBeanServerConnection.getDomains();
		for (String domain : domains) {
			this.logger.info("domain:{}", domain);
		}
		this.logger.info("MBeanCount:{}", mBeanServerConnection.getMBeanCount());
		
//		ObjectName domain = new ObjectName("com.zy.monitor");
//		Set<ObjectInstance> objects = mBeanServerConnection.queryMBeans(null, null);
//		for (ObjectInstance object : objects) {
//			this.logger.info("ObjectName:{}",object.getObjectName());
//		}
		
		Map<String, ObjectName> endpointMap = new HashMap<String, ObjectName>();
		Set<ObjectName> objectNames = mBeanServerConnection.queryNames(null, null);
		for (ObjectName objectName : objectNames) {
			if (objectName.getDomain().equals("com.zy.monitor")
					&& objectName.getKeyProperty("type").equals("Endpoint")) { //筛选域:com.zy.monitor 类型为:Endpoint
				this.logger.info("ObjectName:{},Domain:{},KeyListStr:{}",objectName,objectName.getDomain(),objectName.getKeyPropertyListString());
				endpointMap.put(objectName.getKeyProperty("name"), objectName);
			}
		}
		ObjectName objectName = null;
		
		//Loggers
		System.out.println("");
		objectName = endpointMap.get("Loggers");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			//loggers
			Map loggersResult = (Map) mBeanServerConnection.invoke(objectName, "loggers", null, null);
			this.logger.info("LoggersResult:{}", loggersResult);
			//loggerLevels
			Map loggerLevelsResult = (Map) mBeanServerConnection.invoke(objectName, "loggerLevels", 
					new Object[] {"com.zy.actuator.service.impl.LogserviceImpl"}, new String[] {String.class.getName()});
			this.logger.info("loggerLevelsResult:{}", loggerLevelsResult);
			this.logService.testLog();
			//configureLogLevel
			Map configureLogLevelResult = (Map) mBeanServerConnection.invoke(objectName, "configureLogLevel", 
					new Object[] {"com.zy.actuator.service.impl.LogserviceImpl","ERROR"}, new String[] {String.class.getName(), String.class.getName()});
			this.logger.info("configureLogLevelResult:{}", configureLogLevelResult);
			loggerLevelsResult = (Map) mBeanServerConnection.invoke(objectName, "loggerLevels", 
					new Object[] {"com.zy.actuator.service.impl.LogserviceImpl"}, new String[] {String.class.getName()});
			this.logger.info("loggerLevelsResult:{}", loggerLevelsResult);
			this.logService.testLog();
		}
		
		//Auditevents
		System.out.println("");
		objectName = endpointMap.get("Auditevents");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Map auditeventsResult = (Map) mBeanServerConnection.invoke(objectName, "events", null, null);
			this.logger.info("AuditeventsResult:{}", auditeventsResult);
		}
		
		//Beans
		System.out.println("");
		objectName = endpointMap.get("Beans");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Map beansResult = (Map) mBeanServerConnection.invoke(objectName, "beans", null, null);
			this.logger.info("BeansResult:{}", beansResult.size());
		}
		
		//Conditions
		System.out.println("");
		objectName = endpointMap.get("Conditions");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Map conditionsResult = (Map) mBeanServerConnection.invoke(objectName, "applicationConditionEvaluation", null, null);
			this.logger.info("ConditionsResult:{}", conditionsResult.size());
		}
		
		//Configprops
		System.out.println("");
		objectName = endpointMap.get("Configprops");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Map configpropsResult = (Map) mBeanServerConnection.invoke(objectName, "configurationProperties", null, null);
			this.logger.info("ConfigpropsResult:{}", configpropsResult);
		}
		
		//Env
		System.out.println("");
		objectName = endpointMap.get("Env");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Map environmentResult = (Map) mBeanServerConnection.invoke(objectName, "environment", null, null);
			this.logger.info("environmentResult:{}", environmentResult);
			Map environmentEntryResult = (Map) mBeanServerConnection.invoke(objectName, "environmentEntry", 
					new Object[] {"java.vm.name"}, new String[] {String.class.getName()});
			this.logger.info("environmentEntryResult:{}", environmentEntryResult);
		}
		
		//Health
		System.out.println("");
		objectName = endpointMap.get("Health");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Map healthResult = (Map) mBeanServerConnection.invoke(objectName, "health", null, null);
			this.logger.info("HealthResult:{}", healthResult);
		}
		
		//Info
		System.out.println("");
		objectName = endpointMap.get("Info");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Map infoResult = (Map) mBeanServerConnection.invoke(objectName, "info", null, null);
			this.logger.info("InfoResult:{}", infoResult);
		}
		
		//Mappings
		System.out.println("");
		objectName = endpointMap.get("Mappings");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Map mappingsResult = (Map) mBeanServerConnection.invoke(objectName, "mappings", null, null);
			this.logger.info("MappingsResult:{}", mappingsResult);
		}
		
		//Scheduledtasks
		System.out.println("");
		objectName = endpointMap.get("Scheduledtasks");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Map scheduledtasksResult = (Map) mBeanServerConnection.invoke(objectName, "scheduledTasks", null, null);
			this.logger.info("scheduledtasksResult:{}", scheduledtasksResult);
		}
		
		//Threaddump
		System.out.println("");
		objectName = endpointMap.get("Threaddump");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Map threaddumpResult = (Map) mBeanServerConnection.invoke(objectName, "threadDump", null, null);
			this.logger.info("ThreaddumpResult:{}", threaddumpResult);
		}
		
		//Metrics
		System.out.println("");
		objectName = endpointMap.get("Metrics");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Map metricsListResult = (Map) mBeanServerConnection.invoke(objectName, "listNames", null, null);
			this.logger.info("MetricsListResult:{}", metricsListResult);
			List<String> metrics = (List<String>) metricsListResult.get("names");
			//具体指标
			for (String metric : metrics) {
				String[] params = {metric};
				Map metricsResult = (Map) mBeanServerConnection.invoke(objectName, "metric", params, null);
				this.logger.info("metricsResult:{}", metricsResult);
			}
			
		}
		
		//Shutdown
//		System.out.println("");
//		objectName = endpointMap.get("Shutdown");
//		if (objectName != null) {
//			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
//			this.logger.info("mBeanInfo:{}", mBeanInfo);
//			Map shutdownResult = (Map) mBeanServerConnection.invoke(objectName, "shutdown", null, null);
//			this.logger.info("shutdownResult:{}", shutdownResult);
//		}
		
		//Extend 
		System.out.println("");
		ObjectName objectNameEx = new ObjectName("java.lang:type=ClassLoading");
		MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectNameEx);
		this.logger.info("mBeanInfo:{}", mBeanInfo);
		Object loadedClassCount = mBeanServerConnection.getAttribute(objectNameEx, "LoadedClassCount");
		this.logger.info("loadedClassCount:{}", loadedClassCount);
		MBeanAttributeInfo[] attributeInfos = mBeanInfo.getAttributes();
		for (MBeanAttributeInfo attributeInfo : attributeInfos) {
			Object attribute = mBeanServerConnection.getAttribute(objectNameEx, attributeInfo.getName());
			this.logger.info("{}:{}", attributeInfo.getName(), attribute);
		}
		
		
		System.out.println("");
		jMXConnector.close();
		monitorServer.close();
	}
	
	/**
	 * Spring Boot1.x 测试
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception{
		this.logger.info("test start....");
		monitorServer.start();
		
		String serverHost = "localhost";
		int serverPort = 9999;
		StringBuffer urlSb = new StringBuffer("service:jmx:rmi:///jndi/rmi://").append(serverHost).append(":")
				.append(serverPort).append("/jmxrmi");
		JMXServiceURL jMXServiceURL = new JMXServiceURL(urlSb.toString());
		JMXConnector jMXConnector = JMXConnectorFactory.connect(jMXServiceURL);
		MBeanServerConnection mBeanServerConnection = jMXConnector.getMBeanServerConnection();
		String[] domains = mBeanServerConnection.getDomains();
		for (String domain : domains) {
			this.logger.info("domain:{}", domain);
		}
		this.logger.info("MBeanCount:{}", mBeanServerConnection.getMBeanCount());
		
		Map<String, ObjectName> endpointMap = new HashMap<String, ObjectName>();
		Set<ObjectName> objectNames = mBeanServerConnection.queryNames(null, null);
		for (ObjectName objectName : objectNames) {
			if (objectName.getDomain().equals("com.zy.monitor")
					&& objectName.getKeyProperty("type").equals("Endpoint")) { //筛选域:com.zy.monitor 类型为:Endpoint
				this.logger.info("ObjectName:{},Domain:{},KeyListStr:{}",objectName,objectName.getDomain(),objectName.getKeyPropertyListString());
				endpointMap.put(objectName.getKeyProperty("name"), objectName);
			}
		}
		ObjectName objectName = null;
		
		//auditEventsEndpoint
		System.out.println("");
		objectName = endpointMap.get("auditEventsEndpoint");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Date date = new Date();
			String dateStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
			Object auditEventsResult = mBeanServerConnection.invoke(objectName, "getData", 
					new Object[] {dateStr}, new String[] {String.class.getName()});
			this.logger.info("auditEventsResult:{}", auditEventsResult);
		}
		
		//autoConfigurationReportEndpoint
		System.out.println("");
		objectName = endpointMap.get("autoConfigurationReportEndpoint");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Object autoConfigurationReportResult = mBeanServerConnection.invoke(objectName, "getData", null, null);
			this.logger.info("autoConfigurationReportResult:{}", autoConfigurationReportResult);
		}
		
		//beansEndpoint
		System.out.println("");
		objectName = endpointMap.get("beansEndpoint");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Object beansResult = mBeanServerConnection.invoke(objectName, "getData", null, null);
			this.logger.info("beansResult:{}", beansResult);
		}
		
		//configurationPropertiesReportEndpoint
		System.out.println("");
		objectName = endpointMap.get("configurationPropertiesReportEndpoint");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Object configurationPropertiesReportResult = mBeanServerConnection.invoke(objectName, "getData", null, null);
			this.logger.info("configurationPropertiesReportResult:{}", configurationPropertiesReportResult);
		}
		
		//dumpEndpoint
		System.out.println("");
		objectName = endpointMap.get("dumpEndpoint");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Object dumpResult = mBeanServerConnection.invoke(objectName, "getData", null, null);
			this.logger.info("dumpResult:{}", dumpResult);
		}
		
		//environmentEndpoint
		System.out.println("");
		objectName = endpointMap.get("environmentEndpoint");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Object environmentResult = mBeanServerConnection.invoke(objectName, "getData", null, null);
			this.logger.info("environmentResult:{}", environmentResult);
		}
		
		//healthEndpoint 
		System.out.println("");
		objectName = endpointMap.get("healthEndpoint");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Object healthResult = mBeanServerConnection.invoke(objectName, "getData", null, null);
			this.logger.info("healthResult:{}", healthResult);
		}
		
		//infoEndpoint
		System.out.println("");
		objectName = endpointMap.get("infoEndpoint");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Object infoResult = mBeanServerConnection.invoke(objectName, "getData", null, null);
			this.logger.info("infoResult:{}", infoResult);
		}
		
		//loggersEndpoint
		System.out.println("");
		objectName = endpointMap.get("loggersEndpoint");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			//getLoggers
			Object loggersResult = mBeanServerConnection.invoke(objectName, "getLoggers", null, null);
			this.logger.info("loggersResult:{}", loggersResult);
			//getLogger
			Object loggerResult = mBeanServerConnection.invoke(objectName, "getLogger", 
					new Object[] {"com.zy.actuator.service.impl.LogserviceImpl"}, new String[]{String.class.getName()} );
			this.logger.info("loggerResult:{}", loggerResult);
			this.logService.testLog();
			//setLogLevel
			Object logLevelResult = mBeanServerConnection.invoke(objectName, "setLogLevel", 
					new Object[] {"com.zy.actuator.service.impl.LogserviceImpl","ERROR"}, new String[]{String.class.getName(),String.class.getName()});
			this.logger.info("logLevelResult:{}", logLevelResult);
			loggerResult = mBeanServerConnection.invoke(objectName, "getLogger", 
					new Object[] {"com.zy.actuator.service.impl.LogserviceImpl"}, new String[]{String.class.getName()} );
			this.logger.info("loggerResult:{}", loggerResult);
			this.logService.testLog();
		}
		
		//metricsEndpoint
		System.out.println("");
		objectName = endpointMap.get("metricsEndpoint");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Object metricsResult = mBeanServerConnection.invoke(objectName, "getData", null, null);
			this.logger.info("metricsResult:{}", metricsResult);
		}
		
		//traceEndpoint
		System.out.println("");
		objectName = endpointMap.get("traceEndpoint");
		if (objectName != null) {
			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
			this.logger.info("mBeanInfo:{}", mBeanInfo);
			Object traceResult = mBeanServerConnection.invoke(objectName, "getData", null, null);
			this.logger.info("traceResult:{}", traceResult);
		}
		
		//shutdownEndpoint
//		System.out.println("");
//		objectName = endpointMap.get("shutdownEndpoint");
//		if (objectName != null) {
//			MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
//			this.logger.info("mBeanInfo:{}", mBeanInfo);
//			Object shutdownResult = mBeanServerConnection.invoke(objectName, "shutdown", null, null);
//			this.logger.info("shutdownResult:{}", shutdownResult);
//		}
		
		
		System.out.println("");
		jMXConnector.close();
		monitorServer.close();
	}

}
