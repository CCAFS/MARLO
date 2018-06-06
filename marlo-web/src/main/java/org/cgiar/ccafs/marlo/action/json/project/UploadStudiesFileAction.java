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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.FileDB;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.FileManager;

import java.io.File;

import javax.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class UploadStudiesFileAction extends BaseAction {


  private static final long serialVersionUID = 2672626953010451363L;


  private File file;


  private String fileContentType;


  private String fileFileName;


  private String actionName;


  private boolean saved;


  private long fileID;


  @Inject
  public UploadStudiesFileAction(APConfig config) {
    super(config);
  }


  @Override
  public String execute() throws Exception {
    fileFileName = fileFileName.replace(' ', '_');
    FileDB fileDB = this.getFileDB(null, file, fileFileName, this.getStudiesFileResearchPath());
    saved = (fileDB.getId() != null) && fileDB.getId().longValue() > 0 ? true : false;
    FileManager.copyFile(file, this.getStudiesFileResearchPath() + fileDB.getFileName());
    fileID = fileDB.getId();
    return SUCCESS;
  }


  @Override
  public String getActionName() {
    return actionName;
  }


  public File getFile() {
    return file;
  }


  public String getFileContentType() {
    return fileContentType;
  }

  public String getFileFileName() {
    return fileFileName;
  }

  public long getFileID() {
    return fileID;
  }

  public String getPath() {
    return config.getDownloadURL() + "/" + this.getStudiesSourceFolder().replace('\\', '/');
  }

  private String getStudiesFileResearchPath() {
    String upload = config.getUploadsBaseFolder();
    return upload + File.separator + this.getStudiesSourceFolder() + File.separator;
  }

  private String getStudiesSourceFolder() {
    return APConstants.STUDIES_FOLDER.concat(File.separator).concat(this.getCrpSession()).concat(File.separator)
      .concat(File.separator).concat(actionName.replace("/", "_")).concat(File.separator);
  }

  public boolean isSaved() {
    return saved;
  }

  public void setActionName(String actionName) {
    this.actionName = actionName;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public void setFileContentType(String fileContentType) {
    this.fileContentType = fileContentType;
  }

  public void setFileFileName(String fileFileName) {
    this.fileFileName = fileFileName;
  }


  public void setFileID(long fileID) {
    this.fileID = fileID;
  }

  public void setSaved(boolean saved) {
    this.saved = saved;
  }

}
