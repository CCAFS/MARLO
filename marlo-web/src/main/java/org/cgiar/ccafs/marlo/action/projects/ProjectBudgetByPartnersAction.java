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
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.AgreementStatusEnum;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryDifference;
import org.cgiar.ccafs.marlo.validation.projects.ProjectBudgetsValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectSectionValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

public class ProjectBudgetByPartnersAction extends BaseAction {


  private static final long serialVersionUID = 7833194831832715444L;


  // Managers
  private InstitutionManager institutionManager;
  private BudgetTypeManager budgetTypeManager;
  private ProjectManager projectManager;
  private FundingSourceManager fundingSourceManager;
  private ProjectBudgetManager projectBudgetManager;
  private ProjectPartnerManager projectPartnerManager;
  private PhaseManager phaseManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private GlobalUnitManager crpManager;
  private AuditLogManager auditLogManager;
  private GlobalUnitProjectManager globalUnitProjectManager;
  // Variables
  private ProjectBudgetsValidator projectBudgetsValidator;
  private long projectID;
  private GlobalUnit loggedCrp;
  private Project project;
  private String transaction;
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
  private long sharedPhaseID;
  private ProjectSectionValidator<ProjectBudgetByPartnersAction> projectSectionValidator;

  @Inject
  public ProjectBudgetByPartnersAction(APConfig config, InstitutionManager institutionManager,
    ProjectManager projectManager, GlobalUnitManager crpManager, ProjectBudgetManager projectBudgetManager,
    AuditLogManager auditLogManager, BudgetTypeManager budgetTypeManager, FundingSourceManager fundingSourceManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ProjectBudgetsValidator projectBudgetsValidator,
    ProjectPartnerManager projectPartnerManager, PhaseManager phaseManager,
    GlobalUnitProjectManager globalUnitProjectManager,
    ProjectSectionValidator<ProjectBudgetByPartnersAction> projectSectionValidator) {
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
    this.projectPartnerManager = projectPartnerManager;
    this.phaseManager = phaseManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.projectSectionValidator = projectSectionValidator;

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
    if (type == 2 || type == 3 || type == 4) {

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

  public boolean canEditGender() {
    return this.hasPermissionNoBase(this.generatePermission(Permission.PROJECT_GENDER_PROJECT_BASE_PERMISSION,
      loggedCrp.getAcronym(), projectID + ""));
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

  public boolean existOnYear(Long partnerId, int year) {

    GlobalUnitProject gp = globalUnitProjectManager.findByProjectId(project.getId());
    ProjectPartner projectPartner = projectPartnerManager.getProjectPartnerById(partnerId.longValue());
    Phase phase = phaseManager.getPhaseById(this.getActualPhase().getId());

    if (year < this.getActualPhase().getYear()) {
      phase = phaseManager.findCycle(this.getActualPhase().getDescription(), year, true, gp.getGlobalUnit().getId());
      if (phase == null) {
        phase = phaseManager.findCycle(this.getActualPhase().getDescription(), year, false, gp.getGlobalUnit().getId());
      }
    }

    if (phase != null) {
      List<ProjectPartner> partners = phase.getPartners().stream()
        .filter(c -> c.getProject().getId().longValue() == projectID
          && projectPartner.getInstitution().getId().equals(c.getInstitution().getId()) && c.isActive())
        .collect(Collectors.toList());
      if (!partners.isEmpty()) {
        return true;
      }
    }
    return false;
  }

  /**
   * The name of the autosave file is constructed and the path is searched
   * 
   * @return Auto save file path
   */
  private Path getAutoSaveFilePath() {
    // get the class simple name
    String composedClassName = project.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public ProjectBudget getBudget(Long institutionId, int year, long type) {

    return project.getBudgets().get(this.getIndexBudget(institutionId, year, type));
  }

  public int getBudgetIndex() {
    return budgetIndex;
  }

  public List<ProjectBudget> getBudgetsByPartner(Long institutionId, int year) {


    Phase sharedPhase = phaseManager.getPhaseById(sharedPhaseID);

    List<ProjectBudget> budgets = project.getBudgets().stream()
      .filter(c -> c != null && c.getInstitution() != null && c.getInstitution().getId() != null
        && c.getFundingSource().getFundingSourceInfo(sharedPhase) != null
        && c.getInstitution().getId().longValue() == institutionId.longValue() && c.getYear() == year
        && c.getPhase().equals(sharedPhase))
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

    Phase sharedPhase = phaseManager.getPhaseById(sharedPhaseID);

    if (project.getBudgets() != null) {
      int i = 0;
      for (ProjectBudget projectBudget : project.getBudgets()) {
        if (projectBudget != null) {
          if (projectBudget.getInstitution() != null) {
            if (projectBudget.getInstitution().getId().longValue() == institutionId && year == projectBudget.getYear()
              && projectBudget.getBudgetType().getId() != null
              && type == projectBudget.getBudgetType().getId().longValue()
              && projectBudget.getFundingSource().getId().longValue() == fundingSourceID
              && projectBudget.getFundingSource().getFundingSourceInfo(sharedPhase) != null) {
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
    projectBudget.getFundingSource().getFundingSourceInfo(this.getActualPhase());
    project.getBudgets().add(projectBudget);

    return this.getIndexBudget(institutionId, year, type, fundingSourceID);
  }


  public int getIndexBudget(Long institutionId, int year, long type) {
    Phase sharedPhase = phaseManager.getPhaseById(sharedPhaseID);
    if (project.getBudgets() != null) {
      int i = 0;
      for (ProjectBudget projectBudget : project.getBudgets()) {
        if (projectBudget != null) {
          if (projectBudget.getInstitution() != null) {
            if (projectBudget.getInstitution().getId().longValue() == institutionId.longValue()
              && projectBudget.getFundingSource().getFundingSourceInfo(sharedPhase) != null
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


  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }


  public List<ProjectPartner> getProjectPPAPartners() {
    return projectPPAPartners;
  }

  public long getSharedPhaseID() {
    return sharedPhaseID;
  }

  public Map<String, String> getStatus() {
    return status;
  }


  public String getTotalAmount(long institutionId, int year, long budgetType, Integer coFinancing) {
    Phase sharedPhase = phaseManager.getPhaseById(sharedPhaseID);
    return projectBudgetManager.amountByBudgetType(institutionId, year, budgetType, projectID, coFinancing,
      sharedPhase.getId());
  }

  public double getTotalGender(long institutionId, int year, long budgetType, Integer coFinancing) {
    Phase sharedPhase = phaseManager.getPhaseById(sharedPhaseID);
    List<ProjectBudget> budgets = projectBudgetManager.getByParameters(institutionId, year, budgetType, projectID,
      coFinancing, sharedPhase.getId());

    double totalGender = 0;
    if (budgets != null) {
      for (ProjectBudget projectBudget : budgets) {
        if (projectBudget.getPhase().equals(sharedPhase)) {
          double amount = projectBudget.getAmount() != null ? projectBudget.getAmount() : 0;
          double gender = projectBudget.getGenderPercentage() != null ? projectBudget.getGenderPercentage() : 0;

          totalGender = totalGender + (amount * (gender / 100));
        }

      }
    }

    return totalGender;
  }

  public double getTotalGenderPer(long institutionId, int year, long budgetType, Integer coFinancing) {

    String totalAmount = this.getTotalAmount(institutionId, year, budgetType, coFinancing);

    double dTotalAmount = Double.parseDouble(totalAmount);

    double totalGender = this.getTotalGender(institutionId, year, budgetType, coFinancing);

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


  @Override
  public boolean isPPA(Institution institution) {

    GlobalUnitProject gp = globalUnitProjectManager.findByProjectId(project.getId());

    if (institution == null) {
      return false;
    }

    if (institution.getId() != null) {
      institution = institutionManager.getInstitutionById(institution.getId());
      if (institution != null) {
        if (institution.getCrpPpaPartners().stream()
          .filter(c -> c.getCrp().getId().longValue() == gp.getGlobalUnit().getId().longValue() && c.isActive())
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

    GlobalUnitProject gp = globalUnitProjectManager.findByProjectId(project.getId());

    status = new HashMap<>();
    List<AgreementStatusEnum> list = Arrays.asList(AgreementStatusEnum.values());
    for (AgreementStatusEnum agreementStatusEnum : list) {
      status.put(agreementStatusEnum.getStatusId(), agreementStatusEnum.getStatus());
    }

    institutions = institutionManager.findAll();


    liaisonInstitutions = new ArrayList<>();

    liaisonInstitutions.addAll(gp.getGlobalUnit().getLiaisonInstitutions());
    liaisonInstitutions.addAll(
      liaisonInstitutionManager.findAll().stream().filter(c -> c.getCrp() == null).collect(Collectors.toList()));


  }


  @Override
  public void prepare() throws Exception {
    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    Phase phase = this.getActualPhase();
    sharedPhaseID = phase.getId();

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

        List<HistoryDifference> differences = new ArrayList<>();
        Map<String, String> specialList = new HashMap<>();
        int i = 0;
        project.setBudgets(project.getProjectBudgets().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        for (ProjectBudget budget : project.getBudgets()) {
          budget.setFundingSource(fundingSourceManager.getFundingSourceById(budget.getFundingSource().getId()));
          // int[] index = new int[1];
          // index[0] = i;
          // differences.addAll(historyComparator.getDifferencesList(budget, transaction, specialList,
          // "project.budgets[" + i + "]", "project", 1));
          // i++;
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
        Project projectDb = projectManager.getProjectById(project.getId());
        project.setProjectInfo(projectDb.getProjecInfoPhase(this.getActualPhase()));
        project.getProjectInfo()
          .setProjectEditLeader(projectDb.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader());
        reader.close();
        // Dont show Complete or Canceled Funding Sources
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

        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        project.setBudgets(project.getProjectBudgets().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())
            && c.getFundingSource().getFundingSourceInfo(this.getActualPhase()) != null
            && (c.getFundingSource().getFundingSourceInfo(this.getActualPhase()).getStatus() != 3
              || c.getFundingSource().getFundingSourceInfo(this.getActualPhase()).getStatus() != 5))
          .collect(Collectors.toList()));
      }

      // Pre-load Project Co-Funded Lists.
      this.loadCofundedInfoList();


      Project projectBD = projectManager.getProjectById(projectID);
      project.setProjectInfo(projectBD.getProjecInfoPhase(this.getActualPhase()));


      project.setPartners(projectBD.getProjectPartners().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
        .collect(Collectors.toList()));

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

      // First we remove the element from the array.
      project.getPartners().remove(leader);
      // then we add it to the first position.
      project.getPartners().add(0, leader);
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

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_BUDGETS_RELATION);
      relationsName.add(APConstants.PROJECT_INFO_RELATION);

      project = projectManager.getProjectById(projectID);
      /**
       * The following is required because we need to update something on the @Project if we want a row created in
       * the auditlog table.
       */
      this.setModificationJustification(project);
      projectManager.saveProject(project, this.getActionName(), relationsName, this.getActualPhase());
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
    List<ProjectBudget> budgets = project.getBudgets();
    Project projectDB = projectManager.getProjectById(projectID);


    for (ProjectBudget projectBudget : projectDB.getProjectBudgets().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {

      if (budgets == null) {
        budgets = (new ArrayList<>());
      }
      if (projectBudget.getYear() == this.getCurrentCycleYear()) {

        if (!budgets.contains(projectBudget)) {
          projectBudgetManager.deleteProjectBudget(projectBudget.getId());

        }
      }

    }

    if (budgets != null) {
      for (ProjectBudget projectBudget : budgets) {
        if (projectBudget != null) {
          if (projectBudget.getYear() >= this.getActualPhase().getYear()) {
            this.saveBudget(projectBudget, projectDB);
          }

        }
      }
    }

  }

  public void saveBudget(ProjectBudget projectBudget, Project projectDB) {

    /**
     * If the entity is new we can save it as is.
     */
    if (projectBudget.getId() == null) {

      projectBudget.setProject(projectDB);
      projectBudget.setPhase(this.getActualPhase());

      projectBudgetManager.saveProjectBudget(projectBudget);
      return;
    }
    /**
     * The entity is existing so we need to retrieve and then update it. This is necessary to make sure we don't try and
     * persist a projectBudget with no manadatory logging fields.
     */
    ProjectBudget projectBudgetDB = projectBudgetManager.getProjectBudgetById(projectBudget.getId());
    projectBudgetDB.setPhase(this.getActualPhase());
    projectBudgetDB.setProject(projectDB);
    projectBudgetDB.setAmount(projectBudget.getAmount());
    projectBudgetDB.setBudgetType(projectBudget.getBudgetType());
    projectBudgetDB.setFundingSource(projectBudget.getFundingSource());
    projectBudgetDB.setGenderPercentage(projectBudget.getGenderPercentage());
    projectBudgetDB.setGenderValue(projectBudget.getGenderValue());
    projectBudgetDB.setInstitution(projectBudget.getInstitution());
    projectBudgetDB.setYear(projectBudget.getYear());

    projectBudgetManager.saveProjectBudget(projectBudgetDB);

    this.projectSectionValidator.validateProjectBudgetsCoAs(this, this.getProjectID(), false);
    this.projectSectionValidator.validateProjectBudgetsFlagship(this, this.getProjectID(), false);
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

  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setProjectPPAPartners(List<ProjectPartner> projectPPAPartners) {
    this.projectPPAPartners = projectPPAPartners;
  }


  public void setSharedPhaseID(long sharedPhaseID) {
    this.sharedPhaseID = sharedPhaseID;
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
