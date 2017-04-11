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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.AgreementStatusEnum;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.projects.ProjectBudgetsValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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

public class ProjectBudgetByPartnersAction extends BaseAction {

  private static final long serialVersionUID = 7833194831832715444L;


  private InstitutionManager institutionManager;


  private BudgetTypeManager budgetTypeManager;


  private ProjectManager projectManager;

  private FundingSourceManager fundingSourceManager;
  private ProjectBudgetManager projectBudgetManager;


  private LiaisonInstitutionManager liaisonInstitutionManager;

  private ProjectBudgetsValidator projectBudgetsValidator;

  private CrpManager crpManager;


  private long projectID;

  private Crp loggedCrp;

  private Project project;
  private String transaction;
  private AuditLogManager auditLogManager;
  // Pre-loaded List to Bilateral Co-funded Projects Service.
  private Map<String, String> status;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<Institution> institutions;
  // Model for the view
  private Map<String, String> budgetTypes;
  private List<BudgetType> budgetTypesList;
  private Map<String, String> w3bilateralBudgetTypes;// List of W3/Bilateral budget types (W3, Bilateral).
  private List<ProjectPartner> projectPPAPartners; // Is used to list all the PPA partners that belongs to the project.
  private int budgetIndex;

  @Inject
  public ProjectBudgetByPartnersAction(APConfig config, InstitutionManager institutionManager,
    ProjectManager projectManager, CrpManager crpManager, ProjectBudgetManager projectBudgetManager,
    AuditLogManager auditLogManager, BudgetTypeManager budgetTypeManager, FundingSourceManager fundingSourceManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ProjectBudgetsValidator projectBudgetsValidator) {
    super(config);

    this.institutionManager = institutionManager;
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.projectBudgetManager = projectBudgetManager;
    this.auditLogManager = auditLogManager;
    this.budgetTypeManager = budgetTypeManager;
    this.fundingSourceManager = fundingSourceManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.projectBudgetsValidator = projectBudgetsValidator;
  }

  public boolean canAddFunding(long institutionID) {
    boolean permission = this.hasPermissionNoBase(
      this.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, loggedCrp.getAcronym()))

      || this.hasPermissionNoBase(
        this.generatePermission(Permission.PROJECT_FUNDING_W3_BASE_PERMISSION, loggedCrp.getAcronym()))


      || this
        .hasPermissionNoBase(this.generatePermission(Permission.PROJECT_FUNDING_W3_PROJECT_INSTITUTION_BASE_PERMISSION,
          loggedCrp.getAcronym(), projectID + "", institutionID + ""));
    return permission;
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


  public boolean canEditFunding(long type, long institutionID) {
    if (type == 1) {
      boolean permission = this.hasPermissionNoBase(
        this.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, loggedCrp.getAcronym()));
      return permission;
    }
    if (type == 2 || type == 3) {
      boolean permission = this
        .hasPermissionNoBase(this.generatePermission(Permission.PROJECT_FUNDING_W3_PROJECT_BASE_PERMISSION,
          loggedCrp.getAcronym(), projectID + ""))
        || this.hasPermissionNoBase(
          this.generatePermission(Permission.PROJECT_FUNDING_W3_PROJECT_INSTITUTION_BASE_PERMISSION,
            loggedCrp.getAcronym(), projectID + "", institutionID + ""));
      return permission;
    }
    return true;

  }


  public boolean canEditFundingSourceBudget() {
    return this.hasPermissionNoBase(
      this.generatePermission(Permission.PROJECT_FUNDING_SOURCE_ADD_BUDGET_PERMISSION, loggedCrp.getAcronym()));


  }

  public boolean canSearchFunding(long institutionID) {

    boolean permission = this.hasPermissionNoBase(
      this.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, loggedCrp.getAcronym()))

      || this.hasPermissionNoBase(
        this.generatePermission(Permission.PROJECT_FUNDING_W3_BASE_PERMISSION, loggedCrp.getAcronym()))

      || this.hasPermissionNoBase(this.generatePermission(Permission.PROJECT_FUNDING_W3_PROJECT_BASE_PERMISSION,
        loggedCrp.getAcronym(), projectID + ""))


      || this
        .hasPermissionNoBase(this.generatePermission(Permission.PROJECT_FUNDING_W3_PROJECT_INSTITUTION_BASE_PERMISSION,
          loggedCrp.getAcronym(), projectID + "", institutionID + ""));
    return permission;

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
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public ProjectBudget getBudget(Long institutionId, int year, long type) {

    if (!project.isBilateralProject()) {
      if (type == 1 || type == 4) {

        return project.getBudgets().get(this.getIndexBudget(institutionId, year, type));
      } else {
        ProjectBudget projectBudget = new ProjectBudget();
        projectBudget.setInstitution(institutionManager.getInstitutionById(institutionId));
        projectBudget.setYear(year);
        projectBudget.setBudgetType(budgetTypeManager.getBudgetTypeById(type));
        projectBudget.setAmount(new Double(0));
        projectBudget.setGenderPercentage(new Double(0));
        projectBudget.setGenderValue(new Double(0));


        return projectBudget;
      }
    } else {

      return project.getBudgets().get(this.getIndexBudget(institutionId, year, type));
    }
  }

  public int getBudgetIndex() {
    return budgetIndex;
  }


  public List<ProjectBudget> getBudgetsByPartner(Long institutionId, int year) {
    List<ProjectBudget> budgets = project.getBudgets().stream()
      .filter(c -> c != null && c.getInstitution() != null && c.getInstitution().getId() != null
        && c.getInstitution().getId().longValue() == institutionId.longValue() && c.getYear() == year)
      .collect(Collectors.toList());
    return budgets;
  }


  public Map<String, String> getBudgetTypes() {
    return budgetTypes;
  }


  public List<BudgetType> getBudgetTypesList() {
    return budgetTypesList;
  }


  public int getIndexBudget(long institutionId, int year, long type, long fundingSourceID) {
    if (project.getBudgets() != null) {
      int i = 0;
      for (ProjectBudget projectBudget : project.getBudgets()) {
        if (projectBudget != null) {
          if (projectBudget.getInstitution() != null) {
            if (projectBudget.getInstitution().getId().longValue() == institutionId && year == projectBudget.getYear()
              && projectBudget.getBudgetType().getId() != null
              && type == projectBudget.getBudgetType().getId().longValue()
              && projectBudget.getFundingSource().getId().longValue() == fundingSourceID) {
              return i;
            }

          }
        }

        i++;
      }

    } else {
      project.setBudgets(new ArrayList<>());
    }

    ProjectBudget projectBudget = new ProjectBudget();
    projectBudget.setInstitution(institutionManager.getInstitutionById(institutionId));
    projectBudget.setYear(year);
    projectBudget.setFundingSource(fundingSourceManager.getFundingSourceById(fundingSourceID));;
    projectBudget.setBudgetType(budgetTypeManager.getBudgetTypeById(type));
    project.getBudgets().add(projectBudget);

    return this.getIndexBudget(institutionId, year, type);
  }


  public int getIndexBudget(Long institutionId, int year, long type) {
    if (project.getBudgets() != null) {
      int i = 0;
      for (ProjectBudget projectBudget : project.getBudgets()) {
        if (projectBudget != null) {
          if (projectBudget.getInstitution() != null) {
            if (projectBudget.getInstitution().getId().longValue() == institutionId.longValue()
              && year == projectBudget.getYear() && type == projectBudget.getBudgetType().getId().longValue()) {
              return i;
            }

          }
        }

        i++;
      }

    } else {
      project.setBudgets(new ArrayList<>());
    }

    ProjectBudget projectBudget = new ProjectBudget();
    projectBudget.setInstitution(institutionManager.getInstitutionById(institutionId));
    projectBudget.setYear(year);
    projectBudget.setBudgetType(budgetTypeManager.getBudgetTypeById(type));
    project.getBudgets().add(projectBudget);

    return this.getIndexBudget(institutionId, year, type);
  }


  public List<Institution> getInstitutions() {
    return institutions;
  }

  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
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

  public List<ProjectPartner> getProjectPPAPartners() {
    return projectPPAPartners;
  }


  public Map<String, String> getStatus() {
    return status;
  }

  public String getTotalAmount(long institutionId, int year, long budgetType) {
    return projectBudgetManager.amountByBudgetType(institutionId, year, budgetType, projectID);
  }

  public double getTotalGender(long institutionId, int year, long budgetType) {

    List<ProjectBudget> budgets = projectBudgetManager.getByParameters(institutionId, year, budgetType, projectID);

    double totalGender = 0;
    if (budgets != null) {
      for (ProjectBudget projectBudget : budgets) {
        double amount = projectBudget.getAmount() != null ? projectBudget.getAmount() : 0;
        double gender = projectBudget.getGenderPercentage() != null ? projectBudget.getGenderPercentage() : 0;

        totalGender = totalGender + (amount * (gender / 100));
      }
    }

    return totalGender;
  }


  public double getTotalGenderPer(long institutionId, int year, long budgetType) {

    String totalAmount = this.getTotalAmount(institutionId, year, budgetType);

    double dTotalAmount = Double.parseDouble(totalAmount);

    double totalGender = this.getTotalGender(institutionId, year, budgetType);

    if (dTotalAmount != 0) {
      return (totalGender * 100) / dTotalAmount;
    } else {
      return 0.0;
    }
  }


  public double getTotalYear(int year, long type) {
    double total = 0;
    if (project.getBudgets() != null) {

      for (ProjectBudget projectBudget : project.getBudgets()) {
        if (projectBudget != null) {
          if (projectBudget.getBudgetType() != null) {
            if (year == projectBudget.getYear() && type == projectBudget.getBudgetType().getId().longValue()) {
              if (projectBudget.getAmount() != null) {
                total = total + projectBudget.getAmount();
              }
            }
          }
        }
      }
    }


    return total;
  }


  public String getTransaction() {
    return transaction;
  }


  public Map<String, String> getW3bilateralBudgetTypes() {
    return w3bilateralBudgetTypes;
  }


  public boolean isPPA(Institution institution) {
    if (institution == null) {
      return false;
    }

    if (institution.getId() != null) {
      institution = institutionManager.getInstitutionById(institution.getId());
      if (institution != null) {
        if (institution.getCrpPpaPartners().stream()
          .filter(c -> c.getCrp().getId().longValue() == loggedCrp.getId().longValue() && c.isActive())
          .collect(Collectors.toList()).size() > 0) {
          return true;
        }
      }

    }

    return false;
  }


  /**
   * Load the Lists whit the information to created a project bilateral co-funded, this is a pre-load to show in the
   * project bilateral co-funded creation popup
   */
  public void loadCofundedInfoList() {
    status = new HashMap<>();
    List<AgreementStatusEnum> list = Arrays.asList(AgreementStatusEnum.values());
    for (AgreementStatusEnum agreementStatusEnum : list) {
      status.put(agreementStatusEnum.getStatusId(), agreementStatusEnum.getStatus());
    }

    institutions = institutionManager.findAll();


    liaisonInstitutions = new ArrayList<>();

    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions());
    liaisonInstitutions.addAll(
      liaisonInstitutionManager.findAll().stream().filter(c -> c.getCrp() == null).collect(Collectors.toList()));


  }


  @Override
  public void prepare() throws Exception {
    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    w3bilateralBudgetTypes = new HashMap<>();

    // Budget Types list
    budgetTypesList = budgetTypeManager.findAll();

    budgetTypes = new HashMap<>();

    for (BudgetType budgetType : budgetTypeManager.findAll()) {
      if (budgetType.getId().intValue() == 1) {
        if (this.hasPermissionNoBase(
          this.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, loggedCrp.getAcronym()))) {
          budgetTypes.put(budgetType.getId().toString(), budgetType.getName());
        }
      } else {
        budgetTypes.put(budgetType.getId().toString(), budgetType.getName());
      }

    }
    w3bilateralBudgetTypes.put("2", "W3");
    w3bilateralBudgetTypes.put("3", "Bilateral");
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

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        project = (Project) autoSaveReader.readFromJson(jReader);
        Project projectDb = projectManager.getProjectById(project.getId());
        project.setProjectEditLeader(projectDb.isProjectEditLeader());
        project.setAdministrative(projectDb.getAdministrative());
        reader.close();

        if (project.getBudgets() != null) {
          for (ProjectBudget projectBudget : project.getBudgets()) {
            if (projectBudget != null && projectBudget.getFundingSource() != null) {
              projectBudget
                .setFundingSource(fundingSourceManager.getFundingSourceById(projectBudget.getFundingSource().getId()));
            }

          }
        }

        this.setDraft(true);
      } else {
        this.setDraft(false);

        if (!project.isBilateralProject()) {
          project
            .setBudgets(project.getProjectBudgets().stream().filter(c -> c.isActive()).collect(Collectors.toList()));


        } else {
          project
            .setBudgets(project.getProjectBudgets().stream().filter(c -> c.isActive()).collect(Collectors.toList()));


        }

        System.out.println("Size budgets" + project.getBudgets().size());
      }

      // Pre-load Project Co-Funded Lists.
      this.loadCofundedInfoList();


      Project projectBD = projectManager.getProjectById(projectID);
      project.setStartDate(projectBD.getStartDate());
      project.setEndDate(projectBD.getEndDate());

      project
        .setPartners(projectBD.getProjectPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

      for (ProjectPartner projectPartner : project.getPartners()) {
        projectPartner.setPartnerPersons(
          projectPartner.getProjectPartnerPersons().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      }


      this.projectPPAPartners = new ArrayList<ProjectPartner>();
      for (ProjectPartner pp : project.getPartners()) {
        if (this.isPPA(pp.getInstitution())) {
          this.projectPPAPartners.add(pp);
        }
      }
    }


    if (project.getBudgets() != null) {
      if (project.getBudgets().size() == 0) {
        budgetIndex = 0;
      } else {
        budgetIndex = project.getBudgets().size() - 1;
      }
    } else {
      budgetIndex = 0;
    }


    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_BUDGET_BASE_PERMISSION, params));


    ProjectPartner leader = project.getLeader();
    if (leader != null) {
      if (project.isBilateralProject()) {
        project.getPartners().clear();
        project.getPartners().add(leader);
      } else {
        // First we remove the element from the array.
        project.getPartners().remove(leader);
        // then we add it to the first position.
        project.getPartners().add(0, leader);
      }

    }

    if (this.isHttpPost()) {
      if (project.getPartners() != null) {
        project.getPartners().clear();
      }


      if (project.getBudgets() != null) {
        project.getBudgets().clear();

      }


    }

  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {
      this.saveBasicBudgets();
      Project projectDB = projectManager.getProjectById(projectID);

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_BUDGETS_RELATION);

      project = projectManager.getProjectById(projectID);
      project.setModifiedBy(this.getCurrentUser());
      project.setActiveSince(new Date());
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
    } else {
      return NOT_AUTHORIZED;
    }

  }

  public void saveBasicBudgets() {
    Project projectDB = projectManager.getProjectById(projectID);

    if (!projectDB.isBilateralProject()) {
      projectDB
        .setBudgets(projectDB.getProjectBudgets().stream().filter(c -> c.isActive()).collect(Collectors.toList()));


    } else {
      projectDB
        .setBudgets(projectDB.getProjectBudgets().stream().filter(c -> c.isActive()).collect(Collectors.toList()));


    }

    for (ProjectBudget projectBudget : projectDB.getBudgets().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {

      if (project.getBudgets() == null) {
        project.setBudgets(new ArrayList<>());
      }
      if (projectBudget.getYear() == this.getCurrentCycleYear()) {
        if (!project.getBudgets().contains(projectBudget)) {
          projectBudgetManager.deleteProjectBudget(projectBudget.getId());

        }
      }

    }

    if (project.getBudgets() != null) {
      for (ProjectBudget projectBudget : project.getBudgets()) {
        if (projectBudget != null) {
          this.saveBudget(projectBudget);
        }
      }
    }
  }


  public void saveBudget(ProjectBudget projectBudget) {
    if (projectBudget.getId() == null) {
      projectBudget.setCreatedBy(this.getCurrentUser());

      projectBudget.setActiveSince(new Date());
      projectBudget.setActive(true);
      projectBudget.setProject(project);
      projectBudget.setModifiedBy(this.getCurrentUser());
      projectBudget.setModificationJustification("");

    } else {
      ProjectBudget ProjectBudgetDB = projectBudgetManager.getProjectBudgetById(projectBudget.getId());
      projectBudget.setCreatedBy(ProjectBudgetDB.getCreatedBy());

      projectBudget.setActiveSince(ProjectBudgetDB.getActiveSince());
      projectBudget.setActive(true);
      projectBudget.setProject(project);
      projectBudget.setModifiedBy(this.getCurrentUser());
      projectBudget.setModificationJustification("");
    }


    projectBudgetManager.saveProjectBudget(projectBudget);
  }


  public void setBudgetIndex(int budgetIndex) {
    this.budgetIndex = budgetIndex;
  }

  public void setBudgetTypes(Map<String, String> budgetTypes) {
    this.budgetTypes = budgetTypes;
  }


  public void setBudgetTypesList(List<BudgetType> budgetTypesList) {
    this.budgetTypesList = budgetTypesList;
  }

  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
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


  public void setProjectPPAPartners(List<ProjectPartner> projectPPAPartners) {
    this.projectPPAPartners = projectPPAPartners;
  }

  public void setStatus(Map<String, String> status) {
    this.status = status;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  public void setW3bilateralBudgetTypes(Map<String, String> w3bilateralBudgetTypes) {
    this.w3bilateralBudgetTypes = w3bilateralBudgetTypes;
  }


  @Override
  public void validate() {
    if (save) {
      projectBudgetsValidator.validate(this, project, true);
    }
  }

}
