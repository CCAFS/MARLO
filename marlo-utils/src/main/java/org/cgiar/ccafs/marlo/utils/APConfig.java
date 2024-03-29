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

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@Named
public class APConfig {

  // Logging.
  private static final Logger LOG = LoggerFactory.getLogger(APConfig.class);
  @Value("${mysql.host}")
  private String MYSQL_HOST;
  @Value("${mysql.user}")
  private String MYSQL_USER;
  @Value("${mysql.password}")
  private String MYSQL_PASSWORD;
  @Value("${mysql.database}")
  private String MYSQL_DATABASE;
  @Value("${mysql.port}")
  private String MYSQL_PORT;
  @Value("${mysql.show_sql}")
  private String MYSQL_SHOW_SQL;
  // Email Parameters
  @Value("${email.user}")
  private String EMAIL_USER;
  @Value("${email.notification}")
  private String EMAIL_NOTIFICATION;
  @Value("${email.password}")
  private String EMAIL_PASSWORD;
  @Value("${email.host}")
  private String EMAIL_HOST;
  @Value("${email.port}")
  private Integer EMAIL_PORT;
  @Value("$email.auth}")
  private String EMAIL_AUTH;
  @Value("${email.starttls}")
  private String EMAIL_STARTTLS;
  @Value("${email.userbackup}")
  private String EMAIL_USER_BACKUP;
  @Value("${email.notificationbackup}")
  private String EMAIL_NOTIFICATION_BACKUP;
  @Value("${email.passwordbackup}")
  private String EMAIL_PASSWORD_BACKUP;
  @Value("${email.hostbackup}")
  private String EMAIL_HOST_BACKUP;
  @Value("${email.portbackup}")
  private Integer EMAIL_PORT_BACKUP;
  @Value("${email.starttlsbackup}")
  private String EMAIL_STARTTLS_BACKUP;
  @Value("$email.authbackup}")
  private String EMAIL_AUTH_BACKUP;
  @Value("${email.pmu}")
  private String EMAIL_PMU;
  @Value("${google.api.key}")
  private String GOOGLE_API_KEY;
  @Value("${pusher.api.key}")
  private String PUSH_API_KEY;
  @Value("${pusher.api.appid}")
  private String PUSH_APP_ID;
  @Value("${pusher.api.privatekey}")
  private String PUSH_SECRETE_KEY;
  @Value("${file.downloads}")
  private String FILE_DOWNLOADS;
  @Value("${file.uploads.projectsFolder}")
  private String PROJECTS_BASE_FOLDER;
  @Value("${file.uploads.project.bilateralProposalFolder}")
  private String PROJECT_BILATERAL_PROPOSAL_FOLDER;
  @Value("${file.uploads.project.bilateralPAnualReport}")
  private String PROJECT_BILATERAL_ANUAL_REPORT_FOLDER;
  @Value("${file.uploads.project.WorkplanFolder}")
  private String PROJECT_WORKPLAN_FOLDER;
  @Value("${autosave.active}")
  private String AUTO_SAVE_ACTIVE;
  @Value("${file.uploads.baseFolder}")
  private String UPLOADS_BASE_FOLDER;
  @Value("${marlo.production}")
  private String PRODUCTION;
  @Value("${marlo.debug}")
  private String DEBUG_MODE;
  @Value("${marlo.admin.active}")
  private String ADMIN_ACTIVE;
  @Value("${marlo.impactPathway.active}")
  private String IMPACT_PATHWAY_ACTIVE;
  @Value("${ocs.link}")
  private String OCS_LINK;
  @Value("${ocs.password}")
  private String OCS_PASSWORD;
  @Value("${ocs.user}")
  private String OCS_USER;
  @Value("${autosave.folder}")
  private String AUTOSAVE_FOLDER;
  @Value("${marlo.baseUrl}")
  private String BASE_URL;
  @Value("${file.uploads.fundingSourceFolder}")
  private String FUNDING_SOURCE_FOLDER;

  // Add public user to call the Clarisa Rest Services
  @Value("${clarisa.publicUser}")
  private String CLARISA_PUBLIC_USER;
  @Value("${clarisa.publicPass}")
  private String CLARISA_PUBLIC_PASSWORD;

  // Path to get the IP maps database
  @Value("${clarisa.mapDatabase.path}")
  private String CLARISA_MAP_DATABASE_PATH;


  // Path to get the base URL of media hosted in the CDN
  @Value("${cdn.url}")
  private String CDN_URL;

  // Path for Summaries Links
  @Value("${clarisa.summariesPDF}")
  private String CLARISA_SUMMARIES_PDF;

  // Clarisa WOS service link
  @Value("${clarisa.wos.link}")
  private String CLARISA_WOS_LINK;
  // Clarisa WOS service link 2
  @Value("${clarisa.wos.link2}")
  private String CLARISA_WOS_LINK2;

  // Clarisa user for WOS synchronization
  @Value("${clarisa.wos.user}")
  private String CLARISA_WOS_USER;
  @Value("${clarisa.wos.password}")
  private String CLARISA_WOS_PASSWORD;

  // Clarisa API Configurations
  @Value("${clarisa.api.host}")
  private String CLARISA_API_HOST;
  @Value("${clarisa.api.username}")
  private String CLARISA_API_USERNAME;
  @Value("${clarisa.api.password}")
  private String CLARISA_API_PASSWORD;

  @Value("${recaptcha.site.key}")
  private String RECAPTCHAT_SITE_KEY;

  public APConfig() {
  }

  /**
   * Get the folder where the bilateral project contract proposal should be
   * loaded
   *
   * @return a string with the path
   */
  public String getAnualReportFolder() {
    if (PROJECT_BILATERAL_ANUAL_REPORT_FOLDER == null) {
      LOG.error("there is not a base folder to save the uploaded files configured.");
      /** Should we just throw an exception? **/
      return null;
    }
    return PROJECT_BILATERAL_ANUAL_REPORT_FOLDER;
  }

  /**
   * Get the folder destination to save the autosave temporal files
   *
   * @return a string with the auto save folder destination.
   */
  public String getAutoSaveFolder() {

    if (AUTOSAVE_FOLDER == null) {
      LOG.error("there is not a base folder to save the uploaded files configured.");
      /** Should we just throw an exception? **/
      return null;
    }
    return AUTOSAVE_FOLDER;
  }

  /**
   * Return the base url previously added in the configuration file.
   *
   * @return The Base Url in the following format: http://baseurl or
   *         https://baseurl.
   */
  public String getBaseUrl() {
    if (BASE_URL == null) {
      LOG.error("There is not a base url configured");
      return null;
    }
    String base = new String(BASE_URL);
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
   * Get the base url where media is hosted in cdn
   *
   * @return a string with the url
   */
  public String getBaseUrlCdn() {

    if (CDN_URL == null || CDN_URL.equals("")) {

      // LOG.warn("there is not a base url where the media hosted in CDN, using current URL instead");
      return null;
    }

    return CDN_URL;
  }

  /**
   * Get the folder where the bilateral project contract proposal should be
   * loaded
   *
   * @return a string with the path
   */
  public String getBilateralProjectContractProposalFolder() {

    if (PROJECT_BILATERAL_PROPOSAL_FOLDER == null) {

      LOG.error("there is not a base folder to save the uploaded files configured.");
      return null;
    }

    return PROJECT_BILATERAL_PROPOSAL_FOLDER;
  }

  public String getClarisa_summaries_pdf() {
    if (CLARISA_SUMMARIES_PDF == null) {
      LOG.error("there is not an CLARISA_SUMMARIES_PDF configured.");
      return null;
    }
    return CLARISA_SUMMARIES_PDF;
  }

  public String getClarisaAPIHost() {
    if (CLARISA_API_HOST == null) {
      LOG.error("there is not a Clarisa API host configured.");
      return null;
    }
    return CLARISA_API_HOST;
  }

  public String getClarisaAPIPassword() {
    if (CLARISA_API_PASSWORD == null) {
      LOG.error("there is not a Clarisa API password configured.");
      return null;
    }
    return CLARISA_API_PASSWORD;
  }

  public String getClarisaAPIUsername() {
    if (CLARISA_API_USERNAME == null) {
      LOG.error("there is not a Clarisa API username configured.");
      return null;
    }
    return CLARISA_API_USERNAME;
  }

  public String getClarisaMapDatabase() {

    if (CLARISA_MAP_DATABASE_PATH == null) {
      LOG.error("there is not an clarisa map database configured.");
      return "";
    }

    return CLARISA_MAP_DATABASE_PATH;
  }

  public String getClarisaPassword() {

    if (CLARISA_PUBLIC_PASSWORD == null) {
      LOG.error("there is not an clarisa public user configured.");
      return "";
    }

    return CLARISA_PUBLIC_PASSWORD;
  }

  public String getClarisaUser() {

    if (CLARISA_PUBLIC_USER == null) {
      LOG.error("there is not an clarisa public user configured.");
      return "";
    }

    return CLARISA_PUBLIC_USER;
  }

  /**
   * Get the Clarisa WOS service link
   *
   * @return a string with the WOS link
   */
  public String getClarisaWOSLink() {
    if (CLARISA_WOS_LINK == null) {
      LOG.error("there is not a Clarisa WOS service link configured.");
      return null;
    }
    return CLARISA_WOS_LINK;
  }

  /**
   * Get the Clarisa WOS service link 2
   *
   * @return a string with the WOS link 2
   */
  public String getClarisaWOSLink2() {
    if (CLARISA_WOS_LINK2 == null) {
      LOG.error("there is not a Clarisa WOS service link 2 configured.");
      return null;
    }
    return CLARISA_WOS_LINK2;
  }

  /**
   * Get the Clarisa WOS password
   *
   * @return a string with the Clarisa WOS password
   */
  public String getClarisaWOSPassword() {
    if (CLARISA_WOS_PASSWORD == null) {
      LOG.error("there is not a Clarisa WOS password configured.");
      return null;
    }
    return CLARISA_WOS_PASSWORD;
  }

  /**
   * Get the Clarisa WOS user
   *
   * @return a string with the Clarisa WOS user
   */
  public String getClarisaWOSUser() {
    if (CLARISA_WOS_USER == null) {
      LOG.error("there is not a Clarisa WOS user configured.");
      return null;
    }
    return CLARISA_WOS_USER;
  }

  /**
   * Get the URL where the users can download the uploaded files
   *
   * @return a string with the path
   */
  public String getDownloadURL() {
    if (FILE_DOWNLOADS == null) {
      LOG.error("There is not a downloads url configured");
      return null;
    }
    String downloadsURL = new String(FILE_DOWNLOADS);
    while (downloadsURL != null && downloadsURL.endsWith("/")) {
      downloadsURL = downloadsURL.substring(0, downloadsURL.length() - 1);
    }
    /*
     * if (!downloadsURL.startsWith("https://")) { downloadsURL = "http://"
     * + downloadsURL; return downloadsURL;}
     */
    return downloadsURL;
  }

  public String getEmail_auth() {
    if (EMAIL_AUTH == null) {
      LOG.error("there is not an email authentication configured.");
      return null;
    }
    return EMAIL_AUTH;
  }

  public String getEmail_authBackup() {
    if (EMAIL_AUTH_BACKUP == null) {
      LOG.error("there is not an email authentication backup configured.");
      return null;
    }
    return EMAIL_AUTH_BACKUP;
  }

  /**
   * get the email.configuration tag in the configuration file
   *
   * @return string whit the email that is in the configuration file.
   */

  public String getEmail_notificaction_backup() {

    if (EMAIL_NOTIFICATION_BACKUP == null) {
      LOG.error("there is not an email user backup configured.");
      return null;
    }
    return EMAIL_NOTIFICATION_BACKUP;
  }

  public String getEmail_password_backup() {

    if (EMAIL_PASSWORD_BACKUP == null) {
      LOG.error("there is not an email password backup configured.");
      return null;
    }

    return EMAIL_PASSWORD_BACKUP;
  }

  public String getEmail_pmu() {

    if (EMAIL_PMU == null) {
      LOG.error("there is not an email password backup configured.");
      return null;
    }

    return EMAIL_PMU;
  }

  public String getEmail_starttls() {

    if (EMAIL_STARTTLS == null) {

      LOG.error("there is not an email starttls configured.");
      return null;
    }

    return EMAIL_STARTTLS;
  }

  public String getEmail_starttlsbackup() {

    if (EMAIL_STARTTLS_BACKUP == null) {

      LOG.error("there is not an email starttls backup configured.");
      return null;
    }

    return EMAIL_STARTTLS_BACKUP;
  }

  public String getEmail_user_backup() {

    if (EMAIL_USER_BACKUP == null) {

      LOG.error("there is not an email user backup configured.");
      return null;
    }

    return EMAIL_USER_BACKUP;
  }

  public String getEmailHost() {

    if (EMAIL_HOST == null) {

      LOG.error("there is not an email host configured.");
      return null;
    }

    return EMAIL_HOST;
  }

  public String getEmailHostbackup() {

    if (EMAIL_HOST_BACKUP == null) {

      LOG.error("there is not an email host backup configured.");
      return null;
    }

    return EMAIL_HOST_BACKUP;
  }

  /**
   * get the email.configuration tag in the configuration file
   *
   * @return string whit the email that is in the configuration file.
   */
  public String getEmailNotification() {

    if (EMAIL_NOTIFICATION == null) {
      LOG.error("there is not an email user configured.");
      return null;
    }
    return EMAIL_NOTIFICATION;
  }

  public String getEmailPassword() {

    if (EMAIL_PASSWORD == null) {
      LOG.error("there is not an email password configured.");
      return null;
    }

    return EMAIL_PASSWORD;
  }

  public int getEmailPort() {

    if (EMAIL_PORT == null) {
      LOG.error("there is not an email port configured.");
      return 0;
    }
    return EMAIL_PORT;

  }

  public int getEmailPortbackup() {

    if (EMAIL_PORT_BACKUP == null) {
      LOG.error("there is not an email port backup configured.");
      return 0;
    }
    return EMAIL_PORT_BACKUP;

  }

  public String getEmailUsername() {

    if (EMAIL_USER == null) {

      LOG.error("there is not an email user configured.");
      return null;
    }

    return EMAIL_USER;
  }

  public String getFundingSourceFolder(String crp) {

    if (FUNDING_SOURCE_FOLDER == null) {
      LOG.error("there is not a base folder to upload the funding source files configured.");
      return null;
    }

    return crp.concat(File.separator).concat(FUNDING_SOURCE_FOLDER);

  }

  public String getGoogleApiKey() {

    if (GOOGLE_API_KEY == null) {
      LOG.error("there is not an google api key configured.");
      return "";
    }

    return GOOGLE_API_KEY;
  }

  /**
   * Get the folder where the bilateral project contract proposal should be
   * loaded
   *
   * @return a string with the path
   */
  public String getMysqlDatabase() {
    if (MYSQL_DATABASE == null) {
      LOG.error("there is no value configured for the Mysql database.");
      /** Should we just throw an exception? **/
      return null;
    }
    return MYSQL_DATABASE;
  }

  /**
   * Get the OCS ws link
   *
   * @return a string with the ocs link
   */
  public String getOcsLink() {
    if (OCS_LINK == null) {
      LOG.error("there is not an Ocs link configured.");
      return null;
    }
    return OCS_LINK;
  }

  /**
   * Get the OCS ws password
   *
   * @return a string with the ocs password
   */
  public String getOcsPassword() {
    if (OCS_PASSWORD == null) {
      LOG.error("there is not an Ocs password configured.");
      return null;
    }
    return OCS_PASSWORD;
  }

  /**
   * Get the OCS ws user
   *
   * @return a string with the ocs user
   */
  public String getOcsUser() {
    if (OCS_USER == null) {
      LOG.error("there is not an Ocs user configured.");
      return null;
    }
    return OCS_USER;
  }

  /**
   * Get the folder that contains all the files related to a project
   *
   * @return a string with the path
   */
  public String getProjectsBaseFolder(String crp) {

    if (PROJECTS_BASE_FOLDER == null) {
      LOG.error("there is not a base folder to upload the project files configured.");
      return null;
    }

    return crp.concat(File.separator).concat(PROJECTS_BASE_FOLDER);

  }

  /**
   * Get the folder where the project work plan should be uploaded
   *
   * @return a string with the path
   */
  public String getProjectWorkplanFolder() {

    if (PROJECT_WORKPLAN_FOLDER == null) {

      LOG.error("there is not a base folder to save the uploaded files configured.");
      return null;
    }

    return PROJECT_WORKPLAN_FOLDER;
  }

  public String getPushApiKey() {

    if (PUSH_API_KEY == null) {

      LOG.error("there is not an push api key configured.");
      return "";
    }

    return PUSH_API_KEY;
  }

  public String getPushAppId() {

    if (PUSH_APP_ID == null) {

      LOG.error("there is not an push api key configured.");
      return "";
    }

    return PUSH_APP_ID;
  }

  public String getPushKeySecret() {

    if (PUSH_SECRETE_KEY == null) {

      LOG.error("there is not an push api key configured.");
      return "";
    }

    return PUSH_SECRETE_KEY;
  }

  /**
   * Get recaptcha account key
   * loaded
   *
   * @return a string with the path
   */
  public String getRecaptchatSiteKey() {
    if (RECAPTCHAT_SITE_KEY == null) {
      LOG.error("there is not a base folder to save the uploaded files configured.");
      /** Should we just throw an exception? **/
      return null;
    }
    return RECAPTCHAT_SITE_KEY;
  }

  /**
   * Get the base folder where the uploaded files should be saved
   *
   * @return a string with the path
   */
  public String getUploadsBaseFolder() {

    if (UPLOADS_BASE_FOLDER == null) {

      LOG.error("there is not a base folder to save the uploaded files configured.");
      return null;
    }

    return UPLOADS_BASE_FOLDER;
  }

  /**
   * Get the flag that indicate if the admin section is active according to
   * the variable in the configuration file.
   *
   * @return a boolean indicating if it is active.
   */
  public boolean isAdminActive() {
    if (ADMIN_ACTIVE == null) {
      LOG.error("There is not a Admin active configured");
      return false;
    }
    return ADMIN_ACTIVE.equals("true");
  }

  public boolean isAutoSaveActive() {

    if (AUTO_SAVE_ACTIVE == null) {
      LOG.error("There is not a auto save configured");
      return false;
    }
    return AUTO_SAVE_ACTIVE.equals("true");
  }

  /**
   * If we are activate the Marlo debug mode.
   *
   * @return true if debug mode in marlo is active, false otherwise.
   */
  public boolean isDebug() {
    if (DEBUG_MODE == null) {
      LOG.error("There is not a debug mode active configured");
      return false;
    }
    return DEBUG_MODE.equals("true");
  }

  /**
   * Get the flag that indicate if the Impact Pathway section is active
   * according to the variable in the configuration file.
   *
   * @return a boolean indicating if it is active.
   */
  public boolean isImpactPathwayActive() {
    if (IMPACT_PATHWAY_ACTIVE == null) {
      LOG.error("There is not a ImpactPathway active configured");
      return false;
    }
    return IMPACT_PATHWAY_ACTIVE.equals("true");
  }

  /**
   * If we are running Marlo in production or testing mode.
   *
   * @return true if Marlo is running in production mode, false if is run in
   *         testing mode.
   */
  public boolean isProduction() {
    if (PRODUCTION == null) {
      LOG.error("There is not a production/testing mode active configured");
      return false;
    }
    return PRODUCTION.equals("true");
  }

  /**
   * Allow sql statements to be logged
   */
  public boolean isShowSql() {
    if (MYSQL_SHOW_SQL == null) {
      LOG.warn("The property: " + MYSQL_SHOW_SQL + ", has not been configured and therefore will be set to false.");
      return false;
    }
    return MYSQL_SHOW_SQL.equals("true");
  }

}
