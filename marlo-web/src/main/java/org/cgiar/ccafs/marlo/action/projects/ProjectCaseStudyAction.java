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
package org.cgiar.ccafs.marlo.action.projects;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CaseStudyManager;
import org.cgiar.ccafs.marlo.data.manager.CaseStudyProjectManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.CaseStudy;
import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.FileManager;
import org.cgiar.ccafs.marlo.validation.projects.ProjectHighLightValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sebastian Amariles G.
 * @author Christian Garcia O.
 */
public class ProjectCaseStudyAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = -5209003027874233584L;
  // LOG
  private static Logger LOG = LoggerFactory.getLogger(ProjectCaseStudyAction.class);
  private CrpManager crpManager;


  private Crp loggedCrp;

  private ProjectHighLightValidator highLightValidator;
  private String transaction;


  private AuditLogManager auditLogManager;


  // Manager
  private ProjectManager projectManager;


  private CaseStudyManager caseStudyManager;
  private CaseStudyProjectManager caseStudyProjectManager;

  private FileDBManager fileDBManager;

  private File file;
  // private ProjectHighLightValidator validator;
  private String fileFileName;
  private String contentType;
  // Model for the back-end
  private CaseStudy caseStudy;
  private Project project;
  // Model for the front-end
  private long caseStudyID;
  private long projectID;

  private List<Project> myProjects;

  @Inject
  public ProjectCaseStudyAction(APConfig config, ProjectManager projectManager, CaseStudyManager highLightManager,
    CrpManager crpManager, AuditLogManager auditLogManager, FileDBManager fileDBManager,
    CaseStudyProjectManager projectHighligthTypeManager, ProjectHighLightValidator highLightValidator) {
    super(config);
    this.projectManager = projectManager;
    this.caseStudyManager = highLightManager;

    this.auditLogManager = auditLogManager;
    this.crpManager = crpManager;
    this.fileDBManager = fileDBManager;
    this.highLightValidator = highLightValidator;

    this.caseStudyProjectManager = projectHighligthTypeManager;


  }

  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
    }

    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    } else {
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    }
    messages = this.getActionMessages();

    return SUCCESS;
  }


  private String getAnualReportRelativePath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project.getId() + File.separator
      + "hightlihts" + File.separator;
  }


  private Path getAutoSaveFilePath() {
    String composedClassName = caseStudy.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = caseStudy.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public CaseStudy getCaseStudy() {
    return caseStudy;
  }

  public long getCaseStudyID() {
    return caseStudyID;
  }


  private String getCaseStudyPath() {
    return config.getUploadsBaseFolder() + File.separator + this.getCaseStudyUrlPath() + File.separator;
  }


  public String getCaseStudyUrl() {
    return config.getDownloadURL() + "/" + this.getCaseStudyUrlPath().replace('\\', '/');
  }

  public String getCaseStudyUrlPath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project.getId() + File.separator
      + "caseStudy" + File.separator;
  }


  public String getContentType() {
    return contentType;
  }


  public int getEndYear() {
    DateFormat dateFormat = new SimpleDateFormat("yyyy");
    return Integer.parseInt(dateFormat.format(project.getEndDate()));
  }


  public File getFile() {
    return file;
  }


  public String getFileFileName() {
    return fileFileName;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public List<Project> getMyProjects() {
    return myProjects;
  }


  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }


  public String getTransaction() {
    return transaction;
  }


  @Override
  public String next() {
    return SUCCESS;
  }


  @Override
  public void prepare() throws Exception {

    super.prepare();

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    caseStudyID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.CASE_STUDY_REQUEST_ID)));
    projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    project = projectManager.getProjectById(projectID);
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      CaseStudy history = (CaseStudy) auditLogManager.getHistory(transaction);

      if (history != null) {
        caseStudy = history;
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {
      this.caseStudy = caseStudyManager.getCaseStudyById(caseStudyID);

    }


    if (caseStudy != null) {


      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        caseStudy = (CaseStudy) autoSaveReader.readFromJson(jReader);
        reader.close();
        if (caseStudy.getProjects() != null) {
          for (CaseStudyProject caseStudyProject : caseStudy.getProjects()) {
            caseStudyProject.setProject(projectManager.getProjectById(caseStudyProject.getProject().getId()));

          }

        }

        this.setDraft(true);
      } else {

        if (caseStudy.getFile() != null) {
          caseStudy.setFile(fileDBManager.getFileDBById(caseStudy.getFile().getId()));
        }
        caseStudy.setProjects(caseStudy.getCaseStudyProjects().stream().collect(Collectors.toList()));

        this.setDraft(false);
      }

    }


    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_CASE_STUDY_BASE_PERMISSION, params));

    if (projectManager.findAll() != null) {

      if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin()) {
        myProjects =
          loggedCrp.getProjects().stream()
            .filter(p -> p.isActive()
              && p.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()))
          .collect(Collectors.toList());
      } else {
        myProjects =
          projectManager.getUserProjects(this.getCurrentUser().getId(), loggedCrp.getAcronym()).stream()
            .filter(p -> p.isActive()
              && p.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()))
          .collect(Collectors.toList());
      }
      Collections.sort(myProjects, (p1, p2) -> p1.getId().compareTo(p2.getId()));
    }

    if (this.isHttpPost()) {
      if (caseStudy.getProjects() != null) {
        caseStudy.getProjects().clear();
      }


    }

  }


  @Override
  public String save() {

    if (this.hasPermission("canEdit")) {

      DateFormat dateformatter = new SimpleDateFormat(APConstants.DATE_FORMAT);

      Path path = this.getAutoSaveFilePath();


      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_PROJECT_HIGHLIGTH_TYPE_RELATION);
      relationsName.add(APConstants.PROJECT_CASE_STUDIES_PROJECTS_RELATION);
      CaseStudy caseStudyDB = caseStudyManager.getCaseStudyById(caseStudyID);
      caseStudy.setActiveSince(new Date());
      caseStudy.setModifiedBy(this.getCurrentUser());
      caseStudy.setModificationJustification(this.getJustification());
      caseStudy.setCreatedBy(caseStudyDB.getCreatedBy());
      if (file != null) {
        caseStudy.setFile(this.getFileDB(caseStudyDB.getFile(), file, fileFileName, this.getCaseStudyPath()));

        FileManager.copyFile(file, this.getCaseStudyPath() + fileFileName);

      }
      if (caseStudy.getFile() != null) {
        if (caseStudy.getFile().getId() == null) {
          caseStudy.setFile(null);
        }
      }

      for (CaseStudyProject caseStudyProject : caseStudyDB.getCaseStudyProjects()) {
        if (!caseStudy.getProjects().contains(caseStudyProject)) {
          caseStudyProjectManager.deleteCaseStudyProject(caseStudyProject.getId());
        }
      }
      for (CaseStudyProject caseStudyProject : caseStudy.getProjects()) {
        if (caseStudyProject.getId() == null) {
          caseStudyProject.setCreated(false);
          caseStudyProject.setCaseStudy(caseStudy);
          caseStudyProjectManager.saveCaseStudyProject(caseStudyProject);
        }
      }

      caseStudy.setActive(true);


      if (path.toFile().exists()) {
        path.toFile().delete();
      }


      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }

        } else {
          this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        return SUCCESS;
      } else {
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }
    }
    return NOT_AUTHORIZED;
  }

  public void setCaseStudy(CaseStudy caseStudy) {
    this.caseStudy = caseStudy;
  }

  public void setCaseStudyID(long highlightID) {
    this.caseStudyID = highlightID;
  }


  public void setContentType(String contentType) {
    this.contentType = contentType;
  }


  public void setFile(File file) {
    this.file = file;
  }


  public void setFileFileName(String fileFileName) {
    this.fileFileName = fileFileName;
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setMyProjects(List<Project> myProjects) {
    this.myProjects = myProjects;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }


  @Override
  public void validate() {

    if (save) {
      // highLightValidator.validate(this, project, caseStudy, true);
    }
  }
}
