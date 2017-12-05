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
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.model.FileDB;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.FileManager;

import java.io.File;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadBaseLineFileAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 4392148101400269106L;


  /**
   * @author Christian David Garcia Oviedo
   */


  public static Logger LOG = LoggerFactory.getLogger(UploadBaseLineFileAction.class);


  // Manager

  private FileDBManager fileDBManager;


  // Model
  private File file;
  private String fileContentType;
  private String fileFileName;
  private String outcomeID;


  private boolean saved;


  private long fileID;


  @Inject
  public UploadBaseLineFileAction(APConfig config, FileDBManager fileDBManager) {
    super(config);

    this.fileDBManager = fileDBManager;
    this.config = config;

  }


  @Override
  public String execute() throws Exception {

    FileDB fileDB = this.getFileDB(null, file, fileFileName, this.getFundingSourceFilePath());
    saved = (fileDB.getId() != null) && fileDB.getId().longValue() > 0 ? true : false;
    FileManager.copyFile(file, this.getFundingSourceFilePath() + fileDB.getFileName());
    fileID = fileDB.getId();
    return SUCCESS;
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


  private String getFundingSourceFilePath() {
    String upload = config.getUploadsBaseFolder();
    return upload + File.separator + this.getFundingSourceRelativePath() + File.separator;
  }


  public String getFundingSourceFileURL() {
    return config.getDownloadURL() + "/" + this.getFundingSourceFilePath().replace('\\', '/');
  }

  private String getFundingSourceRelativePath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + outcomeID + File.separator + "baseLine"
      + File.separator;
  }

  public String getOutcomeID() {
    return outcomeID;
  }


  public boolean isSaved() {
    return saved;
  }


  @Override
  public void prepare() throws Exception {

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


  public void setOutcomeID(String outcomeID) {
    this.outcomeID = outcomeID;
  }


  public void setSaved(boolean saved) {
    this.saved = saved;
  }


}

