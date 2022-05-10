/**
 * ***************************************************************
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
 * ***************************************************************
 */
package org.cgiar.ccafs.marlo.action.downloads;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.inject.Inject;

/**
 * @author Tony Shikali
 */
public class DownloadFileAction extends BaseAction {

  /**
  * 
  */
  private static final long serialVersionUID = 1L;

  private InputStream fileInputStream;


  private String crp = "";

  private String category = "";

  private Long id;

  private String filename = "";

  @Inject
  public DownloadFileAction(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {
    String filePath = "";
    if (category != null && category.equals("safeguard")) {
      filePath = config.getUploadsBaseFolder() + "/" + crp + "/" + "projects" + "/" + id + "/safeguard/" + filename;
    } else {
      filePath = config.getUploadsBaseFolder() + "/" + crp + "/" + category + "/" + id + "/baseLine/" + filename;
    }
    File file = new File(filePath);

    if (file.exists()) {
      fileInputStream = new FileInputStream(file);
      return SUCCESS;
    } else {
      this.addFieldError("org.cgiar.ccafs.marlo.action.downloads.DownloadFileAction",
        "Sorry, we couldn't download the file now. Please get in touch with support");
      return ERROR;
    }
  }

  /**
   * Get the value of category
   *
   * @return the value of category
   */
  public String getCategory() {
    return category;
  }

  /**
   * Get the value of crp
   *
   * @return the value of crp
   */
  public String getCrp() {
    return crp;
  }

  public InputStream getFileInputStream() {
    return fileInputStream;
  }

  /**
   * Get the value of filename
   *
   * @return the value of filename
   */
  public String getFilename() {
    return filename;
  }

  /**
   * Get the value of id
   *
   * @return the value of id
   */
  public Long getId() {
    return id;
  }

  /**
   * Set the value of category
   *
   * @param category new value of category
   */
  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * Set the value of crp
   *
   * @param crp new value of crp
   */
  public void setCrp(String crp) {
    this.crp = crp;
  }

  /**
   * Set the value of filename
   *
   * @param filename new value of filename
   */
  public void setFilename(String filename) {
    this.filename = filename;
  }

  /**
   * Set the value of id
   *
   * @param id new value of id
   */
  public void setId(Long id) {
    this.id = id;
  }

}
