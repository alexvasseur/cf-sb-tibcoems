javac -cp lib/jms-2.0.jar:lib/tibjms.jar src/pivotal/tibco/tibjmsMsgProducer.java

java -cp src:lib/jms-2.0.jar:lib/tibjms.jar pivotal.tibco.tibjmsMsgProducer $@
