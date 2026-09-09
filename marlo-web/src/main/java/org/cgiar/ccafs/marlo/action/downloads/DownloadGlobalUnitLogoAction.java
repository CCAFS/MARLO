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

package org.cgiar.ccafs.marlo.action.downloads;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadGlobalUnitLogoAction extends BaseAction {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(DownloadGlobalUnitLogoAction.class);
  private static final String LOGOS_RELATIVE_PATH = "globalUnits" + File.separator + "logos" + File.separator;
  private static final String USER_DIR_PROPERTY = "user.dir";
  private static final String LEGACY_LOGO_FOLDER = "marlo-web/src/main/webapp/global/images/crps";
  /**
   * The acronym arrives from an anonymous request and is turned into a file name, so anything that could step
   * outside the logos folder -- a path separator, a dot segment -- is rejected instead of sanitized. Every
   * acronym in global_units is covered by this pattern, including the ones that carry a space.
   */
  private static final Pattern VALID_ACRONYM_PATTERN = Pattern.compile("[A-Z0-9 _-]{1,50}");
  private static final int MAX_LOGGED_ACRONYM_LENGTH = 50;

  private transient InputStream fileInputStream;
  private String acronym;

  @Inject
  public DownloadGlobalUnitLogoAction(APConfig config) {
    super(config);
  }

  /**
   * Keeps a rejected acronym printable before it reaches the log. The value comes from an anonymous request, so
   * a CR or an LF in it would let one log call write what looks like a second, fabricated log line. Control
   * characters become a question mark and the value is cut to the length the acronym pattern would have
   * accepted; everything else is left alone, so a path traversal attempt is still readable in the log.
   * Replace this with LogSanitizer once CHG-COGNITO-AUTH-001 reaches staging.
   *
   * @param value the raw acronym as it arrived in the request, may be null.
   * @return a value safe to interpolate into a single log line, never null.
   */
  private String printable(String value) {
    if (value == null) {
      return "";
    }
    String stripped = value.replaceAll("[^\\x20-\\x7E]", "?");
    if (stripped.length() > MAX_LOGGED_ACRONYM_LENGTH) {
      return stripped.substring(0, MAX_LOGGED_ACRONYM_LENGTH) + "...(truncated)";
    }
    return stripped;
  }

  @Override
  public String execute() throws Exception {
    String normalizedAcronym = StringUtils.upperCase(StringUtils.trim(acronym), Locale.ROOT);
    if (StringUtils.isNotBlank(normalizedAcronym)) {
      if (VALID_ACRONYM_PATTERN.matcher(normalizedAcronym).matches()) {
        String uploadsBase = config.getUploadsBaseFolder();
        if (StringUtils.isNotBlank(uploadsBase)) {
          File logoFile = new File(uploadsBase, LOGOS_RELATIVE_PATH + normalizedAcronym + ".png");
          if (logoFile.exists() && logoFile.isFile()) {
            this.fileInputStream = new FileInputStream(logoFile);
            return SUCCESS;
          }
          // Debug, not warn: serving the default logo for a unit that has none uploaded is this action's
          // contract, not a degraded answer. Only a missing default.png below is worth a higher level.
          LOG.debug("globalUnitLogoDownload: logo not found in uploads for acronym {}", normalizedAcronym);
        } else {
          LOG.error("globalUnitLogoDownload: uploads base folder not configured");
        }

        String workspaceRoot = System.getProperty(USER_DIR_PROPERTY);
        File legacyLogoFile =
          new File(workspaceRoot, LEGACY_LOGO_FOLDER + File.separator + normalizedAcronym + ".png");
        if (legacyLogoFile.exists() && legacyLogoFile.isFile()) {
          this.fileInputStream = new FileInputStream(legacyLogoFile);
          return SUCCESS;
        }

        LOG.debug("globalUnitLogoDownload: logo not found in legacy folder for acronym {}", normalizedAcronym);
      } else {
        LOG.warn("globalUnitLogoDownload: the requested acronym is not a valid global unit acronym, so it was not"
          + " used to build a file path: {}", this.printable(normalizedAcronym));
      }
    }

    InputStream defaultStream =
      ServletActionContext.getServletContext().getResourceAsStream("/global/images/crps/default.png");
    if (defaultStream != null) {
      this.fileInputStream = defaultStream;
      return SUCCESS;
    }
    LOG.warn("globalUnitLogoDownload: the default logo is missing from the WAR, so no image is returned for the"
      + " acronym {}", this.printable(normalizedAcronym));
    return ERROR;
  }

  public InputStream getFileInputStream() {
    return fileInputStream;
  }

  public String getAcronym() {
    return acronym;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }
}
