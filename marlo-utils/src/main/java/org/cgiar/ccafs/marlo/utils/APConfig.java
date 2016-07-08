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

  private static final String PRODUCTION = "marlo.production";
  private static final String DEBUG_MODE = "marlo.debug";
  private static final String ADMIN_ACTIVE = "marlo.admin.active";
  private static final String IMPACT_PATHWAY_ACTIVE = "marlo.impactPathway.active";

  private static final String AUTOSAVE_FILE = "autosave.file";

  private static final String BASE_URL = "marlo.baseUrl";
  // Logging.
  private static final Logger LOG = LoggerFactory.getLogger(APConfig.class);

  private PropertiesManager properties;


  @Inject
  public APConfig(PropertiesManager properties) {
    this.properties = properties;
  }

  /**
   * Get the folder destination to save the autosave temporal files
   * 
   * @return a string with the auto save folder destination.
   */
  public String getAutoSaveFolder() {
    try {
      return properties.getPropertiesAsString(AUTOSAVE_FILE);
    } catch (Exception e) {
      LOG.error("there is not a base folder to save the uploaded files configured.");
    }
    return null;
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

  public int getEmailPort() {
    try {
      return Integer.parseInt(properties.getPropertiesAsString(EMAIL_PORT));
    } catch (Exception e) {
      LOG.error("there is not an email port configured.");
    }
    return 0;
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
   * Get the flag that indicate if the admin section is active
   * according to the variable in the configuration file.
   * 
   * @return a boolean indicating if it is active.
   */
  public boolean isAdminActive() {
    String adminActive = properties.getPropertiesAsString(ADMIN_ACTIVE);
    if (adminActive == null) {
      LOG.error("There is not a Admin active configured");
      return false;
    }
    return adminActive.equals("true");
  }


  /**
   * If we are activate the Marlo debug mode.
   * 
   * @return true if debug mode in marlo is active, false otherwise.
   */
  public boolean isDebug() {
    String variable = properties.getPropertiesAsString(DEBUG_MODE);
    if (variable == null) {
      LOG.error("There is not a debug mode active configured");
      return false;
    }
    return variable.equals("true");
  }

  /**
   * Get the flag that indicate if the Impact Pathway section is active
   * according to the variable in the configuration file.
   * 
   * @return a boolean indicating if it is active.
   */
  public boolean isImpactPathwayActive() {
    String adminActive = properties.getPropertiesAsString(IMPACT_PATHWAY_ACTIVE);
    if (adminActive == null) {
      LOG.error("There is not a ImpactPathway active configured");
      return false;
    }
    return adminActive.equals("true");
  }

  /**
   * If we are running Marlo in production or testing mode.
   * 
   * @return true if Marlo is running in production mode, false if is run in testing mode.
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
