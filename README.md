JBoss AS7/EAP6 Cluster Visualizer based on HTML5
================================================

How to install:

1. Clone clusterdemo project
   git clone https://github.com/yongyang/clusterdemo.git
   (Now assume the project home directory is %CLUSTER_HOME%)
   NOTE: make sure git is installed on your system.

2. Build
   cd %CLUSTER_HOME%
   mvn -Dmaven.test.skip=true clean package
   If everything is OK, the Web archive file clusterdemo.war now is on the target directory.
   NOTE: make sure you are in directory %CLUSTER_HOME%, and maven 3 is installed on your system

3. Install JBoss AS7/EAP6
   Download AS7 from http://www.jboss.org/jbossas/downloads, then unzip the zip file to %JBOSS_HOME%

4. Deploy WAR file to AS7/EAP6
   cp %CLUSTER_HOME/target/clusterdemo.war %JBOSS_HOME%/standalone/deployments/

5. Add admin user to ManagementRealm, the user name is "admin", password is "123456"
   cd %JBOSS_HOME%/bin
   ./add-user.sh
   Then, follow the prompt message to add user admin/123456 to ManagementRealm
   NOTE: you can use different user name and password, however the default user name and password in web.xml need to be updated too.

6. Run JBoss AS7/EAP6 instance in standalone mode and with standalone-ha.xml configuration
   ./standalone.sh -c standalone-ha.xml -b %IP_ADDRESS% -u 230.0.0.4 -Djboss.server.base.dir=../standalone -Djboss.node.name=node1 -Djboss.socket.binding.port-offset=0
   NOTE: use real ip address to replace %IP_ADDRESS%, and make sure java6 is installed on your system and with %JAVA_HOME% set

7. Run more JBoss AS7/EAP6 instance
   Re-do 3-6 steps to run more JBoss AS7/EAP6 instances in difference machine.
   if you want to run more JBoss AS7/EAP6 instances in same machine, follow below steps:
   a. Create another standalone directory
      cp -r %JBOSS_HOME%/standalone %JBOSS_HOME%/standalone2
   b. Deploy WAR file to standalone2
      cp %CLUSTER_HOME/target/clusterdemo.war %JBOSS_HOME%/standalone2/deployments/
   c. Start with different jboss.server.base.dir, jboss.node.name and jboss.socket.binding.port-offset
      ./standalone.sh -c standalone-ha.xml -b %IP_ADDRESS% -u 230.0.0.4 -Djboss.server.base.dir=../standalone2 -Djboss.node.name=node2 -Djboss.socket.binding.port-offset=100

8. Now visit the clusterdemo web application by your favorite Browser, Firefox, Chrome, Safari are preferred.
   NOTE: URL is http://%IP_ADDRESS%:8080/clusterdemo

HAVE FUN!