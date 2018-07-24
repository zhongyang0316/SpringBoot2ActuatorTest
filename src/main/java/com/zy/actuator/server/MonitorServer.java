package com.zy.actuator.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监控管理Server
 * @author zhongyang
 *
 */
public class MonitorServer implements Cloneable {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	//初始化
	private MBeanServer mBeanServer;
	
	//初始化
	private String serverHost = "localhost";
	
	//初始化
	private int serverPort = 9999;
	
	private JMXConnectorServer jMXConnectorServer;
	
	private Registry registry;
	
	public MonitorServer(MBeanServer mBeanServer){
		this.mBeanServer = mBeanServer;
	}
	
	public MonitorServer(String serverHost, int serverPort, MBeanServer mBeanServer) {
		this.mBeanServer = mBeanServer;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
	}
	
	public void start() {
		
		if (this.mBeanServer == null) {
			throw new RuntimeException("mBeanServer is null");
		}
		
		if (this.jMXConnectorServer != null || this.registry != null) {
			this.logger.info("JmxMonitorServer Already Runing");
			return;
		}
		
		try {
			this.logger.info("JmxMonitorServer Starting...");
			
			//注册端口
			this.registry = LocateRegistry.createRegistry(this.serverPort);
			this.logger.debug("JmxMonitorServer registry:{}", this.registry);
			
			//URL路径的结尾可以随意指定，但如果需要用Jconsole来进行连接，则必须使用jmxrmi
			//service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi
			StringBuffer urlSb = new StringBuffer("service:jmx:rmi:///jndi/rmi://").append(this.serverHost).append(":")
					.append(this.serverPort).append("/jmxrmi");
			JMXServiceURL url = new JMXServiceURL(urlSb.toString());
			this.logger.info("JmxMonitorServer JMXServiceURL:{}", urlSb.toString());
			this.jMXConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, this.mBeanServer);
			this.jMXConnectorServer.start();
			this.logger.debug("JmxMonitorServer jMXConnectorServer:{}", this.jMXConnectorServer);
			
			this.logger.info("JmxMonitorServer Start Success...");
		} catch (Exception e) {
			this.logger.error("JmxMonitorServer start Falied!Error:{}", e);
			throw new RuntimeException(e);
		}
	}
	
	public void close() {
		try {
			this.logger.info("JmxMonitorServer Closeing...");
			
			if (this.jMXConnectorServer != null) {
				this.jMXConnectorServer.stop();
				this.jMXConnectorServer = null;
				this.logger.debug("JmxMonitorServer jMXConnectorServer closed");
			}
			
			//关闭端口
			if (this.registry != null) {
				UnicastRemoteObject.unexportObject(this.registry, true);
				this.registry = null;
				this.logger.debug("JmxMonitorServer registry closed");
			}
			
			this.logger.info("JmxMonitorServer Close Success...");
		} catch (Exception e) {
			this.logger.error("JmxMonitorServer close Falied!Error:{}", e);
			throw new RuntimeException(e);
		}
		
	}

	public String getServerHost() {
		return this.serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public int getServerPort() {
		return this.serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

}
