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
package org.cgiar.ccafs.marlo.utils;


import java.io.File;

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
  private static final String GOOGLE_API_KEY = "google.api.key";
  private static final String PUSH_API_KEY = "pusher.api.key";
  private static final String PUSH_APP_ID = "pusher.api.appid";
  private static final String PUSH_SECRETE_KEY = "pusher.api.privatekey";
  private static final String FILE_DOWNLOADS = "file.downloads";
  private static final String PROJECTS_BASE_FOLDER = "file.uploads.projectsFolder";
  private static final String PROJECT_BILATERAL_PROPOSAL_FOLDER = "file.uploads.project.bilateralProposalFolder";
  private static final String PROJECT_BILATERAL_ANUAL_REPORT_FOLDER = "file.uploads.project.bilateralPAnualReport";
  private static final String PROJECT_WORKPLAN_FOLDER = "file.uploads.project.WorkplanFolder";
  private static final String RESOURCE_PATH = "resource.path";
  private static final String AUTO_SAVE_ACTIVE = "autosave.active";
  private static final String UPLOADS_BASE_FOLDER = "file.uploads.baseFolder";
  private static final String PRODUCTION = "marlo.production";
  private static final String DEBUG_MODE = "marlo.debug";
  private static final String ADMIN_ACTIVE = "marlo.admin.active";
  private static final String IMPACT_PATHWAY_ACTIVE = "marlo.impactPathway.active";
  private static final String OCS_LINK = "ocs.link";
  private static final String OCS_PASSWORD = "ocs.password";
  private static final String OCS_USER = "ocs.user";

  private static final String AUTOSAVE_FOLDER = "autosave.folder";

  private static final String BASE_URL = "marlo.baseUrl";
  // Logging.
  private static final Logger LOG = LoggerFactory.getLogger(APConfig.class);

  private PropertiesManager properties;

  @Inject
  public APConfig(PropertiesManager properties) {
    this.properties = properties;
  }

  /**
   * Get the folder where the bilateral project contract proposal should be loaded
   * 
   * @return a string with the path
   */
  public String getAnualReportFolder() {
    try {
      return properties.getPropertiesAsString(PROJECT_BILATERAL_ANUAL_REPORT_FOLDER);
    } catch (Exception e) {
      LOG.error("there is not a base folder to save the uploaded files configured.");
    }
    return null;
  }

  /**
   * Get the folder destination to save the autosave temporal files
   * 
   * @return a string with the auto save folder destination.
   */
  public String getAutoSaveFolder() {
    try {
      return properties.getPropertiesAsString(AUTOSAVE_FOLDER);
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

  /**
   * Get the folder where the bilateral project contract proposal should be loaded
   * 
   * @return a string with the path
   */
  public String getBilateralProjectContractProposalFolder() {
    try {
      return properties.getPropertiesAsString(PROJECT_BILATERAL_PROPOSAL_FOLDER);
    } catch (Exception e) {
      LOG.error("there is not a base folder to save the uploaded files configured.");
    }
    return null;
  }

  /**
   * Get the URL where the users can download the uploaded files
   * 
   * @return a string with the path
   */
  public String getDownloadURL() {
    String downloadsURL = properties.getPropertiesAsString(FILE_DOWNLOADS);
    if (downloadsURL == null) {
      LOG.error("There is not a downloads url configured");
      return null;
    }
    while (downloadsURL != null && downloadsURL.endsWith("/")) {
      downloadsURL = downloadsURL.substring(0, downloadsURL.length() - 1);
    }
    /*
     * if (!downloadsURL.startsWith("https://")) {
     * downloadsURL = "http://" + downloadsURL;
     * return downloadsURL;}
     */
    return downloadsURL;
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

  public String getGoogleApiKey() {
    try {
      return properties.getPropertiesAsString(GOOGLE_API_KEY);
    } catch (Exception e) {
      LOG.error("there is not an google api key configured.");
    }
    return "";
  }

  /**
   * Get the OCS ws link
   * 
   * @return a string with the ocs link
   */
  public String getOcsLink() {
    String ocsLink = properties.getPropertiesAsString(OCS_LINK);
    return ocsLink;
  }


  /**
   * Get the OCS ws password
   * 
   * @return a string with the ocs password
   */
  public String getOcsPassword() {
    String ocsPass = properties.getPropertiesAsString(OCS_PASSWORD);
    return ocsPass;
  }

  /**
   * Get the OCS ws user
   * 
   * @return a string with the ocs user
   */
  public String getOcsUser() {
    String ocsUser = properties.getPropertiesAsString(OCS_USER);
    return ocsUser;
  }

  /**
   * Get the folder that contains all the files related to a project
   * 
   * @return a string with the path
   */
  public String getProjectsBaseFolder(String crp) {
    try {
      return crp.concat(File.separator).concat(properties.getPropertiesAsString(PROJECTS_BASE_FOLDER));
    } catch (Exception e) {
      LOG.error("there is not a base folder to upload the project files configured.");
    }
    return null;
  }

  /**
   * Get the folder where the project work plan should be uploaded
   * 
   * @return a string with the path
   */
  public String getProjectWorkplanFolder() {
    try {
      return properties.getPropertiesAsString(PROJECT_WORKPLAN_FOLDER);
    } catch (Exception e) {
      LOG.error("there is not a base folder to save the uploaded files configured.");
    }
    return null;
  }


  public String getPushApiKey() {
    try {
      return properties.getPropertiesAsString(PUSH_API_KEY);
    } catch (Exception e) {
      LOG.error("there is not an push api key configured.");
    }
    return "";
  }

  public String getPushAppId() {
    try {
      return properties.getPropertiesAsString(PUSH_APP_ID);
    } catch (Exception e) {
      LOG.error("there is not an push api key configured.");
    }
    return "";
  }

  public String getPushKeySecret() {
    try {
      return properties.getPropertiesAsString(PUSH_SECRETE_KEY);
    } catch (Exception e) {
      LOG.error("there is not an push api key configured.");
    }
    return "";
  }


  /**
   * Get the base folder where the uploaded files should be saved
   * 
   * @return a string with the path
   */
  public String getUploadsBaseFolder() {
    try {
      return properties.getPropertiesAsString(UPLOADS_BASE_FOLDER);
    } catch (Exception e) {
      LOG.error("there is not a base folder to save the uploaded files configured.");
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

  public boolean isAutoSaveActive() {
    String adminActive = properties.getPropertiesAsString(AUTO_SAVE_ACTIVE);
    if (adminActive == null) {
      LOG.error("There is not a auto save configured");
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
