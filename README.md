Author
======
* Alexandre Vasseur, Pivotal
* License: GPL

Introduction
============
A java version of Tibco EMS broker for cloudfoundry.  It is ported from cf-mysql-broker.


![alt tag](https://github.com/avasseur-pivotal/cf-sb-tibcoems/blob/master/tibcoEMSinPCF.png)


How To Build and Run
====================
To build the project
```
./gradlew build
```

The build command creates jar file with embedded tomcat container.
```
java -jar build/libs/cf-tibco-java-broker-0.1.0.jar
```

Configuration
=============
By default,
* the tomcat server is listening at port `9000`
* requires a TibcoEMs installation with tibemsd64 and most importantly tibemsadmin64
* the integration relies on tibemsadmin64 -script. Scripts are generated on the fly by the service broker

The configuration can be changed by modifying the file under `resources\application.yml`

Tested with
===========
* TibcoEMS 8.0.0
* Pivotal Cloud Foundry 1.3




Routes
======
|Routes|Method|Description|
|------|------|-----------|
|/v2/catalog|GET|Service and its plan details by this broker|
|/v2/service_instances/:id|PUT|create a dedicated database for this service|
|/v2/service_instances/:id|DELETE|delete previously created database for this service|
|/v2/service_instances/:id/service_bindings/:id|PUT|create user and grant privilege for the database associated with service.|
|/v2/service_instances/:id/service_bindings/:id|DELETE|delete the user created previously for this binding.|

