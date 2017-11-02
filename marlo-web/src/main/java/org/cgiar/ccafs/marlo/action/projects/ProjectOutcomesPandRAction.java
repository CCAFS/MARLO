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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpElementManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomePandrManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcomePandr;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.FileManager;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.utils.HistoryDifference;
import org.cgiar.ccafs.marlo.validation.projects.ProjectOutcomesPandRValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Christian David Garcia Oviedo- CIAT/CCAFS
 */
public class ProjectOutcomesPandRAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 1256371160406383889L;
  /**
   * 
   */

  // Manager
  private ProjectManager projectManager;
  private InstitutionManager institutionManager;
  private CrpProgramManager crpProgrammManager;
  private ProjectOutcomePandrManager projectOutcomePandrManager;
  private IpElementManager ipElementManager;
  private FileDBManager fileDBManager;
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  private ProjectOutcomesPandRValidator projectOutcomesPandRValidator;

  private List<Integer> allYears;


  private long projectID;
  private Project project;
  private GlobalUnit loggedCrp;


  private File file;
  // private ProjectHighLightValidator validator;
  private String fileFileName;
  private String contentType;

  private String transaction;

  private HistoryComparator historyComparator;
  private AuditLogManager auditLogManager;


  @Inject
  public ProjectOutcomesPandRAction(APConfig config, ProjectManager projectManager,
    InstitutionManager institutionManager, CrpProgramManager crpProgrammManager, AuditLogManager auditLogManager,
    GlobalUnitManager crpManager, FileDBManager fileDBManager, ProjectOutcomePandrManager projectOutcomePandrManager,
    HistoryComparator historyComparator, IpElementManager ipElementManager,
    ProjectOutcomesPandRValidator projectOutcomesPandRValidator) {
    super(config);
    this.projectManager = projectManager;
    this.institutionManager = institutionManager;
    this.crpProgrammManager = crpProgrammManager;
    this.projectOutcomePandrManager = projectOutcomePandrManager;
    this.ipElementManager = ipElementManager;
    this.fileDBManager = fileDBManager;
    this.historyComparator = historyComparator;
    this.crpManager = crpManager;
    this.projectOutcomesPandRValidator = projectOutcomesPandRValidator;
    this.auditLogManager = auditLogManager;

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


  @Override
  public List<Integer> getAllYears() {
    return allYears;
  }


  private Path getAutoSaveFilePath() {
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public String getContentType() {
    return contentType;
  }

  public File getFile() {
    return file;
  }


  public String getFileFileName() {
    return fileFileName;
  }

  public int getIndex(int year) {
    if (project.getOutcomesPandr() != null) {
      int i = 0;
      for (ProjectOutcomePandr projectOutcomePandr : project.getOutcomesPandr()) {
        if (projectOutcomePandr.getYear() == year) {
          return i;
        }
        i++;
      }
    }
    return -1;


  }


  public ProjectOutcomePandr getOutcome(int year) {
    int index = this.getIndex(year);
    if (index >= 0) {
      return project.getOutcomesPandr().get(index);
    }
    return null;
  }


  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }

  public ProjectManager getProjectManager() {
    return projectManager;
  }


  private String getProjectOutcomePath() {
    return config.getUploadsBaseFolder() + File.separator + this.getProjectOutcomeUrlPath() + File.separator;
  }

  public String getProjectOutcomeUrl() {
    return config.getDownloadURL() + "/" + this.getProjectOutcomeUrlPath().replace('\\', '/');
  }

  public String getProjectOutcomeUrlPath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project.getId() + File.separator
      + "project_outcome" + File.separator;
  }

  public String getProjectRequest() {
    return APConstants.PROJECT_REQUEST_ID;
  }

  public String getTransaction() {
    return transaction;
  }

  @Override
  public String next() {
    String result = this.save();
    if (result.equals(BaseAction.SUCCESS)) {
      return BaseAction.NEXT;
    } else {
      return result;
    }
  }

  public void ouctomesNewData(List<ProjectOutcomePandr> outcomesPandr) {

    for (ProjectOutcomePandr outcomePandr : outcomesPandr) {
      if (outcomePandr != null) {
        if (outcomePandr.getId() == null || outcomePandr.getId() == -1) {
          outcomePandr.setActive(true);
          outcomePandr.setCreatedBy(this.getCurrentUser());
          outcomePandr.setModifiedBy(this.getCurrentUser());
          outcomePandr.setModificationJustification(this.getJustification());
          outcomePandr.setActiveSince(new Date());

          outcomePandr.setId(null);
          outcomePandr.setProject(project);

        } else {
          ProjectOutcomePandr outcomePandrDB =
            projectOutcomePandrManager.getProjectOutcomePandrById(outcomePandr.getId());
          outcomePandr.setActive(true);
          outcomePandr.setCreatedBy(outcomePandrDB.getCreatedBy());
          outcomePandr.setModifiedBy(this.getCurrentUser());
          outcomePandr.setModificationJustification(this.getJustification());
          outcomePandr.setYear(outcomePandrDB.getYear());
          outcomePandr.setProject(project);
          outcomePandr.setActiveSince(outcomePandrDB.getActiveSince());
          if (outcomePandr.getYear() != this.getCurrentCycleYear()) {
            outcomePandr.setAnualProgress(outcomePandrDB.getAnualProgress());
            outcomePandr.setComunication(outcomePandrDB.getComunication());

          }
        }

        if (outcomePandr.getYear() == this.getCurrentCycleYear()) {
          if (file != null) {
            outcomePandr
              .setFile(this.getFileDB(outcomePandr.getFile(), file, fileFileName, this.getProjectOutcomePath()));

            FileManager.copyFile(file, this.getProjectOutcomePath() + fileFileName);

          }
          if (outcomePandr.getFile() != null) {
            if (outcomePandr.getFile().getId() == null) {
              outcomePandr.setFile(null);
            }
          }
        }


        projectOutcomePandrManager.saveProjectOutcomePandr(outcomePandr);
      }


    }

  }


  @Override
  public void prepare() throws Exception {
    super.prepare();

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());


    projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Project history = (Project) auditLogManager.getHistory(transaction);

      if (history != null) {
        project = history;
        List<HistoryDifference> differences = new ArrayList<>();
        Map<String, String> specialList = new HashMap<>();
        int i = 0;
        project.setOutcomesPandr(
          project.getProjectOutcomesPandr().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        for (ProjectOutcomePandr projectOutcomePandr : project.getOutcomesPandr()) {
          int[] index = new int[1];
          index[0] = i;
          differences.addAll(historyComparator.getDifferencesList(projectOutcomePandr, transaction, specialList,
            "project.outcomesPandr[" + i + "]", "project", 1));
          i++;
        }
        if (this.isLessonsActive()) {
          this.loadLessons(loggedCrp, project);
        }
        if (project.getProjectComponentLesson() != null) {
          differences.addAll(historyComparator.getDifferencesList(project.getProjectComponentLesson(), transaction,
            specialList, "project.projectComponentLesson", "project", 1));
        }
        this.setDifferences(differences);

      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {
      project = projectManager.getProjectById(projectID);
    }

    if (project != null) {


      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();


        AutoSaveReader autoSaveReader = new AutoSaveReader();

        project = (Project) autoSaveReader.readFromJson(jReader);


        if (project.getOutcomesPandr() == null) {

          project.setOutcomesPandr(new ArrayList<ProjectOutcomePandr>());
        } else {
          for (ProjectOutcomePandr outcomePandr : project.getOutcomesPandr()) {

            if (outcomePandr != null) {
              if (outcomePandr.getFile() != null && outcomePandr.getFile().getId() != null) {
                outcomePandr.setFile(fileDBManager.getFileDBById(outcomePandr.getFile().getId()));
              }

            }


          }
        }
        this.setDraft(true);
      } else {
        project.setOutcomesPandr(
          project.getProjectOutcomesPandr().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        if (this.isLessonsActive()) {
          this.loadLessons(loggedCrp, project);
        }
        this.setDraft(false);
      }
    }

    Project projectDB = projectManager.getProjectById(projectID);
    project.setStartDate(projectDB.getStartDate());
    project.setEndDate(projectDB.getEndDate());
    project.setAdministrative(projectDB.getAdministrative());
    project.setProjectEditLeader(projectDB.isProjectEditLeader());
    // Getting the list of all institutions

    if (this.isHttpPost()) {


      if (project.getOutcomesPandr() != null) {
        for (ProjectOutcomePandr projectOutcomePandr : project.getOutcomesPandr()) {
          projectOutcomePandr.setFile(null);
        }
        project.getOutcomesPandr().clear();
      }


    }

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_OUTCOMES_PANDR_BASE_PERMISSION, params));


  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      Project projectDB = projectManager.getProjectById(project.getId());
      project.setActive(true);
      project.setCreatedBy(projectDB.getCreatedBy());
      project.setModifiedBy(this.getCurrentUser());
      project.setModificationJustification(this.getJustification());
      project.setActiveSince(projectDB.getActiveSince());

      this.ouctomesNewData(project.getOutcomesPandr());
      /*
       * this.activitiesPreviousData(project.getClosedProjectActivities(), false);
       * this.activitiesNewData(project.getClosedProjectActivities());
       */
      if (this.isLessonsActive()) {
        this.saveLessons(loggedCrp, project);
      }
      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_OUTCOMES_PANDR_RELATION);
      relationsName.add(APConstants.PROJECT_LESSONS_RELATION);
      project = projectManager.getProjectById(projectID);
      project.setActiveSince(new Date());
      project.setModifiedBy(this.getCurrentUser());
      project.setModificationJustification(this.getJustification());
      projectManager.saveProject(project, this.getActionName(), relationsName);
      Path path = this.getAutoSaveFilePath();

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


  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
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


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setProjectManager(ProjectManager projectManager) {
    this.projectManager = projectManager;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      projectOutcomesPandRValidator.validate(this, project, true);

    }
  }
}