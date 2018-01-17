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
import org.cgiar.ccafs.marlo.data.manager.BudgetTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterOfActivityManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetsCluserActvityManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsCluserActvity;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.projects.ProjectBudgetsCoAValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectBudgetsValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class ProjectBudgetByClusterOfActivitiesAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -7931655721857302103L;


  private CrpClusterOfActivityManager crpClusterOfActivityManager;


  private BudgetTypeManager budgetTypeManager;


  private ProjectManager projectManager;


  private ProjectBudgetsCluserActvityManager projectBudgetsCluserActvityManager;


  private CrpManager crpManager;
  private long projectID;
  private Crp loggedCrp;
  private Project project;
  private String transaction;
  private AuditLogManager auditLogManager;
  private ProjectBudgetsCoAValidator validator;

  private List<BudgetType> budgetTypesList;


  @Inject
  public ProjectBudgetByClusterOfActivitiesAction(APConfig config,
    CrpClusterOfActivityManager crpClusterOfActivityManager, ProjectManager projectManager, CrpManager crpManager,
    ProjectBudgetManager projectBudgetManager, AuditLogManager auditLogManager, BudgetTypeManager budgetTypeManager,

    LiaisonInstitutionManager liaisonInstitutionManager, ProjectBudgetsValidator projectBudgetsValidator,
    ProjectBudgetsCluserActvityManager projectBudgetsCluserActvityManager, ProjectBudgetsCoAValidator validator) {
    super(config);
    this.crpClusterOfActivityManager = crpClusterOfActivityManager;
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.validator = validator;
    this.auditLogManager = auditLogManager;
    this.budgetTypeManager = budgetTypeManager;
    this.projectBudgetsCluserActvityManager = projectBudgetsCluserActvityManager;
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


  /**
   * This method clears the cache and re-load the user permissions in the next iteration.
   */
  @Override
  public void clearPermissionsCache() {
    ((APCustomRealm) securityContext.getRealm())
      .clearCachedAuthorizationInfo(securityContext.getSubject().getPrincipals());
  }


  private Path getAutoSaveFilePath() {
    String composedClassName = project.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public ProjectBudgetsCluserActvity getBudget(String activitiyId, int year, long type) {
    if (project.getBudgetsCluserActvities() == null) {
      project.setBudgetsCluserActvities(new ArrayList<>());
    }
    return project.getBudgetsCluserActvities().get(this.getIndexBudget(activitiyId, year, type));
  }

  public List<BudgetType> getBudgetTypesList() {
    return budgetTypesList;
  }


  public int getIndexBudget(String activitiyId, int year, long type) {

    if (project.getBudgetsCluserActvities() != null) {
      int i = 0;
      for (ProjectBudgetsCluserActvity projectBudget : project.getBudgetsCluserActvities()) {
        if (projectBudget.getCrpClusterOfActivity() != null) {
          if (projectBudget.getCrpClusterOfActivity().getIdentifier().equals(activitiyId)
            && year == projectBudget.getYear() && type == projectBudget.getBudgetType().getId().longValue()) {
            return i;
          }

        }
        i++;
      }

    } else {
      project.setBudgetsCluserActvities(new ArrayList<>());
    }

    ProjectBudgetsCluserActvity projectBudget = new ProjectBudgetsCluserActvity();
    projectBudget.setCrpClusterOfActivity(
      crpClusterOfActivityManager.getCrpClusterOfActivityByIdentifierPhase(activitiyId, this.getActualPhase()));
    projectBudget.setYear(year);
    projectBudget.setBudgetType(budgetTypeManager.getBudgetTypeById(type));
    project.getBudgetsCluserActvities().add(projectBudget);

    return this.getIndexBudget(activitiyId, year, type);
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public double getRemaining(Long type, int year) throws ParseException {
    double remaining = 100;

    if (project.getBudgetsCluserActvities() != null) {
      for (ProjectBudgetsCluserActvity projectBudgetsCluserActvity : project.getBudgetsCluserActvities()) {
        if (projectBudgetsCluserActvity.getYear() == year
          && projectBudgetsCluserActvity.getBudgetType().getId().longValue() == type.longValue()) {
          if (projectBudgetsCluserActvity.getAmount() != null) {

            remaining = remaining - projectBudgetsCluserActvity.getAmount().doubleValue();
            remaining = this.round(remaining, 2);
          }
        }
      }
    }

    return remaining;
  }


  public double getRemainingGender(Long type, int year) throws ParseException {
    double remaining = 100;
    DecimalFormat df = new DecimalFormat("0.00");
    if (project.getBudgetsCluserActvities() != null) {
      for (ProjectBudgetsCluserActvity projectBudgetsCluserActvity : project.getBudgetsCluserActvities()) {
        if (projectBudgetsCluserActvity.getYear() == year
          && projectBudgetsCluserActvity.getBudgetType().getId().longValue() == type.longValue()) {
          if (projectBudgetsCluserActvity.getGenderPercentage() != null) {
            remaining = remaining - projectBudgetsCluserActvity.getGenderPercentage().doubleValue();
            remaining = this.round(remaining, 2);
          }
        }
      }
    }

    return remaining;
  }


  public Long getTotalAmount(Long type, int year) {

    long totalAmount = 0;
    double porcentage;
    try {
      porcentage = Math.abs(this.getRemaining(type, year) - 100);
      totalAmount = (long) (this.getTotalYearPartners(year, type) * (porcentage / 100));
      return totalAmount;
    } catch (ParseException e) {
      return new Long(0);
    }


  }

  public Long getTotalGender(Long type, int year) {

    long totalAmount = 0;
    double porcentage;
    try {
      porcentage = Math.abs(this.getRemainingGender(type, year) - 100);
      totalAmount = (long) (this.getTotalGenderPartners(year, type) * (porcentage / 100));
      return totalAmount;
    } catch (ParseException e) {
      return new Long(0);
    }


  }

  public long getTotalGenderPartners(int year, long type) {
    long total = 0;
    Project projectBD = projectManager.getProjectById(projectID);


    for (ProjectBudget projectBudget : projectBD.getProjectBudgets()) {
      if (year == projectBudget.getYear() && type == projectBudget.getBudgetType().getId().longValue()
        && projectBudget.getPhase().equals(this.getActualPhase()) && projectBudget.isActive()) {
        if (projectBudget.getGenderPercentage() != null && projectBudget.getAmount() != null) {
          total = (long) (total + (projectBudget.getAmount() * (projectBudget.getGenderPercentage() / 100)));
        }

      }


    }
    return total;
  }

  public double getTotalYearPartners(int year, long type) {
    double total = 0;
    Project projectBD = projectManager.getProjectById(projectID);


    for (ProjectBudget projectBudget : projectBD.getProjectBudgets()) {
      if (year == projectBudget.getYear() && type == projectBudget.getBudgetType().getId().longValue()
        && projectBudget.getPhase().equals(this.getActualPhase()) && projectBudget.isActive()) {
        if (projectBudget.getAmount() != null) {
          total = total + projectBudget.getAmount();
        }

      }


    }
    return total;
  }

  public String getTransaction() {
    return transaction;
  }


  public boolean hasBudgets(Long type, int year) {
    Project projectBD = projectManager.getProjectById(projectID);
    List<ProjectBudget> budgets = projectBD.getProjectBudgets().stream()
      .filter(c -> c.isActive() && c.getYear() == year && c.getPhase().equals(this.getActualPhase())
        && c.getBudgetType().getId().longValue() == type.longValue() && (c.getAmount() != null && c.getAmount() > 0))
      .collect(Collectors.toList());

    return budgets.size() > 0;
  }

  @Override
  public void prepare() throws Exception {
    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    // Budget Types list
    budgetTypesList = budgetTypeManager.findAll();

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Project history = (Project) auditLogManager.getHistory(transaction);

      if (history != null) {
        project = history;
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
        Project projectDb = projectManager.getProjectById(project.getId());
        project.setProjectInfo(projectDb.getProjecInfoPhase(this.getActualPhase()));
        project.getProjectInfo()
          .setProjectEditLeader(projectDb.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader());
        reader.close();
        this.setDraft(true);
      } else {
        this.setDraft(false);
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));

        project.setBudgetsCluserActvities(project.getProjectBudgetsCluserActvities().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));


      }

      /**
       * There is an issue where a CoA that is unmapped from the project - still appears here, this happens for
       * both auto-save files and non auto-save updates.
       */
      project.getBudgetsCluserActvities().removeIf(Objects::isNull);


      Project projectBD = projectManager.getProjectById(projectID);
      project.setProjectInfo(projectBD.getProjecInfoPhase(this.getActualPhase()));


      List<CrpClusterOfActivity> activities = new ArrayList<CrpClusterOfActivity>();
      for (ProjectClusterActivity crpClusterOfActivity : projectBD.getProjectClusterActivities().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
        activities.add(crpClusterOfActivity.getCrpClusterOfActivity());
      }
      project.setCrpActivities(activities);

    }
    if (project.getBudgetsCluserActvities() != null) {
      for (ProjectBudgetsCluserActvity projectBudgetsCluserActvity : project.getBudgetsCluserActvities()) {

        // This additional null check is required for auto save file after a CoA has been deleted from the project
        if (projectBudgetsCluserActvity.getCrpClusterOfActivity() != null) {
          projectBudgetsCluserActvity.setCrpClusterOfActivity(crpClusterOfActivityManager
            .getCrpClusterOfActivityById(projectBudgetsCluserActvity.getCrpClusterOfActivity().getId()));
        }

      }
    }

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_BUDGET_CLUSTER_BASE_PERMISSION, params));

    if (this.isHttpPost()) {


      // if (project.getCrpActivities() != null) {
      // project.getCrpActivities().clear();
      // }
      //
      // if (project.getBudgetsCluserActvities() != null) {
      // project.getBudgetsCluserActvities().clear();
      // }
      if (project.getBudgetsCluserActvities() != null) {
        project.getBudgetsCluserActvities().clear();
      }

    }

  }


  public double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {
      this.saveBasicBudgets();

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_BUDGETS_ACTVITIES_RELATION);
      relationsName.add(APConstants.PROJECT_INFO_RELATION);

      project = projectManager.getProjectById(projectID);
      project.setActiveSince(new Date());
      project.setModifiedBy(this.getCurrentUser());
      projectManager.saveProject(project, this.getActionName(), relationsName, this.getActualPhase());
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists()) {
        path.toFile().delete();
      }
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

      return NOT_AUTHORIZED;
    }

  }

  public void saveBasicBudgets() {
    Project projectDB = projectManager.getProjectById(projectID);


    for (ProjectBudgetsCluserActvity projectBudget : projectDB.getProjectBudgetsCluserActvities().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {

      if (project.getBudgetsCluserActvities() == null) {
        project.setBudgetsCluserActvities(new ArrayList<>());
      }
      if (projectBudget.getYear() == this.getCurrentCycleYear()) {
        if (!project.getBudgetsCluserActvities().contains(projectBudget)) {
          projectBudgetsCluserActvityManager.deleteProjectBudgetsCluserActvity(projectBudget.getId());

        }

      }
    }
    if (project.getBudgetsCluserActvities() != null) {
      for (ProjectBudgetsCluserActvity projectBudgetCluserActivityUI : project.getBudgetsCluserActvities()) {
        if (projectBudgetCluserActivityUI != null) {
          if (projectBudgetCluserActivityUI.getId() == null) {
            projectBudgetCluserActivityUI.setCreatedBy(this.getCurrentUser());

            projectBudgetCluserActivityUI.setActiveSince(new Date());
            projectBudgetCluserActivityUI.setActive(true);
            projectBudgetCluserActivityUI.setProject(project);
            projectBudgetCluserActivityUI.setModifiedBy(this.getCurrentUser());
            projectBudgetCluserActivityUI.setModificationJustification("");
            projectBudgetCluserActivityUI.setPhase(this.getActualPhase());

            if (projectBudgetCluserActivityUI.getCrpClusterOfActivity() != null) {
              projectBudgetsCluserActvityManager.saveProjectBudgetsCluserActvity(projectBudgetCluserActivityUI);
            }


          } else {
            ProjectBudgetsCluserActvity ProjectBudgetDB = projectBudgetsCluserActvityManager
              .getProjectBudgetsCluserActvityById(projectBudgetCluserActivityUI.getId());
            ProjectBudgetDB.setGenderPercentage(projectBudgetCluserActivityUI.getGenderPercentage());
            ProjectBudgetDB.setAmount(projectBudgetCluserActivityUI.getAmount());
            if (projectBudgetCluserActivityUI.getId() == 1056) {
              System.out.println("holi");
            }
            projectBudgetCluserActivityUI.setCreatedBy(ProjectBudgetDB.getCreatedBy());
            projectBudgetCluserActivityUI.setPhase(this.getActualPhase());
            projectBudgetCluserActivityUI.setActiveSince(ProjectBudgetDB.getActiveSince());
            projectBudgetCluserActivityUI.setActive(true);
            projectBudgetCluserActivityUI.setProject(project);
            projectBudgetCluserActivityUI.setModifiedBy(this.getCurrentUser());
            projectBudgetCluserActivityUI.setModificationJustification("");
            projectBudgetCluserActivityUI.setModifiedBy(this.getCurrentUser());
            projectBudgetCluserActivityUI =
              projectBudgetsCluserActvityManager.saveProjectBudgetsCluserActvity(projectBudgetCluserActivityUI);
          }


        }


      }

    }
  }

  public void setBudgetTypesList(List<BudgetType> budgetTypesList) {
    this.budgetTypesList = budgetTypesList;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
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
      validator.validate(this, project, true);
    }
  }


}
