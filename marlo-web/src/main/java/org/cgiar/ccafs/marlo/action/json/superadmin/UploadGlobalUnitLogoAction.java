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

package org.cgiar.ccafs.marlo.action.json.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.File;
import java.awt.image.BufferedImage;
import java.util.Locale;

import javax.inject.Inject;
import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadGlobalUnitLogoAction extends BaseAction {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(UploadGlobalUnitLogoAction.class);

  private static final String LOGOS_RELATIVE_PATH = "globalUnits" + File.separator + "logos" + File.separator;

  private File file;
  private String fileContentType;
  private String fileFileName;
  private String acronym;

  private boolean saved;
  private String logoUrl;
  private String message;

  @Inject
  public UploadGlobalUnitLogoAction(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {
    saved = false;
    message = null;

    if (StringUtils.isBlank(acronym)) {
      LOG.warn("globalUnitLogoUpload: acronym is blank, skipping upload");
      message = "Set the acronym before uploading a logo.";
      return SUCCESS;
    }

    if (file == null || !file.exists()) {
      LOG.warn("globalUnitLogoUpload: no file received for acronym {}", acronym);
      message = "No file was received by the server.";
      return SUCCESS;
    }

    String normalizedAcronym = StringUtils.upperCase(StringUtils.trim(acronym), Locale.ROOT);
    String uploadsBase = config.getUploadsBaseFolder();
    if (StringUtils.isBlank(uploadsBase)) {
      LOG.error("globalUnitLogoUpload: uploads base folder not configured");
      message = "Uploads base folder is not configured.";
      return SUCCESS;
    }

    File targetDir = new File(uploadsBase, LOGOS_RELATIVE_PATH);
    if (!targetDir.exists() && !targetDir.mkdirs()) {
      LOG.error("globalUnitLogoUpload: unable to create target directory {}", targetDir.getAbsolutePath());
      message = "Unable to create the target upload directory.";
      return SUCCESS;
    }

    String finalFileName = normalizedAcronym + ".png";
    File targetFile = new File(targetDir, finalFileName);

    BufferedImage sourceImage = ImageIO.read(file);
    if (sourceImage == null) {
      LOG.warn("globalUnitLogoUpload: uploaded file is not a readable image for acronym {}", normalizedAcronym);
      message = "The uploaded file is not a valid image.";
      return SUCCESS;
    }

    boolean written = ImageIO.write(sourceImage, "png", targetFile);
    if (!written || !targetFile.exists() || targetFile.length() == 0) {
      LOG.error("globalUnitLogoUpload: image write failed for source {} and target {}", file.getAbsolutePath(),
        targetFile.getAbsolutePath());
      message = "The server could not save the uploaded logo file.";
      return SUCCESS;
    }

    this.acronym = normalizedAcronym;
    this.logoUrl = this.getBaseUrl() + "/data/globalUnitLogo.do?acronym=" + normalizedAcronym;
    saved = true;
    message = "Logo uploaded successfully.";

    return SUCCESS;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public String getFileContentType() {
    return fileContentType;
  }

  public void setFileContentType(String fileContentType) {
    this.fileContentType = fileContentType;
  }

  public String getFileFileName() {
    return fileFileName;
  }

  public String getMessage() {
    return message;
  }

  public void setFileFileName(String fileFileName) {
    this.fileFileName = fileFileName;
  }

  public String getAcronym() {
    return acronym;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public boolean isSaved() {
    return saved;
  }

  public void setSaved(boolean saved) {
    this.saved = saved;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }
}
