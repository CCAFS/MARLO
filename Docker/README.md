
# Deployment Instructions

To deploy the application using Docker, follow these steps to prepare your environment, build the Docker image, and run the container. Ensure you have the necessary files and tools ready before starting the deployment process.


## Deployment Steps

1. **Clone the Repository**

```bash
  git clone
```
2. **Place the Properties File**
Place the `marlo-pro.properties` configuration file in the specified directory as outlined in the project's documentation.

/opt/MARLO_11/marlo-web/src/main/resources/config/

3. **Generate the WAR File**
```bash
  sudo mvn clean install -DskipTests
```
4. **Copy the WAR File**
Move or copy the generated WAR file to the same directory where the `Dockerfile` is located. This ensures that the WAR file is included in the Docker build context.

5. **Build the Docker Image**
```bash
  docker build -t custom-tomcat-8.5 .
```

6. **Run the Docker Container**
```bash
  docker run -d \
  --name custom-tomcat-8.5 \
  -v $(pwd)/context.xml:/usr/local/tomcat/conf/context.xml \
  -v $(pwd)/catalina.properties:/usr/local/tomcat/conf/catalina.properties \
  -v $(pwd)/webapps:/usr/local/tomcat/webapps \
  -v $(pwd)/logs:/usr/local/tomcat/logs \
  -p 8080:8080 \
  -p 8443:8443 \
  -p 4040:4040 \
  -e JAVA_OPTS="-javaagent:/usr/local/tomcat/bin/glowroot/glowroot.jar -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Xmx1024m -Xss4096k" \
  --memory 3g \
  --memory-reservation 1g \
  --env-file .env \
  custom-tomcat-8.5
```