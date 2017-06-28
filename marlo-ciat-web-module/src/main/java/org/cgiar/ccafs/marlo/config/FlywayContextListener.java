package org.cgiar.ccafs.marlo.config;


import org.cgiar.ccafs.marlo.utils.PropertiesManager;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FlywayContextListener implements ServletContextListener {

  private final String SQL_MIGRATIONS_PATH = "database/migrations";
  private final String JAVA_MIGRATIONS_PATH = "classpath:/org/cgiar/ccafs/marlo/db/migration";

  Logger LOG = LoggerFactory.getLogger(FlywayContextListener.class);

  private PropertiesManager properties;

  private void configurePlaceholders(Flyway flyway) {
    Map<String, String> placeHolders = new HashMap<>();

    placeHolders.put("database", properties.getPropertiesAsString("mysql.database"));
    placeHolders.put("user", properties.getPropertiesAsString("mysql.user"));

    flyway.setPlaceholders(placeHolders);
    flyway.setPlaceholderPrefix("$[");
    flyway.setPlaceholderSuffix("]");
    flyway.setPlaceholderReplacement(true);
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {

    Flyway flyway = new Flyway();
    properties = new PropertiesManager();
    try {
      Statement st = this.getDataSourceNoSchema().getConnection().createStatement();
      st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + properties.getPropertiesAsString("mysql.database") + ";");
      st.close();
    } catch (SQLException e) {
      LOG.error("Error " + e.getLocalizedMessage());
    }


    flyway.setDataSource(this.getDataSource());
    flyway.setLocations(SQL_MIGRATIONS_PATH, JAVA_MIGRATIONS_PATH);

    this.configurePlaceholders(flyway);
    // DELETE ALL DB
    // flyway.clean();
    if (flyway.info().current() == null) {
      LOG.info("Setting baseline version 1.0");
      flyway.setBaselineVersion(MigrationVersion.fromVersion("1.0"));
      flyway.baseline();

    }


    // Show the changes to be applied
    LOG.info("-------------------------------------------------------------");
    for (MigrationInfo i : flyway.info().all()) {
      LOG.info("migrate task: " + i.getVersion() + " : " + i.getDescription() + " from file: " + i.getScript()
        + " with state: " + i.getState());
    }
    LOG.info("-------------------------------------------------------------");


    flyway.repair();
    flyway.setOutOfOrder(true);
    flyway.migrate();


  }

  private DataSource getDataSource() {
    MysqlDataSource dataSource = new MysqlDataSource();

    StringBuilder url = new StringBuilder();
    url.append("jdbc:mysql://");
    url.append(properties.getPropertiesAsString("mysql.host"));
    url.append(":");
    url.append(properties.getPropertiesAsString("mysql.port"));
    url.append("/");
    url.append(properties.getPropertiesAsString("mysql.database"));

    dataSource.setUrl(url.toString());
    dataSource.setUser(properties.getPropertiesAsString("mysql.user"));
    dataSource.setPassword(properties.getPropertiesAsString("mysql.password"));

    return dataSource;
  }

  private DataSource getDataSourceNoSchema() {
    MysqlDataSource dataSource = new MysqlDataSource();

    StringBuilder url = new StringBuilder();
    url.append("jdbc:mysql://");
    url.append(properties.getPropertiesAsString("mysql.host"));
    url.append(":");
    url.append(properties.getPropertiesAsString("mysql.port"));

    dataSource.setUrl(url.toString());
    dataSource.setUser(properties.getPropertiesAsString("mysql.user"));
    dataSource.setPassword(properties.getPropertiesAsString("mysql.password"));

    return dataSource;
  }


}
