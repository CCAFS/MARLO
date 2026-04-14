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

  private transient InputStream fileInputStream;
  private String acronym;

  @Inject
  public DownloadGlobalUnitLogoAction(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {
    if (StringUtils.isNotBlank(acronym)) {
      String normalizedAcronym = StringUtils.upperCase(StringUtils.trim(acronym), Locale.ROOT);
      String uploadsBase = config.getUploadsBaseFolder();
      if (StringUtils.isNotBlank(uploadsBase)) {
        File logoFile = new File(uploadsBase, LOGOS_RELATIVE_PATH + normalizedAcronym + ".png");
        if (logoFile.exists() && logoFile.isFile()) {
          this.fileInputStream = new FileInputStream(logoFile);
          return SUCCESS;
        }
        LOG.warn("globalUnitLogoDownload: logo not found in uploads for acronym {}", normalizedAcronym);
      } else {
        LOG.error("globalUnitLogoDownload: uploads base folder not configured");
      }

      String workspaceRoot = System.getProperty(USER_DIR_PROPERTY);
      File legacyLogoFile = new File(workspaceRoot, LEGACY_LOGO_FOLDER + File.separator + normalizedAcronym + ".png");
      if (legacyLogoFile.exists() && legacyLogoFile.isFile()) {
        this.fileInputStream = new FileInputStream(legacyLogoFile);
        return SUCCESS;
      }

      LOG.warn("globalUnitLogoDownload: logo not found in legacy folder for acronym {}", normalizedAcronym);
    }

    InputStream defaultStream = ServletActionContext.getServletContext().getResourceAsStream("/global/images/crps/default.png");
    if (defaultStream != null) {
      this.fileInputStream = defaultStream;
      return SUCCESS;
    }
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
