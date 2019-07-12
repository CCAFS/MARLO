package org.cgiar.ccafs.marlo;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */

/**
 * Configuration for Database and Flyway beans.
 */
@Configuration
public class MarloFlywayConfiguration {

  /**
   * MARLO properties are now accesible via the spring @Value annotation
   */
  @Value("${mysql.url}")
  private String databaseUrl;

  @Value("${mysql.user}")
  private String databaseUser;

  @Value("${mysql.password}")
  private String password;

  @Value("${mysql.database}")
  private String databaseSchema;

  private final Logger log = LoggerFactory.getLogger(MarloFlywayConfiguration.class);

  @Bean(name = "flyway")
  public Flyway getFlyway(final DataSource dataSource) {
    Flyway flyway = new Flyway();
    try {
      Statement st = dataSource.getConnection().createStatement();
      st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseSchema + ";");
      st.close();
    } catch (SQLException e) {
      log.error("Error " + e.getLocalizedMessage());
    }

    flyway.setDataSource(dataSource);
    flyway.setLocations("database/migrations");

    Map<String, String> placeHolders = new HashMap<>();

    placeHolders.put("database", databaseSchema);
    placeHolders.put("user", databaseUser);

    flyway.setPlaceholders(placeHolders);
    flyway.setPlaceholderPrefix("$[");
    flyway.setPlaceholderSuffix("]");
    flyway.setPlaceholderReplacement(true);

    // DELETE ALL DB
    //
    if (flyway.info().current() == null) {
      log.info("Setting baseline version 2.0");
      flyway.setBaselineVersion(MigrationVersion.fromVersion("2.0"));
      flyway.baseline();
      flyway.migrate();
    } else {
      // Show the changes to be applied
      flyway.repair();
      flyway.setOutOfOrder(true);
      flyway.migrate();

    }
    return flyway;
  }

}
