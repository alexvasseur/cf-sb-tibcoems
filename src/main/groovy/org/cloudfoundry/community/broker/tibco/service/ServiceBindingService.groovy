package org.cloudfoundry.community.broker.tibco.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

import java.security.MessageDigest

/**
 */
@Service
class ServiceBindingService implements EnvironmentAware {
  @Autowired ServiceInstanceService instanceService

  Environment environment

  @Override
  void setEnvironment(Environment environment) {
    this.environment = environment
  }

  ServiceBinding findById(String id, String instanceId) {
    ServiceInstance instance = instanceService.findById(instanceId)
    ServiceBinding binding = new ServiceBinding(id, instance, environment)

	File temp = File.createTempFile("temp",".cftib");
	temp << "echo off\n"
	temp << "connect tcp://localhost:7222 admin\n"
	temp << "show user ${binding.credentials.username}"
	println "TIBCF findById ${temp.absolutePath} for binding ${binding.credentials.username} -->"
	String sout = "tibemsadmin64 -script ${temp.absolutePath}".execute().text
	println "TIBCF findById ${temp.absolutePath} for binding ${binding.credentials.username} : $sout"
 	// will show "not found"	
	//TODO not sure what we do when not found
	return binding
/*
    try {
      jdbcTemplate.execute("SHOW GRANTS FOR '${binding.username}'")
    } catch (Exception e) {
      e.message =~ /no such grant/
    }
    return binding
*/
  }

  def save(ServiceBinding binding) {
	File temp = File.createTempFile("temp",".cftib");
	temp << "echo off\n"
	temp << "connect tcp://localhost:7222 admin\n"
	temp << "create user ${binding.credentials.username} password=${binding.credentials.password}\n"
	temp << "grant queue sample user=${binding.credentials.username} all\n"
	temp << "grant queue ${binding.credentials.queue} user=${binding.credentials.username} all\n"
	temp << "commit"
	println "TIBCF save ${temp.absolutePath} for binding ${binding.credentials.username} on ${binding.credentials.queue} -->"
	String sout = "tibemsadmin64 -script ${temp.absolutePath}".execute().text
	println "TIBCF save ${temp.absolutePath} for binding ${binding.credentials.username} on ${binding.credentials.queue} : $sout"
  }

  def destroy(ServiceBinding binding) {
	File temp = File.createTempFile("temp",".cftib");
	temp << "echo off\n"
	temp << "connect tcp://localhost:7222 admin\n"
	temp << "delete user ${binding.credentials.username}\n"
	temp << "commit"
	println "TIBCF destroy ${temp.absolutePath} for binding ${binding.credentials.username} -->"
	String sout = "tibemsadmin64 -script ${temp.absolutePath}".execute().text
	println "TIBCF destroy ${temp.absolutePath} for binding ${binding.credentials.username} : $sout"
  }
}

class ServiceBinding {
  Map<String, String> credentials
  //String server, queue, username, password

  ServiceBinding(String id, ServiceInstance instance, Environment environment) {
    MessageDigest digest = MessageDigest.getInstance("MD5")
    digest.update(id.bytes);
    
    this.credentials = [:]  //new HashSet<String, String>()
    this.credentials.username = new BigInteger(1, digest.digest()).toString(16).replaceAll(/[^a-zA-Z0-9]+/, '').substring(0, 16)
    this.credentials.password = UUID.randomUUID().toString()
    this.credentials.server = environment.getProperty('spring.datasource.url')
	//'tcp://localhostTODOpublicIP:7222'
    //this.jdbc_url = environment.getProperty('spring.datasource.url')
    //this.uri = this.jdbc_url.substring(5)
    this.credentials.queue = instance.queue
  }
}
