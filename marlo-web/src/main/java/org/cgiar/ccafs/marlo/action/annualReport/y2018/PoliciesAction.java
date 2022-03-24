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

package org.cgiar.ccafs.marlo.action.annualReport.y2018;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyRegionManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyInvestimentTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageProcessManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyRegion;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisPoliciesByOrganizationTypeDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisPoliciesByRepIndPolicyInvestimentTypeDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisPoliciesByRepIndStageProcessDTO;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.Policies2018Validator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
public class PoliciesAction extends BaseAction {

  private static final long serialVersionUID = -4977149795769459191L;

  // Managers
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private CrpProgramManager crpProgramManager;
  private UserManager userManager;
  private Policies2018Validator validator;
  private ProjectPolicyManager projectPolicyManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressPolicyManager reportSynthesisFlagshipProgressPolicyManager;
  private ReportSynthesisFlagshipProgressStudyManager reportSynthesisFlagshipProgressStudyManager;
  private ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager;
  private RepIndOrganizationTypeManager repIndOrganizationTypeManager;
  private RepIndStageProcessManager repIndStageProcessManager;
  private RepIndPolicyInvestimentTypeManager repIndInvestimentTypeManager;
  private SectionStatusManager sectionStatusManager;
  private ProjectPolicyCountryManager projectPolicyCountryManager;
  private ProjectPolicyRegionManager projectPolicyRegionManager;

  // Variables
  private String transaction;
  private ReportSynthesis reportSynthesis;
  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<ProjectPolicy> projectPolicies;
  private List<ReportSynthesisPoliciesByOrganizationTypeDTO> policiesByOrganizationTypeDTOs;
  private List<ReportSynthesisPoliciesByRepIndStageProcessDTO> policiesByRepIndStageProcessDTOs;
  private Integer total = 0;
  private List<ReportSynthesisPoliciesByRepIndPolicyInvestimentTypeDTO> policiesByRepIndInvestimentTypeDTOs;
  private boolean tableComplete;


  @Inject
  public PoliciesAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, Policies2018Validator validator,
    CrpProgramManager crpProgramManager, ProjectPolicyManager projectPolicyManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ReportSynthesisFlagshipProgressPolicyManager reportSynthesisFlagshipProgressPolicyManager,
    RepIndOrganizationTypeManager repIndOrganizationTypeManager, RepIndStageProcessManager repIndStageProcessManager,
    RepIndPolicyInvestimentTypeManager repIndInvestimentTypeManager, SectionStatusManager sectionStatusManager,
    ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager,
    ReportSynthesisFlagshipProgressStudyManager reportSynthesisFlagshipProgressStudyManager,
    ProjectPolicyCountryManager projectPolicyCountryManager, ProjectPolicyRegionManager projectPolicyRegionManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.validator = validator;
    this.crpProgramManager = crpProgramManager;
    this.projectPolicyManager = projectPolicyManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.reportSynthesisFlagshipProgressPolicyManager = reportSynthesisFlagshipProgressPolicyManager;
    this.repIndOrganizationTypeManager = repIndOrganizationTypeManager;
    this.repIndStageProcessManager = repIndStageProcessManager;
    this.repIndInvestimentTypeManager = repIndInvestimentTypeManager;
    this.sectionStatusManager = sectionStatusManager;
    this.projectExpectedStudyPolicyManager = projectExpectedStudyPolicyManager;
    this.reportSynthesisFlagshipProgressStudyManager = reportSynthesisFlagshipProgressStudyManager;
    this.projectPolicyCountryManager = projectPolicyCountryManager;
    this.projectPolicyRegionManager = projectPolicyRegionManager;
  }

  /**
   * @return true if the policy has a OICR on the AR document or if the policy has no OICR
   */
  public boolean canBeAddedToAR(long policyId, long phaseId) {
    boolean editable = false;

    if (policyId > 0) {
      List<Long> projectExpectedStudiesPolicyIds = projectExpectedStudyPolicyManager.findAll().stream()
        .filter(
          pesp -> pesp != null && pesp.getId() != null && pesp.getPhase() != null && pesp.getPhase().getId() != null
            && pesp.getPhase().getId().longValue() == phaseId && pesp.getProjectPolicy() != null
            && pesp.getProjectPolicy().getId() != null && pesp.getProjectPolicy().getId().longValue() == policyId
            && pesp.getProjectExpectedStudy() != null && pesp.getProjectExpectedStudy().getId() != null)
        .map(pesp -> pesp.getProjectExpectedStudy().getId()).collect(Collectors.toList());

      // does not have any expected studies linked, no problem.
      if (projectExpectedStudiesPolicyIds.isEmpty()) {
        editable = true;
        return editable;
      }

      List<Long> expectedStudiesExcludedFromAR = reportSynthesisFlagshipProgressStudyManager.findAll().stream()
        .filter(s -> s != null && s.isActive() && s.getReportSynthesisFlagshipProgress() != null
          && s.getReportSynthesisFlagshipProgress().getId() != null
          && s.getReportSynthesisFlagshipProgress().getId().equals(synthesisID) && s.getProjectExpectedStudy() != null
          && s.getProjectExpectedStudy().getId() != null
          && projectExpectedStudiesPolicyIds.contains(s.getProjectExpectedStudy().getId()))
        .map(s -> s.getProjectExpectedStudy().getId()).collect(Collectors.toList());

      // if all the expected studies linked to the policy are excluded from the AR Document, the policy should
      // NOT be allowed to be included on the AR Document.
      editable = projectExpectedStudiesPolicyIds.size() != expectedStudiesExcludedFromAR.size();
    }

    return editable;
  }

  /**
   * Ensures that all indicators to be reported are in its corresponding synthesis table
   */
  private void ensureAllIndicatorsOnSynthesis() {
    for (ProjectPolicy projectPolicy : this.projectPolicies) {
      if (projectPolicy != null && projectPolicy.getId() != null) {
        ReportSynthesisFlagshipProgressPolicy synthesisPolicy = this.reportSynthesisFlagshipProgressPolicyManager
          .getReportSynthesisFlagshipProgressPolicyByPolicyAndFlagshipProgress(projectPolicy.getId(),
            reportSynthesis.getReportSynthesisFlagshipProgress().getId());
        if (synthesisPolicy == null) {
          synthesisPolicy = new ReportSynthesisFlagshipProgressPolicy();
          // if isPMU = true, the indicators should be excluded by default. If isPMU = false, the indicators should be
          // included by default
          synthesisPolicy.setActive(this.isPMU());
          synthesisPolicy.setCreatedBy(this.getCurrentUser());
          synthesisPolicy.setProjectPolicy(projectPolicy);
          synthesisPolicy.setReportSynthesisFlagshipProgress(reportSynthesis.getReportSynthesisFlagshipProgress());

          synthesisPolicy = this.reportSynthesisFlagshipProgressPolicyManager
            .saveReportSynthesisFlagshipProgressPolicy(synthesisPolicy);

          if (!this.isPMU()) {
            // apparently the creation of deactivated entities is not supported or simply does not work, so we have to
            // manually "delete" them after creation.
            this.reportSynthesisFlagshipProgressPolicyManager
              .deleteReportSynthesisFlagshipProgressPolicy(synthesisPolicy.getId());
          }
        } else {
          if (!this.isPMU() && synthesisPolicy.getId() != null && synthesisPolicy.isActive()) {
            // if we are currently in a FP/Module, the entity should always be is_active=0 (included)
            this.reportSynthesisFlagshipProgressPolicyManager
              .deleteReportSynthesisFlagshipProgressPolicy(synthesisPolicy.getId());
          }
        }
      }
    }
  }

  /*
   * Fill Countries and Regions information to projectPolicies list
   */
  public void fillGeographicInformation() {
    if (projectPolicies != null && !projectPolicies.isEmpty()) {
      List<ProjectPolicyCountry> countries = new ArrayList<>();
      List<ProjectPolicyRegion> regions = new ArrayList<>();

      for (ProjectPolicy policy : projectPolicies) {

        if (policy.getId() != null) {

          // Fill Policy Countries
          if ((policy.getCountries() != null && policy.getCountries().isEmpty()) || policy.getCountries() == null) {
            if (projectPolicyCountryManager.getPolicyCountrybyPhase(policy.getId(),
              this.getActualPhase().getId()) != null) {
              countries =
                projectPolicyCountryManager.getPolicyCountrybyPhase(policy.getId(), this.getActualPhase().getId());
            }

            if (countries != null && !countries.isEmpty()) {
              policy.setCountries(countries);
            }
          }

          // Fill Policy Regions
          if ((policy.getRegions() != null && policy.getRegions().isEmpty()) || policy.getRegions() == null) {
            if (projectPolicyRegionManager.getPolicyRegionbyPhase(policy.getId(),
              this.getActualPhase().getId()) != null) {
              regions =
                projectPolicyRegionManager.getPolicyRegionbyPhase(policy.getId(), this.getActualPhase().getId());
            }

            if (regions != null && !regions.isEmpty()) {
              policy.setRegions(regions);
            }
          }
        }
      }
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


  private void
    flagshipProgressProjectPoliciesNewData(ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgressDB) {

    List<Long> selectedPs = new ArrayList<>();
    List<Long> selectedPoliciesIds = new ArrayList<>();

    for (ProjectPolicy projectPolicy : projectPolicies) {
      selectedPoliciesIds.add(projectPolicy.getId());
    }

    // Add policies (active =0)
    if (reportSynthesis.getReportSynthesisFlagshipProgress().getPoliciesValue() != null
      && reportSynthesis.getReportSynthesisFlagshipProgress().getPoliciesValue().length() > 0) {
      List<Long> stList = new ArrayList<>();
      for (String string : reportSynthesis.getReportSynthesisFlagshipProgress().getPoliciesValue().trim().split(",")) {
        stList.add(Long.parseLong(string.trim()));
      }

      for (Long studyId : selectedPoliciesIds) {
        int index = stList.indexOf(studyId);
        if (index < 0) {
          selectedPs.add(studyId);
        }
      }

      for (ReportSynthesisFlagshipProgressPolicy flagshipProgressPolicy : reportSynthesisFlagshipProgressDB
        .getReportSynthesisFlagshipProgressPolicies().stream().filter(rio -> rio.isActive())
        .collect(Collectors.toList())) {
        if (!selectedPs.contains(flagshipProgressPolicy.getProjectPolicy().getId())) {
          reportSynthesisFlagshipProgressPolicyManager
            .deleteReportSynthesisFlagshipProgressPolicy(flagshipProgressPolicy.getId());
        }
      }

      for (Long policyId : selectedPs) {
        ProjectPolicy projectPolicy = projectPolicyManager.getProjectPolicyById(policyId);

        ReportSynthesisFlagshipProgressPolicy flagshipProgressPolicyNew = new ReportSynthesisFlagshipProgressPolicy();

        flagshipProgressPolicyNew.setProjectPolicy(projectPolicy);
        flagshipProgressPolicyNew.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

        List<ReportSynthesisFlagshipProgressPolicy> flagshipProgressPolicies =
          reportSynthesisFlagshipProgressDB.getReportSynthesisFlagshipProgressPolicies().stream()
            .filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!flagshipProgressPolicies.contains(flagshipProgressPolicyNew)) {
          flagshipProgressPolicyNew = reportSynthesisFlagshipProgressPolicyManager
            .saveReportSynthesisFlagshipProgressPolicy(flagshipProgressPolicyNew);
        }

      }
    } else {

      // Delete Policies (Save with active=1)
      for (Long policyId : selectedPoliciesIds) {
        ProjectPolicy projectPolicy = projectPolicyManager.getProjectPolicyById(policyId);

        ReportSynthesisFlagshipProgressPolicy flagshipProgressPlannedPolicyNew =
          new ReportSynthesisFlagshipProgressPolicy();

        flagshipProgressPlannedPolicyNew.setProjectPolicy(projectPolicy);
        flagshipProgressPlannedPolicyNew.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

        List<ReportSynthesisFlagshipProgressPolicy> reportSynthesisFlagshipProgressPolicies =
          reportSynthesisFlagshipProgressDB.getReportSynthesisFlagshipProgressPolicies().stream()
            .filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!reportSynthesisFlagshipProgressPolicies.contains(flagshipProgressPlannedPolicyNew)) {
          flagshipProgressPlannedPolicyNew = reportSynthesisFlagshipProgressPolicyManager
            .saveReportSynthesisFlagshipProgressPolicy(flagshipProgressPlannedPolicyNew);
        }
      }
    }

  }


  private Path getAutoSaveFilePath() {
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = reportSynthesis.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
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


  public List<ReportSynthesisPoliciesByOrganizationTypeDTO> getPoliciesByOrganizationTypeDTOs() {
    return policiesByOrganizationTypeDTOs;
  }

  public List<ReportSynthesisPoliciesByRepIndPolicyInvestimentTypeDTO> getPoliciesByRepIndInvestimentTypeDTOs() {
    return policiesByRepIndInvestimentTypeDTOs;
  }

  public List<ReportSynthesisPoliciesByRepIndStageProcessDTO> getPoliciesByRepIndStageProcessDTOs() {
    return policiesByRepIndStageProcessDTOs;
  }

  public List<ProjectPolicy> getProjectPolicies() {
    return projectPolicies;
  }


  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }


  public Long getSynthesisID() {
    return synthesisID;
  }

  public Integer getTotal() {
    return total;
  }

  public String getTransaction() {
    return transaction;
  }

  public boolean isFlagship() {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() != null) {
        CrpProgram crpProgram =
          crpProgramManager.getCrpProgramById(liaisonInstitution.getCrpProgram().getId().longValue());
        if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
          isFP = true;
        }
      }
    }
    return isFP;
  }


  @Override
  public boolean isPMU() {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;

  }

  /**
   * This method get the status of an specific policy depending of the
   * sectionStatuses
   *
   * @param policyID is the policy ID to be identified.
   * @return Boolean object with the status of the policy
   */
  public Boolean isPolicyComplete(long policyID, long phaseID) {

    SectionStatus sectionStatus = this.sectionStatusManager.getSectionStatusByProjectPolicy(policyID, "Reporting",
      this.getActualPhase().getYear(), false, "policies");

    if (sectionStatus == null) {
      tableComplete = true;
      return true;
    }

    if (sectionStatus.getMissingFields().length() != 0) {
      tableComplete = false;
      return false;
    }

    tableComplete = true;
    return true;

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
    Phase phase = this.getActualPhase();
    tableComplete = false;

    // If there is a history version being loaded
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {
      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      ReportSynthesis history = (ReportSynthesis) auditLogManager.getHistory(transaction);
      if (history != null) {
        reportSynthesis = history;
        synthesisID = reportSynthesis.getId();
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }
    } else {
      // Get Liaison institution ID Parameter
      try {
        liaisonInstitutionID =
          Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.LIAISON_INSTITUTION_REQUEST_ID)));
      } catch (NumberFormatException e) {
        User user = userManager.getUser(this.getCurrentUser().getId());
        if (user.getLiasonsUsers() != null || !user.getLiasonsUsers().isEmpty()) {
          List<LiaisonUser> liaisonUsers = new ArrayList<>(user.getLiasonsUsers().stream()
            .filter(lu -> lu.isActive() && lu.getLiaisonInstitution().isActive()
              && lu.getLiaisonInstitution().getCrp().getId().equals(loggedCrp.getId())
              && lu.getLiaisonInstitution().getInstitution() == null)
            .collect(Collectors.toList()));
          if (!liaisonUsers.isEmpty()) {
            boolean isLeader = false;
            for (LiaisonUser liaisonUser : liaisonUsers) {
              LiaisonInstitution institution = liaisonUser.getLiaisonInstitution();
              if (institution.isActive()) {
                if (institution.getCrpProgram() != null) {
                  if (institution.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
                    liaisonInstitutionID = institution.getId();
                    isLeader = true;
                    break;
                  }
                } else {
                  if (institution.getAcronym() != null && institution.getAcronym().equals("PMU")) {
                    liaisonInstitutionID = institution.getId();
                    isLeader = true;
                    break;
                  }
                }
              }
            }
            if (!isLeader) {
              liaisonInstitutionID = this.firstFlagship();
            }
          } else {
            liaisonInstitutionID = this.firstFlagship();
          }
        } else {
          liaisonInstitutionID = this.firstFlagship();
        }
      }

      try {
        synthesisID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.REPORT_SYNTHESIS_ID)));
        reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);

        if (!reportSynthesis.getPhase().equals(phase)) {
          reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
          if (reportSynthesis == null) {
            reportSynthesis = this.createReportSynthesis(phase.getId(), liaisonInstitutionID);
          }
          synthesisID = reportSynthesis.getId();
        }
      } catch (Exception e) {
        reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
        if (reportSynthesis == null) {
          reportSynthesis = this.createReportSynthesis(phase.getId(), liaisonInstitutionID);
        }
        synthesisID = reportSynthesis.getId();

      }
    }

    if (reportSynthesis != null) {

      ReportSynthesis reportSynthesisDB = reportSynthesisManager.getReportSynthesisById(synthesisID);
      synthesisID = reportSynthesisDB.getId();
      liaisonInstitutionID = reportSynthesisDB.getLiaisonInstitution().getId();
      liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);

      projectPolicies = projectPolicyManager.getProjectPoliciesList(liaisonInstitution, phase);

      Path path = this.getAutoSaveFilePath();
      // Verify if there is a Draft file
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();
        AutoSaveReader autoSaveReader = new AutoSaveReader();
        reportSynthesis = (ReportSynthesis) autoSaveReader.readFromJson(jReader);
        synthesisID = reportSynthesis.getId();
        this.setDraft(true);
      } else {
        this.setDraft(false);
        // Check if ToC relation is null -create it
        if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
          ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();
          // create one to one relation
          reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
          flagshipProgress.setReportSynthesis(reportSynthesis);
          // save the changes
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }

        // if(!this.isPMU()) {
        if (this.isSelectedPhaseAR2021()) {
          this.ensureAllIndicatorsOnSynthesis();
        }
        // }

        /*
         * if (CollectionUtils.emptyIfNull(this.reportSynthesisFlagshipProgressPolicyManager.findAll()).stream()
         * .filter(p -> p != null && p.getId() != null && p.getProjectPolicy() != null
         * && p.getReportSynthesisFlagshipProgress() != null && p.getReportSynthesisFlagshipProgress().getId() != null
         * && p.getReportSynthesisFlagshipProgress().getId()
         * .equals(this.reportSynthesis.getReportSynthesisFlagshipProgress().getId()))
         * .count() == 0L) {
         * this.removeAllFromAR();
         * }
         */

        reportSynthesis.getReportSynthesisFlagshipProgress().setProjectPolicies(new ArrayList<>());
        if (reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressPolicies() != null
          && !reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressPolicies()
            .isEmpty()) {
          for (ReportSynthesisFlagshipProgressPolicy flagshipProgressPolicy : reportSynthesis
            .getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressPolicies().stream()
            .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
            reportSynthesis.getReportSynthesisFlagshipProgress().getProjectPolicies()
              .add(flagshipProgressPolicy.getProjectPolicy());
          }
        }
      }
    }


    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym() != null && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));


    /** Graphs and Tables */
    List<ProjectPolicy> selectedProjectPolicies = new ArrayList<ProjectPolicy>();
    policiesByOrganizationTypeDTOs = new ArrayList<ReportSynthesisPoliciesByOrganizationTypeDTO>();
    if (projectPolicies != null && !projectPolicies.isEmpty()) {
      projectPolicies.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
      selectedProjectPolicies.addAll(projectPolicies);
      // Remove unchecked policies
      if (reportSynthesis.getReportSynthesisFlagshipProgress().getProjectPolicies() != null
        && !reportSynthesis.getReportSynthesisFlagshipProgress().getProjectPolicies().isEmpty()) {
        for (ProjectPolicy projectPolicy : reportSynthesis.getReportSynthesisFlagshipProgress().getProjectPolicies()) {
          selectedProjectPolicies.remove(projectPolicy);
        }
      }
      total = selectedProjectPolicies.size();

      if (selectedProjectPolicies != null && !selectedProjectPolicies.isEmpty()) {
        // Chart: Policies by organization type
        policiesByOrganizationTypeDTOs =
          repIndOrganizationTypeManager.getPoliciesByOrganizationTypes(selectedProjectPolicies, phase);

        // Chart: Policies by stage process
        policiesByRepIndStageProcessDTOs =
          repIndStageProcessManager.getPoliciesByStageProcess(selectedProjectPolicies, phase);

        // Chat: Policies by investiment type
        policiesByRepIndInvestimentTypeDTOs =
          repIndInvestimentTypeManager.getPoliciesByInvestimentType(selectedProjectPolicies, phase);
      }

    }

    this.fillGeographicInformation();

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_FLAGSHIP_PROGRESS_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedPolicies() != null) {
        reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedPolicies().clear();
      }
    }

  }


  /*
   * private void removeAllFromAR() {
   * for (ProjectPolicy projectPolicy : this.projectPolicies) {
   * this.reportSynthesisFlagshipProgressPolicyManager.toAnnualReport(projectPolicy,
   * this.reportSynthesis.getReportSynthesisFlagshipProgress(), this.getCurrentUser(), true);
   * }
   * }
   */

  @Override
  public String save() {

    if (this.hasPermission("canEdit")) {

      // Dont save records (check marks in exclusion table) for Flagships
      if (this.isPMU()) {
        ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgressDB =
          reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisFlagshipProgress();

        this.flagshipProgressProjectPoliciesNewData(reportSynthesisFlagshipProgressDB);

        if (reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedPolicies() == null) {
          reportSynthesis.getReportSynthesisFlagshipProgress().setPlannedPolicies(new ArrayList<>());
        }

        reportSynthesisFlagshipProgressDB =
          reportSynthesisFlagshipProgressManager.saveReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

        List<String> relationsName = new ArrayList<>();
        reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);

        /**
         * The following is required because we need to update something on the @ReportSynthesis if we want a row
         * created
         * in the auditlog table.
         */
        this.setModificationJustification(reportSynthesis);

        reportSynthesisManager.save(reportSynthesis, this.getActionName(), relationsName, this.getActualPhase());

        Path path = this.getAutoSaveFilePath();
        if (path.toFile().exists()) {
          path.toFile().delete();
        }

        this.getActionMessages();
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
      }
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }

  }

  public void setLiaisonInstitution(LiaisonInstitution liaisonInstitution) {
    this.liaisonInstitution = liaisonInstitution;
  }

  public void setLiaisonInstitutionID(Long liaisonInstitutionID) {
    this.liaisonInstitutionID = liaisonInstitutionID;
  }


  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setPoliciesByOrganizationTypeDTOs(
    List<ReportSynthesisPoliciesByOrganizationTypeDTO> policiesByOrganizationTypeDTOs) {
    this.policiesByOrganizationTypeDTOs = policiesByOrganizationTypeDTOs;
  }


  public void setPoliciesByRepIndInvestimentTypeDTOs(
    List<ReportSynthesisPoliciesByRepIndPolicyInvestimentTypeDTO> policiesByRepIndInvestimentTypeDTOs) {
    this.policiesByRepIndInvestimentTypeDTOs = policiesByRepIndInvestimentTypeDTOs;
  }


  public void setPoliciesByRepIndStageProcessDTOs(
    List<ReportSynthesisPoliciesByRepIndStageProcessDTO> policiesByRepIndStageProcessDTOs) {
    this.policiesByRepIndStageProcessDTOs = policiesByRepIndStageProcessDTOs;
  }


  public void setProjectPolicies(List<ProjectPolicy> projectPolicies) {
    this.projectPolicies = projectPolicies;
  }


  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }


  public void setSynthesisID(Long synthesisID) {
    this.synthesisID = synthesisID;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }


  @Override
  public void validate() {
    if (this.isPMU()) {
      if (save) {
        validator.validateCheckButton(this, reportSynthesis, true);
      }
    } else {
      if (save) {
        validator.validate(this, reportSynthesis, true);
      }
    }
  }
}