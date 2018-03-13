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

package org.cgiar.ccafs.marlo.action.powb;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.PowbFinancialExpenditureManager;
import org.cgiar.ccafs.marlo.data.manager.PowbFinancialPlanManager;
import org.cgiar.ccafs.marlo.data.manager.PowbFinancialPlannedBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialExpenditure;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialPlan;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialPlannedBudget;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.powb.FinancialPlanValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class FinancialPlanAction extends BaseAction {

  private static final long serialVersionUID = 8792953923111769705L;
  // Managers
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private PowbSynthesisManager powbSynthesisManager;
  private CrpProgramManager crpProgramManager;
  private AuditLogManager auditLogManager;
  private PowbFinancialPlanManager powbFinancialPlanManager;
  private PowbFinancialExpenditureManager powbFinancialExpenditureManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  private PowbFinancialPlannedBudgetManager powbFinancialPlannedBudgetManager;
  // Model for the front-end
  private PowbSynthesis powbSynthesis;
  private Long powbSynthesisID;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<PowbExpenditureAreas> powbExpenditureAreas;
  private String transaction;
  private LiaisonInstitution liaisonInstitution;
  private Long liaisonInstitutionID;
  private GlobalUnit loggedCrp;
  private FinancialPlanValidator validator;


  @Inject
  public FinancialPlanAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, AuditLogManager auditLogManager,
    CrpProgramManager crpProgramManager, PowbSynthesisManager powbSynthesisManager, FinancialPlanValidator validator,
    PowbFinancialPlanManager powbFinancialPlanManager, PowbFinancialExpenditureManager powbFinancialExpenditureManager,
    PowbExpenditureAreasManager powbExpenditureAreasManager,
    PowbFinancialPlannedBudgetManager powbFinancialPlannedBudgetManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.crpProgramManager = crpProgramManager;
    this.auditLogManager = auditLogManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.validator = validator;
    this.powbFinancialPlanManager = powbFinancialPlanManager;
    this.powbFinancialExpenditureManager = powbFinancialExpenditureManager;
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
    this.powbFinancialPlannedBudgetManager = powbFinancialPlannedBudgetManager;
  }

  @Override
  public String cancel() {
    return SUCCESS;
  }

  private void createEmptyFinancialPlan() {
    if (powbSynthesis.getFinancialPlan() == null && this.isPMU()) {
      PowbFinancialPlan newPowbFinancialPlan = new PowbFinancialPlan();
      newPowbFinancialPlan.setActive(true);
      newPowbFinancialPlan.setCreatedBy(this.getCurrentUser());
      newPowbFinancialPlan.setModifiedBy(this.getCurrentUser());
      newPowbFinancialPlan.setActiveSince(new Date());
      newPowbFinancialPlan.setFinancialPlanIssues("");
      newPowbFinancialPlan.setPowbSynthesis(powbSynthesis);
      powbSynthesis.setFinancialPlan(newPowbFinancialPlan);
      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    }
  }

  public Long firstFlagship() {
    List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList()));
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
    long liaisonInstitutionId = liaisonInstitutions.get(0).getId();
    return liaisonInstitutionId;
  }

  private Path getAutoSaveFilePath() {
    String composedClassName = powbSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = powbSynthesis.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<PowbExpenditureAreas> getExpenditureAreas() {
    List<PowbExpenditureAreas> expenditureAreaList = powbExpenditureAreasManager.findAll().stream()
      .filter(e -> e.isActive() && e.getIsExpenditure()).collect(Collectors.toList());
    if (expenditureAreaList != null) {
      return expenditureAreaList;
    } else {
      return new ArrayList<>();
    }
  }

  public List<LiaisonInstitution> getFlagships() {
    List<LiaisonInstitution> flagshipsList = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive())
      .collect(Collectors.toList());
    if (flagshipsList != null) {
      flagshipsList.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
      return flagshipsList;
    } else {
      return new ArrayList<>();
    }
  }

  public LiaisonInstitution getLiaisonInstitution() {
    return liaisonInstitution;
  }


  public Long getLiaisonInstitutionID() {
    return liaisonInstitutionID;
  }

  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }


  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  // Method to download link file
  public String getPath(Long liaisonInstitutionID) {
    return config.getDownloadURL() + "/" + this.getPowbSourceFolder(liaisonInstitutionID).replace('\\', '/');
  }

  public List<PowbExpenditureAreas> getPlannedBudgetAreas() {
    List<PowbExpenditureAreas> plannedBudgetAreasList = powbExpenditureAreasManager.findAll().stream()
      .filter(e -> e.isActive() && !e.getIsExpenditure()).collect(Collectors.toList());
    if (plannedBudgetAreasList != null) {
      return plannedBudgetAreasList;
    } else {
      return new ArrayList<>();
    }
  }

  public List<PowbExpenditureAreas> getPowbExpenditureAreas() {
    return powbExpenditureAreas;
  }

  public PowbFinancialExpenditure getPowbFinancialExpenditurebyExpenditureArea(Long expenditureAreaID) {
    if (expenditureAreaID != null) {
      List<PowbFinancialExpenditure> powbFinancialExpenditure =
        powbSynthesis.getPowbFinancialExpendituresList().stream()
          .filter(c -> c.getPowbExpenditureArea().getId().equals(expenditureAreaID)).collect(Collectors.toList());
      if (powbFinancialExpenditure != null && !powbFinancialExpenditure.isEmpty()) {
        return powbFinancialExpenditure.get(0);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  public PowbFinancialPlannedBudget getPowbFinancialPlanBudget(Long plannedBudgetRelationID, Boolean isLiaison) {
    if (isLiaison) {
      LiaisonInstitution liaisonInstitution =
        liaisonInstitutionManager.getLiaisonInstitutionById(plannedBudgetRelationID);
      if (liaisonInstitution != null) {
        List<PowbFinancialPlannedBudget> powbFinancialPlannedBudgetList = powbSynthesis
          .getPowbFinancialPlannedBudgetList().stream()
          .filter(
            p -> p.getLiaisonInstitution() != null && p.getLiaisonInstitution().getId().equals(plannedBudgetRelationID))
          .collect(Collectors.toList());
        if (powbFinancialPlannedBudgetList != null && !powbFinancialPlannedBudgetList.isEmpty()) {
          PowbFinancialPlannedBudget powbFinancialPlannedBudget = powbFinancialPlannedBudgetList.get(0);

          if (liaisonInstitution.getCrpProgram() != null) {
            this.loadFlagShipBudgetInfo(liaisonInstitution.getCrpProgram());
            powbFinancialPlannedBudget.setW1w2(liaisonInstitution.getCrpProgram().getW1());
            powbFinancialPlannedBudget.setW3Bilateral(liaisonInstitution.getCrpProgram().getW3());
            powbFinancialPlannedBudget.setCenterFunds(liaisonInstitution.getCrpProgram().getCenterFunds());

            powbFinancialPlannedBudget.setEditBudgets(false);

          }

          return powbFinancialPlannedBudget;
        } else {
          PowbFinancialPlannedBudget powbFinancialPlannedBudget = new PowbFinancialPlannedBudget();
          powbFinancialPlannedBudget.setLiaisonInstitution(liaisonInstitution);

          if (liaisonInstitution.getCrpProgram() != null) {
            this.loadFlagShipBudgetInfo(liaisonInstitution.getCrpProgram());
            powbFinancialPlannedBudget.setW1w2(new Double(liaisonInstitution.getCrpProgram().getW1()));
            powbFinancialPlannedBudget.setW3Bilateral(liaisonInstitution.getCrpProgram().getW3());
            powbFinancialPlannedBudget.setCenterFunds(liaisonInstitution.getCrpProgram().getCenterFunds());

            powbFinancialPlannedBudget.setEditBudgets(false);

          }

          return powbFinancialPlannedBudget;
        }
      } else {
        return null;
      }
    } else {
      PowbExpenditureAreas powbExpenditureArea =
        powbExpenditureAreasManager.getPowbExpenditureAreasById(plannedBudgetRelationID);

      if (powbExpenditureArea != null) {
        List<PowbFinancialPlannedBudget> powbFinancialPlannedBudgetList =
          powbSynthesis.getPowbFinancialPlannedBudgetList().stream().filter(p -> p.getPowbExpenditureArea() != null
            && p.getPowbExpenditureArea().getId().equals(plannedBudgetRelationID)).collect(Collectors.toList());
        if (powbFinancialPlannedBudgetList != null && !powbFinancialPlannedBudgetList.isEmpty()) {
          PowbFinancialPlannedBudget powbFinancialPlannedBudget = powbFinancialPlannedBudgetList.get(0);
          if (powbExpenditureArea.getExpenditureArea().equals("CRP Management & Support Cost")) {
            this.loadPMU(powbExpenditureArea);
            powbFinancialPlannedBudget.setW1w2(powbExpenditureArea.getW1());
            powbFinancialPlannedBudget.setW3Bilateral(powbExpenditureArea.getW3());
            powbFinancialPlannedBudget.setCenterFunds(powbExpenditureArea.getCenterFunds());

            powbFinancialPlannedBudget.setEditBudgets(false);
          }
          return powbFinancialPlannedBudget;
        } else {

          PowbFinancialPlannedBudget powbFinancialPlannedBudget = new PowbFinancialPlannedBudget();
          powbFinancialPlannedBudget.setPowbExpenditureArea(powbExpenditureArea);
          if (powbExpenditureArea.getExpenditureArea().equals("CRP Management & Support Cost")) {
            this.loadPMU(powbExpenditureArea);
            powbFinancialPlannedBudget.setW1w2(powbExpenditureArea.getW1());
            powbFinancialPlannedBudget.setW3Bilateral(powbExpenditureArea.getW3());
            powbFinancialPlannedBudget.setCenterFunds(powbExpenditureArea.getCenterFunds());

            powbFinancialPlannedBudget.setEditBudgets(false);
          }
          return powbFinancialPlannedBudget;


        }
      } else {
        return null;
      }
    }
  }

  // Method to get the download folder
  private String getPowbSourceFolder(Long liaisonInstitutionID) {
    LiaisonInstitution liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);
    return APConstants.POWB_FOLDER.concat(File.separator).concat(this.getCrpSession()).concat(File.separator)
      .concat(liaisonInstitution.getAcronym()).concat(File.separator).concat(this.getActionName().replace("/", "_"))
      .concat(File.separator);
  }


  public PowbSynthesis getPowbSynthesis() {
    return powbSynthesis;
  }

  public Long getPowbSynthesisID() {
    return powbSynthesisID;
  }

  public String getTransaction() {
    return transaction;
  }

  public boolean isFlagship() {
    boolean isFP = false;
    if (liaisonInstitution.getCrpProgram() != null) {
      CrpProgram crpProgram =
        crpProgramManager.getCrpProgramById(liaisonInstitution.getCrpProgram().getId().longValue());
      if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
        isFP = true;
      }
    }
    return isFP;
  }

  @Override
  public boolean isPMU() {
    boolean isFP = false;
    if (liaisonInstitution.getCrpProgram() == null) {
      isFP = true;
    }
    return isFP;
  }

  public void loadFlagShipBudgetInfo(CrpProgram crpProgram) {
    List<ProjectFocus> projects =
      crpProgram.getProjectFocuses().stream().filter(c -> c.getProject().isActive() && c.isActive()
        && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());
    Set<Project> myProjects = new HashSet();
    for (ProjectFocus projectFocus : projects) {
      Project project = projectFocus.getProject();
      if (project.isActive()) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        if (project.getProjectInfo() != null && project.getProjectInfo().getStatus() != null) {
          if (project.getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            myProjects.add(project);
          }
        }


      }
    }
    for (Project project : myProjects) {


      double w1 = project.getCoreBudget(this.getActualPhase().getYear(), this.getActualPhase());
      double w3 = project.getW3Budget(this.getActualPhase().getYear(), this.getActualPhase());
      double bilateral = project.getBilateralBudget(this.getActualPhase().getYear(), this.getActualPhase());
      double centerFunds = project.getCenterBudget(this.getActualPhase().getYear(), this.getActualPhase());

      List<ProjectBudgetsFlagship> budgetsFlagships = project.getProjectBudgetsFlagships().stream()
        .filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == crpProgram.getId().longValue()
          && c.getPhase().equals(this.getActualPhase()) && c.getYear() == this.getActualPhase().getYear())
        .collect(Collectors.toList());
      double percentageW1 = 0;
      double percentageW3 = 0;
      double percentageB = 0;
      double percentageCenterFunds = 0;


      if (!this.getCountProjectFlagships(project.getId())) {
        percentageW1 = 100;
        percentageW3 = 100;
        percentageB = 100;
        percentageCenterFunds = 100;

      }
      for (ProjectBudgetsFlagship projectBudgetsFlagship : budgetsFlagships) {
        switch (projectBudgetsFlagship.getBudgetType().getId().intValue()) {
          case 1:
            percentageW1 = percentageW1 + projectBudgetsFlagship.getAmount();
            break;
          case 2:
            percentageW3 = percentageW3 + projectBudgetsFlagship.getAmount();
            break;
          case 3:
            percentageB = percentageB + projectBudgetsFlagship.getAmount();
            break;
          case 4:
            percentageCenterFunds = percentageCenterFunds + projectBudgetsFlagship.getAmount();
            break;
          default:
            break;
        }
      }
      w1 = w1 * (percentageW1) / 100;
      w3 = w3 * (percentageW3) / 100;
      bilateral = bilateral * (percentageB) / 100;
      centerFunds = centerFunds * (percentageCenterFunds) / 100;

      crpProgram.setW1(crpProgram.getW1() + w1);
      crpProgram.setW3(crpProgram.getW3() + w3 + bilateral);
      crpProgram.setCenterFunds(crpProgram.getCenterFunds() + centerFunds);


    }
  }


  public void loadPMU(PowbExpenditureAreas liaisonInstitution) {
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    Set<Project> myProjects = new HashSet();
    for (GlobalUnitProject projectFocus : loggedCrp.getGlobalUnitProjects().stream()
      .filter(c -> c.isActive() && c.isOrigin()).collect(Collectors.toList())) {
      Project project = projectFocus.getProject();
      if (project.isActive()) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        if (project.getProjectInfo() != null && project.getProjectInfo().getStatus() != null) {
          if (project.getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            if (project.getProjecInfoPhase(this.getActualPhase()).getAdministrative() != null
              && project.getProjecInfoPhase(this.getActualPhase()).getAdministrative().booleanValue()) {
              myProjects.add(project);
            }

          }
        }


      }
    }
    for (Project project : myProjects) {


      double w1 = project.getCoreBudget(this.getActualPhase().getYear(), this.getActualPhase());
      double w3 = project.getW3Budget(this.getActualPhase().getYear(), this.getActualPhase());
      double bilateral = project.getBilateralBudget(this.getActualPhase().getYear(), this.getActualPhase());
      double centerFunds = project.getCenterBudget(this.getActualPhase().getYear(), this.getActualPhase());

      double percentageW1 = 0;
      double percentageW3 = 0;
      double percentageB = 0;
      double percentageCenterFunds = 0;


      percentageW1 = 100;
      percentageW3 = 100;
      percentageB = 100;
      percentageCenterFunds = 100;


      w1 = w1 * (percentageW1) / 100;
      w3 = w3 * (percentageW3) / 100;
      bilateral = bilateral * (percentageB) / 100;
      centerFunds = centerFunds * (percentageCenterFunds) / 100;

      liaisonInstitution.setW1(liaisonInstitution.getW1() + w1);
      liaisonInstitution.setW3(liaisonInstitution.getW3() + w3 + bilateral);
      liaisonInstitution.setCenterFunds(liaisonInstitution.getCenterFunds() + centerFunds);

    }
  }


  public List<Project> loadPMUProjects() {
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    List<Project> projectsToRet = new ArrayList<>();

    Set<Project> myProjects = new HashSet();
    for (GlobalUnitProject projectFocus : loggedCrp.getGlobalUnitProjects().stream()
      .filter(c -> c.isActive() && c.isOrigin()).collect(Collectors.toList())) {
      Project project = projectFocus.getProject();
      if (project.isActive()) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        if (project.getProjectInfo() != null && project.getProjectInfo().getStatus() != null) {
          if (project.getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            if (project.getProjecInfoPhase(this.getActualPhase()).getAdministrative() != null
              && project.getProjecInfoPhase(this.getActualPhase()).getAdministrative().booleanValue()) {
              myProjects.add(project);
            }

          }
        }


      }
    }
    for (Project project : myProjects) {


      double w1 = project.getCoreBudget(this.getActualPhase().getYear(), this.getActualPhase());
      double w3 = project.getW3Budget(this.getActualPhase().getYear(), this.getActualPhase());
      double bilateral = project.getBilateralBudget(this.getActualPhase().getYear(), this.getActualPhase());
      double centerFunds = project.getCenterBudget(this.getActualPhase().getYear(), this.getActualPhase());

      double percentageW1 = 0;
      double percentageW3 = 0;
      double percentageB = 0;
      double percentageCenterFunds = 0;


      percentageW1 = 100;
      percentageW3 = 100;
      percentageB = 100;
      percentageCenterFunds = 100;

      project.setW3Budget(w3);
      project.setCoreBudget(w1);
      project.setBilateralBudget(bilateral);
      project.setCentenFundsBudget(centerFunds);

      project.setPercentageW3(percentageW3);
      project.setPercentageW1(percentageW1);
      project.setPercentageBilateral(percentageB);
      project.setPercentageFundsBudget(percentageCenterFunds);


      w1 = w1 * (percentageW1) / 100;
      w3 = w3 * (percentageW3) / 100;
      bilateral = bilateral * (percentageB) / 100;
      centerFunds = centerFunds * (percentageCenterFunds) / 100;

      project.setTotalW3(w3);
      project.setTotalW1(w1);
      project.setTotalBilateral(bilateral);
      project.setTotalCenterFunds(centerFunds);


      projectsToRet.add(project);

    }
    return projectsToRet;

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

  @Override
  public void prepare() throws Exception {
    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    // Check history version
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {
      this.setPowbSynthesisIdHistory();
    } else {
      this.setPowbSynthesisParameters();
    }
    // Validate draft version
    if (powbSynthesis != null) {

      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        this.readJsonAndLoadPowbSynthesis(path);
      } else {
        this.setDraft(false);
        this.createEmptyFinancialPlan();
        powbSynthesis.setPowbFinancialPlannedBudgetList(powbSynthesis.getPowbFinancialPlannedBudget().stream()
          .filter(fp -> fp.isActive()).collect(Collectors.toList()));
        powbSynthesis.setPowbFinancialExpendituresList(powbSynthesis.getPowbFinancialExpenditures().stream()
          .filter(fe -> fe.isActive()).collect(Collectors.toList()));
      }
    }


    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = this.getFlagships();
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.getAcronym().equals("PMU") && c.isActive())
      .collect(Collectors.toList()));
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
    powbExpenditureAreas = new ArrayList<>();
    powbExpenditureAreas =
      powbExpenditureAreasManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());


    if (this.isFlagship()) {
      PowbSynthesis powbSynthesisDB =
        powbSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitution.getId());
      powbSynthesisID = powbSynthesisDB.getId();
    }

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), powbSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.POWB_SYNTHESIS_FINANCIAL_PLAN_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (powbSynthesis.getPowbFinancialPlannedBudgetList() != null) {
        powbSynthesis.getPowbFinancialPlannedBudgetList().clear();
      }
      if (powbSynthesis.getPowbFinancialExpendituresList() != null) {
        powbSynthesis.getPowbFinancialExpendituresList().clear();
      }
    }
  }

  private void readJsonAndLoadPowbSynthesis(Path path) throws IOException {
    BufferedReader reader = null;
    reader = new BufferedReader(new FileReader(path.toFile()));
    Gson gson = new GsonBuilder().create();
    JsonObject jReader = gson.fromJson(reader, JsonObject.class);
    AutoSaveReader autoSaveReader = new AutoSaveReader();
    // We read the JSON serialized by the front-end and cast it to the object
    powbSynthesis = (PowbSynthesis) autoSaveReader.readFromJson(jReader);
    powbSynthesisID = powbSynthesis.getId();
    this.setDraft(true);
    reader.close();
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {
      // Planned Budget
      if (powbSynthesis.getPowbFinancialPlannedBudgetList() != null
        && !powbSynthesis.getPowbFinancialPlannedBudgetList().isEmpty()) {
        for (PowbFinancialPlannedBudget PowbFinancialPlannedBudget : powbSynthesis
          .getPowbFinancialPlannedBudgetList()) {
          if (PowbFinancialPlannedBudget.getId() == null) {
            this.saveNewPlannedBudget(PowbFinancialPlannedBudget);
          } else {
            this.saveUpdatePlannedBudget(PowbFinancialPlannedBudget);
          }
        }
      }

      // FinancialPlan:
      this.saveUpdateFinancialPlan();
      // Financial Expenditures
      if (powbSynthesis.getPowbFinancialExpendituresList() != null
        && !powbSynthesis.getPowbFinancialExpendituresList().isEmpty()) {
        for (PowbFinancialExpenditure powbFinancialExpenditure : powbSynthesis.getPowbFinancialExpendituresList()) {
          if (powbFinancialExpenditure.getId() == null) {
            this.saveNewFinancialExpenditure(powbFinancialExpenditure);
          } else {
            this.saveUpdateFinancialExpenditure(powbFinancialExpenditure);
          }
        }
      }

      List<String> relationsName = new ArrayList<>();
      powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
      powbSynthesis.setActiveSince(new Date());
      powbSynthesis.setModifiedBy(this.getCurrentUser());
      relationsName.add(APConstants.SYNTHESIS_FINANCIAL_EXPENDITURE_RELATION);
      relationsName.add(APConstants.SYNTHESIS_FINANCIAL_PLANNED_BUDGET_RELATION);
      powbSynthesisManager.save(powbSynthesis, this.getActionName(), relationsName, this.getActualPhase());
      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists()) {
        path.toFile().delete();
      }
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
      return NOT_AUTHORIZED;
    }
  }

  private void saveNewFinancialExpenditure(PowbFinancialExpenditure powbFinancialExpenditure) {
    PowbFinancialExpenditure newPowbFinancialExpenditure = new PowbFinancialExpenditure();
    newPowbFinancialExpenditure.setActive(true);
    newPowbFinancialExpenditure.setCreatedBy(this.getCurrentUser());
    newPowbFinancialExpenditure.setModifiedBy(this.getCurrentUser());
    newPowbFinancialExpenditure.setActiveSince(new Date());
    newPowbFinancialExpenditure.setPowbSynthesis(powbSynthesis);
    newPowbFinancialExpenditure.setPowbExpenditureArea(powbFinancialExpenditure.getPowbExpenditureArea());
    if (powbFinancialExpenditure.getW1w2Percentage() != null) {
      newPowbFinancialExpenditure.setW1w2Percentage(powbFinancialExpenditure.getW1w2Percentage());
    } else {
      newPowbFinancialExpenditure.setW1w2Percentage(0.0);
    }
    newPowbFinancialExpenditure.setComments(powbFinancialExpenditure.getComments());

    newPowbFinancialExpenditure =
      powbFinancialExpenditureManager.savePowbFinancialExpenditure(newPowbFinancialExpenditure);
  }

  private void saveNewPlannedBudget(PowbFinancialPlannedBudget powbFinancialPlannedBudget) {
    PowbFinancialPlannedBudget newPowbFinancialPlannedBudget = new PowbFinancialPlannedBudget();
    newPowbFinancialPlannedBudget.setActive(true);
    newPowbFinancialPlannedBudget.setCreatedBy(this.getCurrentUser());
    newPowbFinancialPlannedBudget.setModifiedBy(this.getCurrentUser());
    newPowbFinancialPlannedBudget.setActiveSince(new Date());
    newPowbFinancialPlannedBudget.setPowbSynthesis(powbSynthesis);
    newPowbFinancialPlannedBudget.setPowbExpenditureArea(powbFinancialPlannedBudget.getPowbExpenditureArea());
    newPowbFinancialPlannedBudget.setLiaisonInstitution(powbFinancialPlannedBudget.getLiaisonInstitution());
    if (powbFinancialPlannedBudget.getW1w2() != null) {
      newPowbFinancialPlannedBudget.setW1w2(powbFinancialPlannedBudget.getW1w2());
    } else {
      newPowbFinancialPlannedBudget.setW1w2(0.0);
    }
    if (powbFinancialPlannedBudget.getW3Bilateral() != null) {
      newPowbFinancialPlannedBudget.setW3Bilateral(powbFinancialPlannedBudget.getW3Bilateral());
    } else {
      newPowbFinancialPlannedBudget.setW3Bilateral(0.0);
    }
    if (powbFinancialPlannedBudget.getCenterFunds() != null) {
      newPowbFinancialPlannedBudget.setCenterFunds(powbFinancialPlannedBudget.getCenterFunds());
    } else {
      newPowbFinancialPlannedBudget.setCenterFunds(0.0);
    }
    if (powbFinancialPlannedBudget.getCarry() != null) {
      newPowbFinancialPlannedBudget.setCarry(powbFinancialPlannedBudget.getCarry());
    } else {
      newPowbFinancialPlannedBudget.setCarry(0.0);
    }


    newPowbFinancialPlannedBudget.setComments(powbFinancialPlannedBudget.getComments());

    newPowbFinancialPlannedBudget =
      powbFinancialPlannedBudgetManager.savePowbFinancialPlannedBudget(newPowbFinancialPlannedBudget);
  }

  private void saveUpdateFinancialExpenditure(PowbFinancialExpenditure powbFinancialExpenditure) {
    PowbFinancialExpenditure newPowbFinancialExpenditure =
      powbFinancialExpenditureManager.getPowbFinancialExpenditureById(powbFinancialExpenditure.getId());
    newPowbFinancialExpenditure.setActive(true);
    newPowbFinancialExpenditure.setModifiedBy(this.getCurrentUser());
    newPowbFinancialExpenditure.setActiveSince(new Date());
    if (powbFinancialExpenditure.getW1w2Percentage() != null) {
      newPowbFinancialExpenditure.setW1w2Percentage(powbFinancialExpenditure.getW1w2Percentage());
    } else {
      newPowbFinancialExpenditure.setW1w2Percentage(0.0);
    }
    newPowbFinancialExpenditure.setComments(powbFinancialExpenditure.getComments());
    newPowbFinancialExpenditure =
      powbFinancialExpenditureManager.savePowbFinancialExpenditure(newPowbFinancialExpenditure);
  }

  private void saveUpdateFinancialPlan() {
    PowbFinancialPlan powbFinancialPlan = powbSynthesis.getFinancialPlan();
    if (powbFinancialPlan.getId() == null) {
      powbFinancialPlan.setId(powbSynthesisID);
    }
    powbFinancialPlan.setActiveSince(new Date());
    powbFinancialPlan.setModifiedBy(this.getCurrentUser());
    powbFinancialPlan.setFinancialPlanIssues(powbSynthesis.getFinancialPlan().getFinancialPlanIssues());
    powbFinancialPlan = powbFinancialPlanManager.savePowbFinancialPlan(powbFinancialPlan);
  }

  private void saveUpdatePlannedBudget(PowbFinancialPlannedBudget powbFinancialPlannedBudget) {
    PowbFinancialPlannedBudget powbFinancialPlannedBudgetDB =
      powbFinancialPlannedBudgetManager.getPowbFinancialPlannedBudgetById(powbFinancialPlannedBudget.getId());
    powbFinancialPlannedBudgetDB.setActive(true);
    powbFinancialPlannedBudgetDB.setModifiedBy(this.getCurrentUser());
    powbFinancialPlannedBudgetDB.setActiveSince(new Date());
    if (powbFinancialPlannedBudget.getW1w2() != null) {
      powbFinancialPlannedBudgetDB.setW1w2(powbFinancialPlannedBudget.getW1w2());
    } else {
      powbFinancialPlannedBudgetDB.setW1w2(0.0);
    }
    if (powbFinancialPlannedBudget.getW3Bilateral() != null) {
      powbFinancialPlannedBudgetDB.setW3Bilateral(powbFinancialPlannedBudget.getW3Bilateral());
    } else {
      powbFinancialPlannedBudgetDB.setW3Bilateral(0.0);
    }
    if (powbFinancialPlannedBudget.getCenterFunds() != null) {
      powbFinancialPlannedBudgetDB.setCenterFunds(powbFinancialPlannedBudget.getCenterFunds());
    } else {
      powbFinancialPlannedBudgetDB.setCenterFunds(0.0);
    }
    if (powbFinancialPlannedBudget.getCarry() != null) {
      powbFinancialPlannedBudgetDB.setCarry(powbFinancialPlannedBudget.getCarry());
    } else {
      powbFinancialPlannedBudgetDB.setCarry(0.0);
    }
    powbFinancialPlannedBudgetDB.setComments(powbFinancialPlannedBudget.getComments());
    powbFinancialPlannedBudgetDB =
      powbFinancialPlannedBudgetManager.savePowbFinancialPlannedBudget(powbFinancialPlannedBudgetDB);
  }

  public void setLiaisonInstitution(LiaisonInstitution liaisonInstitution) {
    this.liaisonInstitution = liaisonInstitution;
  }

  public void setLiaisonInstitutionID(Long liaisonInstitutionID) {
    this.liaisonInstitutionID = liaisonInstitutionID;
  }

  /**
   * Get Liaison institution ID Parameter
   */
  private void setLiaisonInstitutionIdParameter() {
    try {
      liaisonInstitutionID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.LIAISON_INSTITUTION_REQUEST_ID)));
    } catch (NumberFormatException e) {
      List<LiaisonInstitution> pmuList = loggedCrp.getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() == null && c.getAcronym().equals("PMU") && c.isActive())
        .collect(Collectors.toList());
      if (pmuList != null && !pmuList.isEmpty()) {
        liaisonInstitutionID = pmuList.get(0).getId();
      }
    }
  }

  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }


  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setPowbExpenditureAreas(List<PowbExpenditureAreas> powbExpenditureAreas) {
    this.powbExpenditureAreas = powbExpenditureAreas;
  }

  public void setPowbSynthesis(PowbSynthesis powbSynthesis) {
    this.powbSynthesis = powbSynthesis;
  }

  public void setPowbSynthesisID(Long powbSynthesisID) {
    this.powbSynthesisID = powbSynthesisID;
  }

  private void setPowbSynthesisIdHistory() {
    transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
    PowbSynthesis history = (PowbSynthesis) auditLogManager.getHistory(transaction);
    if (history != null) {
      powbSynthesis = history;
      powbSynthesisID = powbSynthesis.getId();
      liaisonInstitutionID = powbSynthesis.getLiaisonInstitution().getId();
      liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);
    } else {
      this.transaction = null;
      this.setTransaction("-1");
    }
  }

  private void setPowbSynthesisIdParameter() {
    List<LiaisonInstitution> pmuList = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.getAcronym().equals("PMU") && c.isActive())
      .collect(Collectors.toList());
    if (pmuList != null && !pmuList.isEmpty()) {
      Long liaisonInstitutionID = pmuList.get(0).getId();
      PowbSynthesis powbSynthesis =
        powbSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitutionID);
      if (powbSynthesis != null) {
        powbSynthesisID = powbSynthesis.getId();
      } else {
        powbSynthesis = this.createPowbSynthesis(this.getActualPhase().getId(), liaisonInstitutionID);
        powbSynthesisID = powbSynthesis.getId();
      }
    }
  }

  private void setPowbSynthesisParameters() {
    this.setLiaisonInstitutionIdParameter();
    this.setPowbSynthesisIdParameter();
    liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);
    powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, powbSynthesis, true);
    }
  }

}
