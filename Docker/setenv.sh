# Enable Glowroot monitoring agent for Java 17
# Using latest version (0.13.6) - if issues persist, may need to use a fork or alternative
CATALINA_OPTS="$CATALINA_OPTS -javaagent:/usr/local/tomcat/bin/glowroot/glowroot.jar"

export DB_USER="$DB_USER"
