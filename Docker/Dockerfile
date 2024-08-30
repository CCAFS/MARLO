# Use the Tomcat image with Java JDK 11 as the base image
FROM tomcat:8.5.47-jdk11-openjdk

# Add WAR file to Tomcat's web applications directory
ADD /webapps/ROOT.war /usr/local/tomcat/webapps/

# Add a Tomcat users and roles configuration file
ADD tomcat-users.xml /usr/local/tomcat/conf/

# Add an additional configuration file for Tomcat
ADD web.xml /usr/local/tomcat/conf/

# Add a logging configuration file for Tomcat
ADD logging.properties /usr/local/tomcat/conf/

# Add the Glowroot monitoring tool directory to Tomcat's bin folder
ADD glowroot/ /usr/local/tomcat/bin/glowroot/

# Add an environment configuration script for Tomcat
ADD setenv.sh /usr/local/tomcat/bin/

EXPOSE 8080

# Define the command to run when the container starts: start Tomcat
CMD ["catalina.sh","run"]








