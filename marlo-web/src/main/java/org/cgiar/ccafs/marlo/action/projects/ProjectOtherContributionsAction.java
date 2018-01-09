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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPandrManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.OtherContributionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectCrpContributionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOtherContributionManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpPandr;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.IpIndicator;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.OtherContribution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectCrpContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectOtherContribution;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.utils.HistoryDifference;
import org.cgiar.ccafs.marlo.validation.projects.ProjectOtherContributionsValidator;

import java.io.BufferedReader;
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

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Christian David Garcia Oviedo- CIAT/CCAFS
 */
public class ProjectOtherContributionsAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = -5094474965088380590L;
  /**
   * 
   */

  // Manager
  private final ProjectManager projectManager;
  private final ProjectCrpContributionManager projectCrpContributionManager;
  private final ProjectOtherContributionManager projectOtherContributionManager;
  private final OtherContributionManager otherContributionManager;
  private final CrpPandrManager crpPandrManager;
  private final IpProgramManager ipProgramManager;
  private final IpIndicatorManager ipIndicatorManager;
  private final ProjectOtherContributionsValidator projectOtherContributionsValidator;
  private List<CrpPandr> crps;
  private List<IpProgram> regions;
  private List<IpIndicator> otherIndicators;
  private final HistoryComparator historyComparator;


  private long projectID;


  private Project project;

  private CrpManager crpManager;


  private Crp loggedCrp;

  private String transaction;


  private AuditLogManager auditLogManager;

  @Inject
  public ProjectOtherContributionsAction(APConfig config, ProjectManager projectManager,
    InstitutionManager institutionManager, CrpProgramManager crpProgrammManager, AuditLogManager auditLogManager,
    CrpManager crpManager, ProjectCrpContributionManager projectCrpContributionManager, CrpPandrManager crpPandrManager,
    ProjectOtherContributionsValidator projectOtherContributionsValidator, IpIndicatorManager ipIndicatorManager,
    OtherContributionManager otherContributionManager, ProjectOtherContributionManager projectOtherContributionManager,
    IpProgramManager ipProgramManager, HistoryComparator historyComparator) {
    super(config);
    this.projectManager = projectManager;
    this.projectCrpContributionManager = projectCrpContributionManager;
    this.crpPandrManager = crpPandrManager;
    this.ipIndicatorManager = ipIndicatorManager;
    this.projectOtherContributionsValidator = projectOtherContributionsValidator;
    this.crpManager = crpManager;
    this.ipProgramManager = ipProgramManager;
    this.projectOtherContributionManager = projectOtherContributionManager;
    this.otherContributionManager = otherContributionManager;
    this.auditLogManager = auditLogManager;
    this.historyComparator = historyComparator;

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

  public void crpContributionsNewData(List<ProjectCrpContribution> crpContributions) {
    if (crpContributions != null) {
      for (ProjectCrpContribution crpContribution : crpContributions) {
        if (crpContribution != null) {
          if (crpContribution.getId() == null || crpContribution.getId() == -1) {
            crpContribution.setActive(true);
            crpContribution.setCreatedBy(this.getCurrentUser());
            crpContribution.setModifiedBy(this.getCurrentUser());
            crpContribution.setModificationJustification(this.getJustification());
            crpContribution.setActiveSince(new Date());

            crpContribution.setId(null);
            crpContribution.setProject(project);

          } else {
            ProjectCrpContribution crpContributionDB =
              projectCrpContributionManager.getProjectCrpContributionById(crpContribution.getId());
            crpContribution.setActive(true);
            crpContribution.setCreatedBy(crpContributionDB.getCreatedBy());
            crpContribution.setModifiedBy(this.getCurrentUser());
            crpContribution.setModificationJustification(this.getJustification());

            crpContribution.setProject(project);
            crpContribution.setActiveSince(crpContributionDB.getActiveSince());

          }
          projectCrpContributionManager.saveProjectCrpContribution(crpContribution);
        }


      }
    }


  }


  public void crpContributionsPreviousData(List<ProjectCrpContribution> crpContributions) {
    if (crpContributions != null) {
      crpContributions = new ArrayList<>();
    }
    List<ProjectCrpContribution> projectCrpPrew;
    Project projectBD = projectManager.getProjectById(projectID);


    projectCrpPrew =
      projectBD.getProjectCrpContributions().stream().filter(a -> a.isActive()).collect(Collectors.toList());


    for (ProjectCrpContribution projectCrpContribution : projectCrpPrew) {
      if (!crpContributions.contains(projectCrpContribution)) {
        projectCrpContributionManager.deleteProjectCrpContribution(projectCrpContribution.getId());
      }
    }

  }


  private Path getAutoSaveFilePath() {
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<CrpPandr> getCrps() {
    return crps;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public List<IpIndicator> getOtherIndicators() {
    return otherIndicators;
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

  public String getProjectRequest() {
    return APConstants.PROJECT_REQUEST_ID;
  }


  public List<IpProgram> getRegions() {
    return regions;
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

  public void OtherContributionsNewData(List<OtherContribution> otherContributions) {
    if (otherContributions != null) {
      for (OtherContribution otherContribution : otherContributions) {
        if (otherContribution != null) {
          if (otherContribution.getId() == null || otherContribution.getId() == -1) {
            otherContribution.setActive(true);
            otherContribution.setCreatedBy(this.getCurrentUser());
            otherContribution.setModifiedBy(this.getCurrentUser());
            otherContribution.setModificationJustification(this.getJustification());
            otherContribution.setActiveSince(new Date());
            otherContribution.setId(null);
            otherContribution.setProject(project);

          } else {
            OtherContribution otherContributionDB =
              otherContributionManager.getOtherContributionById(otherContribution.getId());
            otherContribution.setActive(true);
            otherContribution.setCreatedBy(otherContributionDB.getCreatedBy());
            otherContribution.setModifiedBy(this.getCurrentUser());
            otherContribution.setModificationJustification(this.getJustification());

            otherContribution.setProject(project);
            otherContribution.setActiveSince(otherContributionDB.getActiveSince());

          }
          otherContributionManager.saveOtherContribution(otherContribution);
        }


      }
    }


  }

  public void OtherContributionsPreviousData(List<OtherContribution> otherContributions) {
    if (otherContributions != null) {
      otherContributions = new ArrayList<>();
    }
    List<OtherContribution> projectCrpPrew;
    Project projectBD = projectManager.getProjectById(projectID);


    projectCrpPrew = projectBD.getOtherContributions().stream().filter(a -> a.isActive()).collect(Collectors.toList());


    for (OtherContribution projectCrpContribution : projectCrpPrew) {
      if (!otherContributions.contains(projectCrpContribution)) {
        otherContributionManager.deleteOtherContribution(projectCrpContribution.getId());
      }
    }

  }


  @Override
  public void prepare() throws Exception {
    super.prepare();

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());


    projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Project history = (Project) auditLogManager.getHistory(transaction);

      if (history != null) {
        project = history;
        List<HistoryDifference> differences = new ArrayList<>();
        Map<String, String> specialList = new HashMap<>();
        int i = 0;
        project.setCrpContributions(
          project.getProjectCrpContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

        project.setProjectOtherContributionsList(
          project.getProjectOtherContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

        project.setOtherContributionsList(
          project.getOtherContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        for (ProjectOtherContribution projectOtherContribution : project.getProjectOtherContributionsList()) {
          differences.addAll(historyComparator.getDifferencesList(projectOtherContribution, transaction, specialList,
            "project.projectOtherContributionsList[" + i + "]", "project", 1));
          i++;
        }
        i = 0;
        for (OtherContribution projectOtherContribution : project.getOtherContributionsList()) {
          differences.addAll(historyComparator.getDifferencesList(projectOtherContribution, transaction, specialList,
            "project.otherContributionsList[" + i + "]", "project", 1));
          i++;
        }

        i = 0;
        for (ProjectCrpContribution projectOtherContribution : project.getCrpContributions()) {
          differences.addAll(historyComparator.getDifferencesList(projectOtherContribution, transaction, specialList,
            "project.crpContributions[" + i + "]", "project", 1));
          i++;
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


        if (project.getCrpContributions() == null) {

          project.setCrpContributions(new ArrayList<ProjectCrpContribution>());
        } else {
          for (ProjectCrpContribution projectCrpContribution : project.getCrpContributions()) {

            if (projectCrpContribution != null) {
              if (projectCrpContribution.getCrp() != null) {
                projectCrpContribution.setCrp(crpPandrManager.getCrpPandrById(projectCrpContribution.getCrp().getId()));
              }

            }


          }
        }
        this.setDraft(true);
      } else {
        project.setCrpContributions(
          project.getProjectCrpContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

        project.setProjectOtherContributionsList(
          project.getProjectOtherContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

        project.setOtherContributionsList(
          project.getOtherContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        this.setDraft(false);
      }
    }


    Project projectDB = projectManager.getProjectById(projectID);
    project.setProjectEditLeader(projectDB.isProjectEditLeader());
    project.setAdministrative(projectDB.getAdministrative());
    crps = crpPandrManager.findAll();
    regions = ipProgramManager.findAll().stream().filter(c -> c.getIpProgramType().getId().intValue() == 5)
      .collect(Collectors.toList());

    List<IpProgram> tempRegions = new ArrayList<>();
    tempRegions.addAll(regions);
    List<CrpProgram> crpPrograms = new ArrayList<>();

    for (ProjectFocus projectFocus : projectDB.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList())) {
      crpPrograms.add(projectFocus.getCrpProgram());
    }

    for (CrpProgram crpProgram : crpPrograms) {
      for (IpProgram ipProgram : tempRegions) {
        if (ipProgram.getAcronym().replace("RP ", "").equals(crpProgram.getAcronym())) {
          regions.remove(ipProgram);
        }
        if (ipProgram.getAcronym().equals("Global")) {
          regions.remove(ipProgram);
        }
      }
    }

    otherIndicators = ipIndicatorManager.findOtherContributions(projectID);


    if (this.isHttpPost()) {

      if (project.getCrpContributions() != null) {
        project.getCrpContributions().clear();
      }

      if (project.getOtherContributionsList() != null) {
        project.getOtherContributionsList().clear();
      }
    }

    if (project.getProjectOtherContributionsList().isEmpty()) {
      project.getProjectOtherContributionsList().add(new ProjectOtherContribution());
    }
    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_OTHER_CONTRIBRUTIONS_BASE_PERMISSION, params));


  }

  public void projectOtherContributionsNewData(List<ProjectOtherContribution> projectOtherContributions) {
    if (projectOtherContributions != null) {
      for (ProjectOtherContribution projectOtherContribution : projectOtherContributions) {
        if (projectOtherContribution != null) {
          if (projectOtherContribution.getId() == null || projectOtherContribution.getId() == -1) {
            projectOtherContribution.setActive(true);
            projectOtherContribution.setCreatedBy(this.getCurrentUser());
            projectOtherContribution.setModifiedBy(this.getCurrentUser());
            projectOtherContribution.setModificationJustification(this.getJustification());
            projectOtherContribution.setActiveSince(new Date());
            projectOtherContribution.setAdditionalContribution("");
            projectOtherContribution.setId(null);
            projectOtherContribution.setProject(project);

          } else {
            ProjectOtherContribution otherContributionDB =
              projectOtherContributionManager.getProjectOtherContributionById(projectOtherContribution.getId());
            projectOtherContribution.setActive(true);
            projectOtherContribution.setCreatedBy(otherContributionDB.getCreatedBy());
            projectOtherContribution.setModifiedBy(this.getCurrentUser());
            projectOtherContribution.setModificationJustification(this.getJustification());
            projectOtherContribution.setAdditionalContribution(otherContributionDB.getAdditionalContribution());
            projectOtherContribution.setProject(project);
            projectOtherContribution.setActiveSince(otherContributionDB.getActiveSince());

          }
          projectOtherContributionManager.saveProjectOtherContribution(projectOtherContribution);
        }


      }
    }


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
      this.projectOtherContributionsNewData(project.getProjectOtherContributionsList());
      this.OtherContributionsPreviousData(project.getOtherContributionsList());
      this.OtherContributionsNewData(project.getOtherContributionsList());
      this.crpContributionsPreviousData(project.getCrpContributions());
      this.crpContributionsNewData(project.getCrpContributions());


      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_CRP_CONTRIBUTIONS_RELATION);
      relationsName.add(APConstants.PROJECT_OTHER_CONTRIBUTIONS_RELATION);
      relationsName.add(APConstants.OTHER_CONTRIBUTIONS_RELATION);
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


  public void setCrps(List<CrpPandr> crps) {
    this.crps = crps;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setOtherIndicators(List<IpIndicator> otherIndicators) {
    this.otherIndicators = otherIndicators;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setRegions(List<IpProgram> regions) {
    this.regions = regions;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {

      projectOtherContributionsValidator.validate(this, project, true);
    }
  }
}