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
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SafeguardsManager;
import org.cgiar.ccafs.marlo.data.model.FileDB;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.Safeguards;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.FileManager;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UploadSafeguardFileAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 4392148101400269106L;


  /**
   * @author Christian David Garcia Oviedo
   */


  public static Logger LOG = LoggerFactory.getLogger(UploadSafeguardFileAction.class);


  // Manager

  private FileDBManager fileDBManager;
  private SafeguardsManager safeguardsManager;


  // Model
  private File file;
  private String fileContentType;
  private String fileFileName;
  private String safeguardID;
  private Long projectID;
  private Long phaseID;
  private FileDB fileDB;

  private boolean saved;
  private long fileID;

  private ProjectManager projectManager;
  private ProjectInfoManager projectInfoManager;
  private SafeguardsManager safeguardManager;


  @Inject
  public UploadSafeguardFileAction(APConfig config, FileDBManager fileDBManager, SafeguardsManager safeguardsManager,
    ProjectManager projectManager, ProjectInfoManager projectInfoManager) {
    super(config);
    this.safeguardsManager = safeguardsManager;
    this.fileDBManager = fileDBManager;
    this.projectManager = projectManager;
    this.projectInfoManager = projectInfoManager;
    this.config = config;
  }


  @Override
  public String execute() throws Exception {
    fileFileName = fileFileName.replace(' ', '_');
    fileDB = this.getFileDB(null, file, fileFileName, this.getBaseLineFilePath());
    saved = (fileDB.getId() != null) && fileDB.getId().longValue() > 0 ? true : false;
    FileManager.copyFile(file, this.getBaseLineFilePath() + fileDB.getFileName());
    fileID = fileDB.getId();
    try {

      Safeguards safeguard = new Safeguards();
      if (safeguardID != null) {
        this.saveSafeguard();
      } else {
        // If safeguard ID == null then try to get project ID parameter
        if (projectID != null) {
          Project project = projectManager.getProjectById(projectID);
          if (project != null && phaseID != null) {
            ProjectInfo projectInfo = projectInfoManager.getProjectInfoByProjectPhase(projectID, phaseID);
            if (projectInfo != null) {
              project.setProjectInfo(projectInfo);
            }
            List<Safeguards> safeguards = safeguardManager.findAll().stream().filter(s -> s != null
              && s.getProject() != null && s.getProject().getId() != null && s.getProject().getId().equals(projectID))
              .collect(Collectors.toList());
            if (safeguards != null && !safeguards.isEmpty() && safeguards.get(0) != null) {
              safeguard = safeguards.get(0);
              safeguardID = safeguard.getId() + "";
              this.saveSafeguard();
            }
          }
        }

      }
    } catch (Exception e) {
      LOG.error("unable to get phaseID", e);
    }
    return SUCCESS;
  }

  private String getBaseLineFilePath() {
    String upload = config.getUploadsBaseFolder();
    return upload + File.separator + this.getBaseLineRelativePath() + File.separator;
  }


  public String getBaseLineFileURL() {
    return config.getDownloadURL() + "/" + this.getBaseLineFilePath().replace('\\', '/');
  }


  private String getBaseLineRelativePath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + safeguardID + File.separator
      + "safeguard" + File.separator;
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

  public String getSafeguardID() {
    return safeguardID;
  }

  public boolean isSaved() {
    return saved;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    try {
      if (parameters.get(APConstants.PROJECT_ID).isDefined()) {
        projectID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_ID).getMultipleValues()[0]));
      }
    } catch (Exception e) {
      LOG.info("error getting projectID parameter", e);
    }
    try {
      if (parameters.get(APConstants.PHASE_ID).isDefined()) {
        phaseID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
      }
    } catch (Exception e) {
      LOG.info("error getting projectID parameter", e);
    }
  }

  public void saveSafeguard() {
    if (safeguardID != null) {
      if (safeguardsManager.existSafeguards(Long.parseLong(safeguardID))) {
        Safeguards safeguard = new Safeguards();
        safeguard = safeguardsManager.getSafeguardsById(Long.parseLong(safeguardID));
        if (safeguard != null && safeguard.getId() != null && fileDB != null) {
          safeguard.setFile(fileDB);
          safeguard.setLink(this.config.getDownloadURL() + "/file.do?crp=" + this.getCurrentGlobalUnit().getAcronym()
            + "&category=" + "safeguard&id=" + safeguardID + "&filename=" + fileDB.getFileName());
        } else {
          safeguard.setFile(null);
        }
        safeguardsManager.saveSafeguards(safeguard);
      }
    }
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


  public void setSafeguardID(String safeguardID) {
    this.safeguardID = safeguardID;
  }

  public void setSaved(boolean saved) {
    this.saved = saved;
  }


}

