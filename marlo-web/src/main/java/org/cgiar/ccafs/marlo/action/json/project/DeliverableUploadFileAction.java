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
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.model.FileDB;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.FileManager;

import java.io.File;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableUploadFileAction extends BaseAction {

  private static final long serialVersionUID = -4043508735788227016L;


  // Manager
  private FileDBManager fileDBManager;


  // Model
  private File file;


  private String fileContentType;


  private String fileFileName;
  private String fileType;
  private boolean saved;
  private long fileID;
  private long deliverableID;


  @Inject
  public DeliverableUploadFileAction(APConfig config, FileDBManager fileDBManager) {
    super(config);

    this.fileDBManager = fileDBManager;
    this.config = config;

  }


  @Override
  public String execute() throws Exception {

    FileDB fileDB = this.getFileDB(null, file, fileFileName, this.getDeliverablePath(fileType));
    saved = (fileDB.getId() != null) && fileDB.getId().longValue() > 0 ? true : false;
    FileManager.copyFile(file, this.getDeliverablePath(fileType) + fileDB.getFileName());
    fileID = fileDB.getId();
    return SUCCESS;
  }


  private String getDeliverablePath(String fileType) {
    return config.getUploadsBaseFolder() + File.separator + this.getDeliverableUrl(fileType) + File.separator;
  }


  public String getDeliverableUrl(String fileType) {
    return config.getDownloadURL() + "/" + this.getDeliverableUrlPath(fileType).replace('\\', '/');
  }


  public String getDeliverableUrlPath(String fileType) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + deliverableID + File.separator
      + "deliverable" + File.separator + fileType + File.separator;
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

  public String getFileType() {
    return fileType;
  }


  public boolean isSaved() {
    return saved;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    deliverableID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID))[0]));
    fileType = StringUtils.trim(((String[]) parameters.get(APConstants.FILE_TYPE_REQUEST))[0]);
  }

  public void setFile(File file) {
    this.file = file;
  }

  public void setFileContentType(String fileContentType) {
    this.fileContentType = fileContentType;
  }


  public void setFileFileName(String filename) {
    this.fileFileName = filename;
  }


  public void setFileID(long fileID) {
    this.fileID = fileID;
  }


  public void setFileType(String fileType) {
    this.fileType = fileType;
  }


  public void setSaved(boolean saved) {
    this.saved = saved;
  }


}

