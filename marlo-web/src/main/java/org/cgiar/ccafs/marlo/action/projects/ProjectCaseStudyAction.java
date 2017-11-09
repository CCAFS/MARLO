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
import org.cgiar.ccafs.marlo.data.manager.CaseStudyIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.CaseStudyManager;
import org.cgiar.ccafs.marlo.data.manager.CaseStudyProjectManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.IpIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.CaseStudy;
import org.cgiar.ccafs.marlo.data.model.CaseStudyIndicator;
import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.IpIndicator;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.FileManager;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectCaseStudyValidation;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


  // LOG
  private static Logger LOG = LoggerFactory.getLogger(ProjectCaseStudyAction.class);
  /**
   * 
   */
  private static final long serialVersionUID = -5209003027874233584L;
  private AuditLogManager auditLogManager;
  private HistoryComparator historyComparator;


  // Model for the back-end
  private CaseStudy caseStudy;

  // Model for the front-end
  private long caseStudyID;
  private CaseStudyIndicatorManager caseStudyIndicatorManager;


  private Map<String, String> caseStudyIndicators;


  private CaseStudyManager caseStudyManager;

  private CaseStudyProjectManager caseStudyProjectManager;
  private ProjectCaseStudyValidation caseStudyValidation;
  private String contentType;
  private CrpManager crpManager;

  private File file;

  private FileDBManager fileDBManager;
  // private ProjectHighLightValidator validator;
  private String fileFileName;
  private IpIndicatorManager ipIndicatorManager;
  private Crp loggedCrp;
  private List<Project> myProjects;
  private Project project;
  private long projectID;
  // Manager
  private ProjectManager projectManager;
  private PhaseManager phaseManager;

  private String transaction;


  @Inject
  public ProjectCaseStudyAction(APConfig config, ProjectManager projectManager, CaseStudyManager highLightManager,
    CrpManager crpManager, AuditLogManager auditLogManager, FileDBManager fileDBManager,
    CaseStudyProjectManager projectHighligthTypeManager, IpIndicatorManager ipIndicatorManager,
    HistoryComparator historyComparator, ProjectCaseStudyValidation caseStudyValidation,
    CaseStudyIndicatorManager caseStudyIndicatorManager, PhaseManager phaseManager) {
    super(config);
    this.projectManager = projectManager;
    this.caseStudyManager = highLightManager;
    this.phaseManager = phaseManager;
    this.auditLogManager = auditLogManager;
    this.crpManager = crpManager;
    this.fileDBManager = fileDBManager;
    this.ipIndicatorManager = ipIndicatorManager;
    this.caseStudyIndicatorManager = caseStudyIndicatorManager;
    this.caseStudyValidation = caseStudyValidation;
    this.historyComparator = historyComparator;
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


  public Map<String, String> getCaseStudyIndicators() {
    return caseStudyIndicators;
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
    return Integer.parseInt(dateFormat.format(project.getProjecInfoPhase(this.getActualPhase()).getEndDate()));
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


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      CaseStudy history = (CaseStudy) auditLogManager.getHistory(transaction);

      if (history != null) {
        caseStudy = history;
        Map<String, String> specialList = new HashMap<>();
        specialList.put(APConstants.PROJECT_CASE_STUDIES_INDICATORS_RELATION, "caseStudyIndicatorsIds");

        this.setDifferences(historyComparator.getDifferences(transaction, specialList, "caseStudy"));

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
 	      reader.close();
 	

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        caseStudy = (CaseStudy) autoSaveReader.readFromJson(jReader);
      
        if (caseStudy.getProjects() != null) {
          for (CaseStudyProject caseStudyProject : caseStudy.getProjects()) {
            caseStudyProject.setProject(projectManager.getProjectById(caseStudyProject.getProject().getId()));

          }

        }
        if (caseStudy.getIndicators() != null) {
          for (CaseStudyIndicator caseStudyIndicator : caseStudy.getIndicators()) {
            caseStudyIndicator
              .setIpIndicator(ipIndicatorManager.getIpIndicatorById(caseStudyIndicator.getIpIndicator().getId()));

          }

        }
        this.setDraft(true);
      } else {

        if (caseStudy.getFile() != null) {
          caseStudy.setFile(fileDBManager.getFileDBById(caseStudy.getFile().getId()));
        }
        caseStudy.setProjects(caseStudy.getCaseStudyProjects().stream().collect(Collectors.toList()));
        caseStudy.setIndicators(caseStudy.getCaseStudyIndicators().stream().collect(Collectors.toList()));
        List<String> idsIndicators = new ArrayList<>();
        for (CaseStudyIndicator caseStudyIndicator : caseStudy.getIndicators()) {
          idsIndicators.add(caseStudyIndicator.getIpIndicator().getId().toString());
        }
        caseStudy.setCaseStudyIndicatorsIds(idsIndicators);
        this.setDraft(false);
      }

    }


    List<IpIndicator> listIndicators = ipIndicatorManager.getIndicatorsFlagShips();
    caseStudyIndicators = new HashMap<>();
    for (IpIndicator ipIndicator : listIndicators) {
      caseStudyIndicators.put(String.valueOf(ipIndicator.getId()), ipIndicator.getDescription());
    }

    try {
      projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    } catch (Exception e) {
      projectID = caseStudy.getProjects().stream().filter(cs -> cs.isActive() && cs.isCreated())
        .collect(Collectors.toList()).get(0).getProject().getId();
    }

    project = projectManager.getProjectById(projectID);

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_CASE_STUDY_BASE_PERMISSION, params));
    Phase phase = this.getActualPhase();
    phase = phaseManager.getPhaseById(phase.getId());

    myProjects = new ArrayList<>();
    for (ProjectPhase projectPhase : phase.getProjectPhases()) {
      myProjects.add(projectPhase.getProject());
    }

    Collections.sort(myProjects, (p1, p2) -> p1.getId().compareTo(p2.getId()));


    if (this.isHttpPost()) {
      if (caseStudy.getProjects() != null) {
        caseStudy.getProjects().clear();
        caseStudy.setFile(null);
      }


    }

  }


  @Override
  public String save() {

    if (this.hasPermission("canEdit")) {

      DateFormat dateformatter = new SimpleDateFormat(APConstants.DATE_FORMAT);

      Path path = this.getAutoSaveFilePath();

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_CASE_STUDIES_PROJECTS_RELATION);
      relationsName.add(APConstants.PROJECT_CASE_STUDIES_INDICATORS_RELATION);
      CaseStudy caseStudyDB = caseStudyManager.getCaseStudyById(caseStudyID);
      caseStudy.setActiveSince(new Date());
      caseStudy.setModifiedBy(this.getCurrentUser());
      caseStudy.setModificationJustification(this.getJustification());
      caseStudy.setCreatedBy(caseStudyDB.getCreatedBy());
      if (file != null) {
        caseStudy.setFile(this.getFileDB(caseStudyDB.getFile(), file, fileFileName, this.getCaseStudyPath()));

        FileManager.copyFile(file, this.getCaseStudyPath() + fileFileName);
        LOG.info("CASE STUDY" + this.getCaseStudyPath() + "/" + fileFileName);
        System.out.println("CASE STUDY" + this.getCaseStudyPath() + "/" + fileFileName);

      }

      if (caseStudy.getFile() != null) {
        if (caseStudy.getFile().getId() == null || caseStudy.getFile().getId().longValue() == -1) {
          caseStudy.setFile(null);
        }
      } else {
        caseStudy.setFile(null);
      }

      for (CaseStudyProject caseStudyProject : caseStudyDB.getCaseStudyProjects()) {
        if (!caseStudy.getProjects().contains(caseStudyProject)) {
          caseStudyProjectManager.deleteCaseStudyProject(caseStudyProject.getId());
        }
      }
      for (CaseStudyProject caseStudyProject : caseStudy.getProjects()) {
        if (caseStudyProject.getId() == null || caseStudyProject.getId().longValue() == -1) {
          caseStudyProject.setCreated(false);
          caseStudyProject.setCaseStudy(caseStudy);
          caseStudyProject.setId(null);
          caseStudyProjectManager.saveCaseStudyProject(caseStudyProject);
        }
      }

      if (caseStudy.getCaseStudyIndicatorsIds() == null) {
        caseStudy.setCaseStudyIndicatorsIds(new ArrayList<>());
      }
      for (CaseStudyIndicator caseStudyIndicator : caseStudyDB.getCaseStudyIndicators()) {
        if (!caseStudy.getCaseStudyIndicatorsIds().contains(caseStudyIndicator.getIpIndicator().getId().toString())) {
          caseStudyIndicatorManager.deleteCaseStudyIndicator(caseStudyIndicator.getId());
        }
      }

      if (caseStudy.getCaseStudyIndicatorsIds() != null) {
        for (String indicator : caseStudy.getCaseStudyIndicatorsIds()) {
          CaseStudyIndicator caseStudyIndicator = new CaseStudyIndicator();
          caseStudyIndicator.setCaseStudy(caseStudy);
          caseStudyIndicator.setIpIndicator(ipIndicatorManager.getIpIndicatorById(Long.parseLong(indicator)));
          if (!caseStudyDB.getCaseStudyIndicators().contains(caseStudyIndicator)) {
            caseStudyIndicatorManager.saveCaseStudyIndicator(caseStudyIndicator);
          }
        }
      }

      caseStudy.setActive(true);

      caseStudyManager.saveCaseStudy(caseStudy, this.getActionName(), relationsName);

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

  public void setCaseStudyIndicators(Map<String, String> caseStudyIndicators) {
    this.caseStudyIndicators = caseStudyIndicators;
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
      caseStudyValidation.validate(this, project, caseStudy, true);
    }
  }
}
