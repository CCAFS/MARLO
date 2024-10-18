# Use a base image that has Maven to build the WAR
FROM maven:3.8.4-openjdk-11 AS build

# Set the working directory
WORKDIR /app

# Copy all content from the MARLO_11 folder
COPY . .

# Build the WAR, skipping tests
RUN mvn clean install -Dmaven.test.skip=true

# Use the Tomcat image to deploy the WAR
FROM tomcat:8.5.47-jdk11-openjdk

# Copy the generated WAR from the correct path
COPY --from=build /app/marlo-web/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Create the backup folder outside of webapps
RUN mkdir -p /usr/local/tomcat/backup

# Copy the WAR to the backup folder
COPY --from=build /app/marlo-web/target/*.war /usr/local/tomcat/backup/ROOT.war

# Copy necessary dependencies from the Docker folder at the root of the project
COPY Docker/kryo-1.03.jar /usr/local/tomcat/lib/
COPY Docker/msm-kryo-serializer-1.6.1.jar /usr/local/tomcat/lib/
COPY Docker/reflectasm-0.9.jar /usr/local/tomcat/lib/
COPY Docker/kryo-serializers-0.43.jar /usr/local/tomcat/lib/
COPY Docker/joda-time-2.1.jar /usr/local/tomcat/lib/
COPY Docker/minlog-1.3.1.jar /usr/local/tomcat/lib/
COPY Docker/spymemcached-2.11.1.jar /usr/local/tomcat/lib/
COPY Docker/memcached-session-manager-2.3.2.jar /usr/local/tomcat/lib/
COPY Docker/memcached-session-manager-tc8-2.3.2.jar /usr/local/tomcat/lib/

# Add Tomcat configuration
ADD Docker/tomcat-users.xml /usr/local/tomcat/conf/
ADD Docker/web.xml /usr/local/tomcat/conf/
ADD Docker/logging.properties /usr/local/tomcat/conf/
ADD Docker/glowroot/ /usr/local/tomcat/bin/glowroot/
ADD Docker/setenv.sh /usr/local/tomcat/bin/

# Expose port 8080
EXPOSE 8080

# Command to start Tomcat
CMD ["catalina.sh", "run"]
