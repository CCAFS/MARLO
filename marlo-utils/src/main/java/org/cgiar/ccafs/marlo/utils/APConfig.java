/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.utils;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class APConfig {


  public static final String MYSQL_HOST = "mysql.host";
  public static final String MYSQL_USER = "mysql.user";
  public static final String MYSQL_PASSWORD = "mysql.password";
  public static final String MYSQL_DATABASE = "mysql.database";
  public static final String MYSQL_PORT = "mysql.port";

  private static final String EMAIL_USER = "email.user";
  private static final String EMAIL_NOTIFICATION = "email.notification";
  private static final String EMAIL_PASSWORD = "email.password";
  private static final String EMAIL_HOST = "email.host";
  private static final String EMAIL_PORT = "email.port";
  private static final String PRODUCTION = "ccafsap.production";

  private static final String BASE_URL = "marlo.baseUrl";
  // Logging.
  private static final Logger LOG = LoggerFactory.getLogger(APConfig.class);

  private PropertiesManager properties;


  @Inject
  public APConfig(PropertiesManager properties) {
    this.properties = properties;
  }

  /**
   * Return the base url previously added in the configuration file.
   * 
   * @return The Base Url in the following format: http://baseurl or https://baseurl.
   */
  public String getBaseUrl() {
    String base = properties.getPropertiesAsString(BASE_URL);
    if (base == null) {
      LOG.error("There is not a base url configured");
      return null;
    }
    while (base != null && base.endsWith("/")) {
      base = base.substring(0, base.length() - 1);
    }
    if (!base.startsWith("https://")) {
      base = "http://" + base;
      return base;
    }
    return base;
  }

  public String getEmailHost() {
    try {
      return properties.getPropertiesAsString(EMAIL_HOST);
    } catch (Exception e) {
      LOG.error("there is not an email host configured.");
    }
    return null;
  }

  /**
   * get the email.configuration tag in the configuration file
   * 
   * @return string whit the email that is in the configuration file.
   */
  public String getEmailNotification() {
    try {
      return properties.getPropertiesAsString(EMAIL_NOTIFICATION);
    } catch (Exception e) {
      LOG.error("there is not an email user configured.");
    }
    return null;
  }

  public String getEmailPassword() {
    try {
      return properties.getPropertiesAsString(EMAIL_PASSWORD);
    } catch (Exception e) {
      LOG.error("there is not an email password configured.");
    }
    return null;
  }

  public String getEmailUsername() {
    try {
      return properties.getPropertiesAsString(EMAIL_USER);
    } catch (Exception e) {
      LOG.error("there is not an email user configured.");
    }
    return null;
  }

  /**
   * If we are running P&R in production or testing mode.
   * 
   * @return true if P&R is running in production mode, false if is run in testing mode.
   */
  public boolean isProduction() {
    String variable = properties.getPropertiesAsString(PRODUCTION);
    if (variable == null) {
      LOG.error("There is not a production/testing mode active configured");
      return false;
    }
    return variable.equals("true");
  }


}
