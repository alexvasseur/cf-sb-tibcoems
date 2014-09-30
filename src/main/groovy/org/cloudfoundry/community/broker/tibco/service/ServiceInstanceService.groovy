package org.cloudfoundry.community.broker.tibco.service


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


/**
 */
@Service
class ServiceInstanceService {

  ServiceInstance findById(String id) {
    return new ServiceInstance(id)
  }

  boolean isExists(ServiceInstance instance) {
	File temp = File.createTempFile("temp",".cftib");
	temp << "echo off\n"
	temp << "connect tcp://localhost:7222 admin\n"
	temp << "show queues ${instance.queue}"
	println "TIBCF isExists ${temp.absolutePath} for ${instance.queue} -->"
	String sout = "tibemsadmin64 -script ${temp.absolutePath}".execute().text
	println "TIBCF isExists ${temp.absolutePath} for ${instance.queue} : $sout"
	return sout.indexOf("No queues found")<0
  }

  int getNumberOfExistingInstances() {
    return 1
  }

  def create(ServiceInstance instance) {
	File temp = File.createTempFile("temp",".cftib");
	temp << "echo on\n"
	temp << "connect tcp://localhost:7222 admin\n"
	temp << "create queue ${instance.queue}\n"
	temp << "commit"
	println "TIBCF create ${temp.absolutePath} for ${instance.queue} -->"
	String sout = "tibemsadmin64 -script ${temp.absolutePath}".execute().text
	println "TIBCF create ${temp.absolutePath} for ${instance.queue} : $sout"
  }

  def delete(ServiceInstance instance) {
	File temp = File.createTempFile("temp",".cftib");
	temp << "echo on\n"
	temp << "connect tcp://localhost:7222 admin\n"
	temp << "delete queue ${instance.queue}\n"
	temp << "commit"
	println "TIBCF delete ${temp.absolutePath} for ${instance.queue} -->"
	String sout = "tibemsadmin64 -script ${temp.absolutePath}".execute().text
	println "TIBCF delete ${temp.absolutePath} for ${instance.queue} : $sout"
  }
}

class ServiceInstance {
  static String Q_PREFIX = "cf_"
  String queue

  ServiceInstance(String id) {
    String s = id.replaceAll('-', '_')
    queue = "${Q_PREFIX}${s}"
  }
}
