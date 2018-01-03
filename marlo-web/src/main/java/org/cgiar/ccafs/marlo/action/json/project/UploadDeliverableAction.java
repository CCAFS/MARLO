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


package org.cgiar.ccafs.marlo.action.json.project;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableDataSharingFileManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableFile;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.FileManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadDeliverableAction extends BaseAction {

  /**
   * @author Christian David Garcia Oviedo
   */
  private static final long serialVersionUID = -242932821656538581L;


  public static Logger LOG = LoggerFactory.getLogger(UploadDeliverableAction.class);


  // Manager
  private DeliverableManager deliverableManager;
  private DeliverableDataSharingFileManager deliverableFileManager;


  // Model
  private File file;
  private String fileContentType;
  private String fileFileName;
  private String deliverableID;
  private String projectID;
  private Deliverable deliverable;
  private Map<String, Object> fileInfo;
  private boolean saved;
  private long fileID;

  @Inject
  public UploadDeliverableAction(APConfig config, DeliverableManager deliverableManager,
    DeliverableDataSharingFileManager deliverableFileManager) {
    super(config);
    this.deliverableManager = deliverableManager;
    this.deliverableFileManager = deliverableFileManager;
    this.config = config;

  }

  @Override
  public String execute() throws Exception {
    boolean fileCopied = false;

    // Validate if project parameter exists in the URL.

    deliverable = deliverableManager.getDeliverableById(Integer.parseInt(deliverableID));


    // finalPath.append(config.getDeliverablesFilesPath());

    DeliverableFile delFile = new DeliverableFile();
    delFile.setHosted(APConstants.DELIVERABLE_FILE_LOCALLY_HOSTED_STR);
    delFile.setSize(this.file.length());
    delFile.setName(fileFileName);

    DeliverableDataSharingFile fileDatasharing = new DeliverableDataSharingFile();

    fileDatasharing.setFile(this.getFileDB(null, file, fileFileName, this.getDeliverableFilePath()));


    fileCopied = FileManager.copyFile(file, this.getDeliverableFilePath() + fileFileName);

    fileDatasharing.setDeliverable(deliverable);

    fileDatasharing.setTypeId(new Integer(APConstants.DELIVERABLE_FILE_LOCALLY_HOSTED));
    fileDatasharing = deliverableFileManager.saveDeliverableDataSharingFile(fileDatasharing);
    fileID = fileDatasharing.getId();
    saved = (fileID != -1) && fileCopied ? true : false;
    fileInfo = new HashMap<String, Object>();
    fileInfo.put("fileSaved", saved);
    fileInfo.put("fileID", fileID);
    return SUCCESS;
  }


  private String getDeliverableFilePath() {
    String upload = config.getUploadsBaseFolder();
    return upload + File.separator + this.getDeliverableFileRelativePath() + File.separator;
  }

  private String getDeliverableFileRelativePath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + projectID + File.separator
      + "deliverableDataSharing" + File.separator;
  }


  public String getDeliverableFileURL() {
    return config.getDownloadURL() + "/" + this.getDeliverableFilePath().replace('\\', '/');
  }


  public String getDeliverableID() {
    return deliverableID;
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


  public Map<String, Object> getFileInfo() {
    return fileInfo;
  }


  public String getProjectID() {
    return projectID;
  }


  public void setDeliverableID(String deliverableID) {
    this.deliverableID = deliverableID;
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


  public void setFileInfo(Map<String, Object> fileInfo) {
    this.fileInfo = fileInfo;
  }


  public void setProjectID(String projectID) {
    this.projectID = projectID;
  }


}

