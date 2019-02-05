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

package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6ContributionDeliverable;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.projects.ProjectLP6Validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProjectContributionToLP6Action extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;
  private static final Logger LOG = LoggerFactory.getLogger(ProjectContributionToLP6Action.class);

  // Managers
  private ProjectManager projectManager;
  private ProjectLp6ContributionManager projectLp6ContributionManager;
  private ProjectLp6ContributionDeliverableManager projectLp6ContributionDeliverableManager;
  private GlobalUnitManager crpManager;
  private AuditLogManager auditLogManager;
  private DeliverableManager deliverableManager;
  private DeliverableInfoManager deliverableInfoManager;

  // Front-end
  private long projectID;
  private GlobalUnit loggedCrp;
  private Project project;
  private List<CrpProgram> programFlagships;
  private List<CrpProgram> regionFlagships;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<Deliverable> deliverables;
  private ProjectLP6Validator validator;
  private String transaction;
  private ProjectLp6Contribution projectLp6ContributionDB;

  @Inject
  public ProjectContributionToLP6Action(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    AuditLogManager auditLogManager, ProjectLP6Validator validator,
    ProjectLp6ContributionDeliverableManager projectLp6ContributionDeliverableManager,
    ProjectLp6ContributionManager projectLp6ContributionManager, DeliverableManager deliverableManager,
    DeliverableInfoManager deliverableInfoManager) {
    super(config);
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.auditLogManager = auditLogManager;
    this.validator = validator;
    this.projectLp6ContributionDeliverableManager = projectLp6ContributionDeliverableManager;
    this.projectLp6ContributionManager = projectLp6ContributionManager;
    this.deliverableManager = deliverableManager;
    this.deliverableInfoManager = deliverableInfoManager;
  }


  public void cleanNarrativeFields() {
    if (project.getProjectLp6Contribution().getInitiativeRelated() != null
      && project.getProjectLp6Contribution().isInitiativeRelated() == false) {
      project.getProjectLp6Contribution().setInitiativeRelatedNarrative("");
    }

    if (project.getProjectLp6Contribution().isProvidingPathways() != null
      && project.getProjectLp6Contribution().isProvidingPathways() == false) {
      project.getProjectLp6Contribution().setProvidingPathwaysNarrative("");
    }

    if (project.getProjectLp6Contribution().isUndertakingEffortsCsa() != null
      && project.getProjectLp6Contribution().isUndertakingEffortsCsa() == false) {
      project.getProjectLp6Contribution().setUndertakingEffortsCsaNarrative("");
    }

    if (project.getProjectLp6Contribution().isUndertakingEffortsLeading() != null
      && project.getProjectLp6Contribution().isUndertakingEffortsLeading() == false) {
      project.getProjectLp6Contribution().setUndertakingEffortsLeadingNarrative("");
    }

    if (project.getProjectLp6Contribution().isWorkingAcrossFlagships() != null
      && project.getProjectLp6Contribution().isWorkingAcrossFlagships() == false) {
      project.getProjectLp6Contribution().setWorkingAcrossFlagshipsNarrative("");
    }

  }

  private String getAnualReportRelativePath() {
    return config.getProjectsBaseFolder(loggedCrp.getAcronym()) + File.separator + project.getId() + File.separator
      + config.getAnualReportFolder();
  }


  public String getAnualReportURL() {
    return config.getDownloadURL() + "/" + this.getAnualReportRelativePath().replace('\\', '/');
  }

  private Path getAutoSaveFilePath() {
    // get the class simple name
    String composedClassName = project.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatenate name and add the .json extension
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public List<Deliverable> getDeliverables() {
    return deliverables;
  }


  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }


  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }


  public List<CrpProgram> getProgramFlagships() {
    return programFlagships;
  }


  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }

  public List<CrpProgram> getRegionFlagships() {
    return regionFlagships;
  }


  public String getTransaction() {
    return transaction;
  }

  private String getWorkplanRelativePath() {
    return config.getProjectsBaseFolder(loggedCrp.getAcronym()) + File.separator + project.getId() + File.separator
      + config.getProjectWorkplanFolder() + File.separator;
  }


  public String getWorkplanURL() {
    return config.getDownloadURL() + "/" + this.getWorkplanRelativePath().replace('\\', '/');
  }

  @Override
  public void prepare() throws Exception {

    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    } catch (Exception e) {
      LOG.error("unable to parse projectID", e);
    }
    project = projectManager.getProjectById(projectID);

    // We check that you have a TRANSACTION_ID to know if it is history version
    Boolean hasTransaction = false;
    String transactionID = this.getRequest().getParameter(APConstants.TRANSACTION_ID);

    if (transactionID != null) {
      transaction = StringUtils.trim(transactionID);
      Project history = (Project) auditLogManager.getHistory(transaction);

      if (history != null) {
        project = history;
        hasTransaction = true;
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }
    } else {
      // get project info for DB
      project = projectManager.getProjectById(projectID);
    }

    if (project != null) {
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave() && !hasTransaction) {

        // Autosave File
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();

        AutoSaveReader autoSaveReader = new AutoSaveReader();
        project = (Project) autoSaveReader.readFromJson(jReader);
        Project projectDb = projectManager.getProjectById(project.getId());
        project.getProjectInfo().setPhase(projectDb.getProjecInfoPhase(this.getActualPhase()).getPhase());
        project.getProjectInfo()
          .setProjectEditLeader(projectDb.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader());
        project.getProjectInfo()
          .setAdministrative(projectDb.getProjecInfoPhase(this.getActualPhase()).getAdministrative());

        if (project.getProjectLp6Contribution() != null) {

          if (project.getProjectLp6Contribution().getDeliverables() == null) {
            project.getProjectLp6Contribution().setDeliverables(new ArrayList<>());
          }
          // Load Deliverable list from Autosave to LP6Deliverables
          if (project.getProjectLp6ContributionDeliverables() != null
            && !project.getProjectLp6ContributionDeliverables().isEmpty()) {
            for (ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable : project
              .getProjectLp6ContributionDeliverables()) {
              if (projectLp6ContributionDeliverable.getDeliverable() != null
                && projectLp6ContributionDeliverable.getDeliverable().getId() != null) {
                Deliverable deliverable =
                  deliverableManager.getDeliverableById(projectLp6ContributionDeliverable.getDeliverable().getId());
                ProjectLp6ContributionDeliverable autoSaveProjectLp6ContributionDeliverable =
                  new ProjectLp6ContributionDeliverable();
                autoSaveProjectLp6ContributionDeliverable.setDeliverable(deliverable);
                autoSaveProjectLp6ContributionDeliverable.setModifiedBy(this.getCurrentUser());
                autoSaveProjectLp6ContributionDeliverable.setPhase(this.getActualPhase());
                autoSaveProjectLp6ContributionDeliverable
                  .setProjectLp6Contribution(project.getProjectLp6Contribution());
                project.getProjectLp6Contribution().getDeliverables().add(autoSaveProjectLp6ContributionDeliverable);
              }

            }
          }
        }
        this.setDraft(true);
      } else {
        this.setDraft(false);

        // Load the DB information and adjust it to the structures with which the front end
        Project projectDb = projectManager.getProjectById(project.getId());
        if (project.getProjectInfo() == null) {
          project.setProjectInfo(new ProjectInfo());
        }
        project.getProjectInfo().setPhase(projectDb.getProjecInfoPhase(this.getActualPhase()).getPhase());
        project.getProjectInfo()
          .setProjectEditLeader(projectDb.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader());
        project.getProjectInfo()
          .setAdministrative(projectDb.getProjecInfoPhase(this.getActualPhase()).getAdministrative());
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));

        /*
         * Get the actual projectLp6Contribution
         */
        project.setProjectLp6Contribution(project.getProjectLp6Contributions().stream()
          .filter(c -> c.isActive() && c.getPhase().getId() == this.getActualPhase().getId())
          .collect(Collectors.toList()).get(0));

        if (project.getProjectLp6Contribution() != null) {
          // Get selected deliverables
          if (project.getProjectLp6Contribution().getDeliverables() == null) {
            project.getProjectLp6Contribution().setDeliverables(new ArrayList<>());
          }
          List<ProjectLp6ContributionDeliverable> deliverables =
            project.getProjectLp6Contribution().getProjectLp6ContributionDeliverable().stream()
              .filter(ld -> ld.isActive() && ld.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());
          for (ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable : deliverables) {
            if (projectLp6ContributionDeliverable.getDeliverable() != null
              && projectLp6ContributionDeliverable.getDeliverable().getId() != null) {
              projectLp6ContributionDeliverable.setDeliverable(
                deliverableManager.getDeliverableById(projectLp6ContributionDeliverable.getDeliverable().getId()));
            }
          }

          project.getProjectLp6Contribution().getDeliverables().addAll(deliverables);

        }
      }
    }

    // Getting The lists

    /*
     * List of deliverables for the actual project and phase
     */
    deliverables = new ArrayList<>();
    if (project.getDeliverables() != null) {
      List<DeliverableInfo> infos =
        deliverableInfoManager.getDeliverablesInfoByProjectAndPhase(this.getActualPhase(), project);
      deliverables = new ArrayList<>();
      if (infos != null && !infos.isEmpty()) {
        for (DeliverableInfo deliverableInfo : infos) {
          Deliverable deliverable = deliverableInfo.getDeliverable();
          deliverable.setDeliverableInfo(deliverableInfo);
          deliverables.add(deliverable);
        }
      }
    }

    projectLp6ContributionDB = projectLp6ContributionManager.findAll().stream().filter(
      c -> c.isActive() && c.getProject().getId() == projectID && c.getPhase().getId() == this.getActualPhase().getId())
      .collect(Collectors.toList()).get(0);

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_LP6_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (project.getProjectLp6Contribution().getDeliverables() != null) {
        project.getProjectLp6Contribution().getDeliverables().clear();
      }
    }

  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {
      project.getProjectLp6Contribution().setProject(project);
      project.getProjectLp6Contribution().setPhase(this.getActualPhase());
      // Clean the actual project Lp6 contribution
      this.cleanNarrativeFields();

      // Save project Lp6 relations
      this.saveProjectDeliverables();

      projectLp6ContributionManager.saveProjectLp6Contribution(project.getProjectLp6Contribution());

      project.getProjectLp6Contributions().add(project.getProjectLp6Contribution());
      Path path = this.getAutoSaveFilePath();

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_CONTRIBUTION_LP6_RELATION);
      /**
       * The following is required because we need to update something on the @Project if we want a row
       * created in the auditlog table.
       */

      this.setModificationJustification(project);
      projectManager.saveProject(project, this.getActionName(), relationsName, this.getActualPhase());

      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
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
    } else {


      return NOT_AUTHORIZED;
    }

  }


  public void saveProjectDeliverables() {
    List<ProjectLp6ContributionDeliverable> projectLp6ContributionDeliverables =
      projectLp6ContributionDB.getProjectLp6ContributionDeliverable().stream()
        .filter(pd -> pd.isActive() && pd.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());

    if (project.getProjectLp6Contribution().getDeliverables() != null
      && !project.getProjectLp6Contribution().getDeliverables().isEmpty()) {

      if (projectLp6ContributionDeliverables != null && !projectLp6ContributionDeliverables.isEmpty()) {

        for (ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable : projectLp6ContributionDeliverables) {

          if (!project.getProjectLp6Contribution().getDeliverables().contains(projectLp6ContributionDeliverable)) {
            // If the deliverable in bd is deleted in front end, it will be deleted from database
            projectLp6ContributionDeliverableManager
              .deleteProjectLp6ContributionDeliverable(projectLp6ContributionDeliverable.getId());
          }
        }
      }
      project.getProjectLp6Contribution().setProjectLp6ContributionDeliverable(new HashSet<>());
      for (ProjectLp6ContributionDeliverable contributionDeliverables : project.getProjectLp6Contribution()
        .getDeliverables()) {
        if (contributionDeliverables.getId() == null) {
          contributionDeliverables.setPhase(this.getActualPhase());
          contributionDeliverables.setProjectLp6Contribution(project.getProjectLp6Contribution());
          contributionDeliverables =
            projectLp6ContributionDeliverableManager.saveProjectLp6ContributionDeliverable(contributionDeliverables);
          project.getProjectLp6Contribution().getProjectLp6ContributionDeliverable().add(contributionDeliverables);
        }
      }
    } else {
      // If the list of selected deliverables is empty
      if (projectLp6ContributionDeliverables != null && !projectLp6ContributionDeliverables.isEmpty()) {
        for (ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable : projectLp6ContributionDeliverables) {
          projectLp6ContributionDeliverableManager
            .deleteProjectLp6ContributionDeliverable(projectLp6ContributionDeliverable.getId());
        }
      }
    }
  }


  public void setDeliverables(List<Deliverable> deliverables) {
    this.deliverables = deliverables;
  }

  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setProgramFlagships(List<CrpProgram> programFlagships) {
    this.programFlagships = programFlagships;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setRegionFlagships(List<CrpProgram> regionFlagships) {
    this.regionFlagships = regionFlagships;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }


  @Override
  public void validate() {
    // if is saving call the validator to check for the missing fields
    if (save) {
      validator.validate(this, project, project.getProjectLp6Contribution(), true);
    }
  }

}