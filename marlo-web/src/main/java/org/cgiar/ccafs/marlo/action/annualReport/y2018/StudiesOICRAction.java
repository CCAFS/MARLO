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
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressInnovationManager;
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
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisStudiesByCrpProgramDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisStudiesByRepIndStageStudyDTO;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.StudiesOICR2018Validator;

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
public class StudiesOICRAction extends BaseAction {

  private static final long serialVersionUID = 8323800211228698584L;

  // Managers
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private CrpProgramManager crpProgramManager;
  private UserManager userManager;
  private StudiesOICR2018Validator validator;
  private ProjectPolicyManager projectPolicyManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressStudyManager reportSynthesisFlagshipProgressStudyManager;
  private ReportSynthesisFlagshipProgressInnovationManager reportSynthesisFlagshipProgressInnovationManager;
  private ReportSynthesisFlagshipProgressPolicyManager reportSynthesisFlagshipProgressPolicyManager;
  private SectionStatusManager sectionStatusManager;
  private ProjectInnovationManager projectInnovationManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager;
  private ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager;
  private RepIndStageStudyManager repIndStageStudyManager;

  // Variables
  private String transaction;
  private ReportSynthesis reportSynthesis;
  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<ProjectExpectedStudy> projectExpectedStudies;
  private Phase actualPhase;
  private boolean tableComplete;
  private String flagshipsIncomplete;

  // Graph variables
  private Integer total = 0;
  private List<ReportSynthesisStudiesByRepIndStageStudyDTO> reportSynthesisStudiesByRepIndStageStudyDTOs;
  private List<ReportSynthesisStudiesByCrpProgramDTO> reportSynthesisStudiesByCrpProgramDTOs;


  @Inject
  public StudiesOICRAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, StudiesOICR2018Validator validator,
    CrpProgramManager crpProgramManager, ProjectExpectedStudyManager projectExpectedStudyManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ReportSynthesisFlagshipProgressStudyManager reportSynthesisFlagshipProgressStudyManager,
    ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager,
    ProjectPolicyManager projectPolicyManager, ProjectInnovationManager projectInnovationManager,
    SectionStatusManager sectionStatusManager,
    ReportSynthesisFlagshipProgressInnovationManager reportSynthesisFlagshipProgressInnovationManager,
    ReportSynthesisFlagshipProgressPolicyManager reportSynthesisFlagshipProgressPolicyManager,
    ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager,
    RepIndStageStudyManager repIndStageStudyManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.validator = validator;
    this.crpProgramManager = crpProgramManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.reportSynthesisFlagshipProgressStudyManager = reportSynthesisFlagshipProgressStudyManager;
    this.sectionStatusManager = sectionStatusManager;
    this.projectExpectedStudyInnovationManager = projectExpectedStudyInnovationManager;
    this.projectInnovationManager = projectInnovationManager;
    this.reportSynthesisFlagshipProgressInnovationManager = reportSynthesisFlagshipProgressInnovationManager;
    this.reportSynthesisFlagshipProgressPolicyManager = reportSynthesisFlagshipProgressPolicyManager;
    this.projectPolicyManager = projectPolicyManager;
    this.projectExpectedStudyPolicyManager = projectExpectedStudyPolicyManager;
    this.repIndStageStudyManager = repIndStageStudyManager;
  }


  public boolean canBeRemovedFromAR(long studyID, long phaseID) {
    boolean canBeRemoved = false;
    /*
     * Process: get all innovations and policies linked to the study using the methods on this class, then find all the
     * rows on report_synthesis_flagship_progress_innovations and report_synthesis_flagship_progress_policies. If the
     * row is active (is_active = 1) the innovation or policy has been excluded from the report. If not, it is included.
     * Not all the policies and innovations will have a row on the report synthesis table, but is ok.
     */
    if (studyID > 0) {
      List<ProjectInnovation> innovations = this.getInnovations(studyID, phaseID);
      List<ProjectPolicy> policies = this.getPolicies(studyID, phaseID);
      // TODO to use new methods created: isInnovationIncludedInReport and isPolicyIncludedInReport
      List<ReportSynthesisFlagshipProgressInnovation> synthesisInnovations =
        reportSynthesisFlagshipProgressInnovationManager.findAll().stream()
          .filter(i -> i.getReportSynthesisFlagshipProgress() != null
            && i.getReportSynthesisFlagshipProgress().getReportSynthesis() != null
            && i.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase() != null
            && i.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase().getId() != null
            && i.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase().getId().longValue() == phaseID
            && i.getReportSynthesisFlagshipProgress().getReportSynthesis().getLiaisonInstitution() != null
            && i.getReportSynthesisFlagshipProgress().getReportSynthesis().getLiaisonInstitution().getId() != null
            && i.getReportSynthesisFlagshipProgress().getReportSynthesis().getLiaisonInstitution().getId()
              .equals(this.getLiaisonInstitutionID()))
          .collect(Collectors.toList());

      List<ReportSynthesisFlagshipProgressPolicy> synthesisPolicies =
        reportSynthesisFlagshipProgressPolicyManager.findAll().stream()
          .filter(p -> p.getReportSynthesisFlagshipProgress() != null
            && p.getReportSynthesisFlagshipProgress().getReportSynthesis() != null
            && p.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase() != null
            && p.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase().getId() != null
            && p.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase().getId().longValue() == phaseID
            && p.getReportSynthesisFlagshipProgress().getReportSynthesis().getLiaisonInstitution() != null
            && p.getReportSynthesisFlagshipProgress().getReportSynthesis().getLiaisonInstitution().getId() != null
            && p.getReportSynthesisFlagshipProgress().getReportSynthesis().getLiaisonInstitution().getId()
              .equals(this.getLiaisonInstitutionID()))
          .collect(Collectors.toList());

      innovations.removeIf(i -> i == null || i.getId() == null
        || (synthesisInnovations
          .stream().filter(si -> si.isActive() && si.getProjectInnovation() != null
            && si.getProjectInnovation().getId() != null && si.getProjectInnovation().getId().equals(i.getId()))
          .count() > 0));

      policies
        .removeIf(p -> p == null || p.getId() == null
          || (synthesisPolicies
            .stream().filter(sp -> sp.isActive() && sp.getProjectPolicy() != null
              && sp.getProjectPolicy().getId() != null && sp.getProjectPolicy().getId().equals(p.getId()))
            .count() > 0));

      canBeRemoved = innovations.isEmpty() && policies.isEmpty();
    }

    return canBeRemoved;
  }

  /**
   * Ensures that all indicators to be reported are in its corresponding synthesis table
   */
  private void ensureAllIndicatorsOnSynthesis() {
    for (ProjectExpectedStudy projectStudy : this.projectExpectedStudies) {
      if (projectStudy != null && projectStudy.getId() != null) {
        ReportSynthesisFlagshipProgressStudy synthesisStudy = this.reportSynthesisFlagshipProgressStudyManager
          .getReportSynthesisFlagshipProgressStudyByStudyAndFlagshipProgress(projectStudy.getId(),
            reportSynthesis.getReportSynthesisFlagshipProgress().getId());
        if (synthesisStudy == null) {
          synthesisStudy = new ReportSynthesisFlagshipProgressStudy();
          // if isPMU = true, the indicators should be excluded by default. If isPMU = false, the indicators should be
          // included by default
          synthesisStudy.setActive(this.isPMU());
          synthesisStudy.setCreatedBy(this.getCurrentUser());
          synthesisStudy.setProjectExpectedStudy(projectStudy);
          synthesisStudy.setReportSynthesisFlagshipProgress(reportSynthesis.getReportSynthesisFlagshipProgress());

          synthesisStudy =
            this.reportSynthesisFlagshipProgressStudyManager.saveReportSynthesisFlagshipProgressStudy(synthesisStudy);

          if (!this.isPMU()) {
            // apparently the creation of deactivated entities is not supported or simply does not work, so we have to
            // manually "delete" them after creation.
            this.reportSynthesisFlagshipProgressStudyManager
              .deleteReportSynthesisFlagshipProgressStudy(synthesisStudy.getId());
          }
        } else {
          if (!this.isPMU() && synthesisStudy.getId() != null && synthesisStudy.isActive()) {
            // if we are currently in a FP/Module, the entity should always be is_active=0 (included)
            this.reportSynthesisFlagshipProgressStudyManager
              .deleteReportSynthesisFlagshipProgressStudy(synthesisStudy.getId());
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
    flagshipProgressProjectStudiesNewData(ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgressDB) {

    List<Long> selectedPs = new ArrayList<>();
    List<Long> selectedStudiesIds = new ArrayList<>();

    for (ProjectExpectedStudy projectExpectedStudy : projectExpectedStudies) {
      selectedStudiesIds.add(projectExpectedStudy.getId());
    }

    // Add studies (active =0)
    if (reportSynthesis.getReportSynthesisFlagshipProgress().getStudiesValue() != null
      && reportSynthesis.getReportSynthesisFlagshipProgress().getStudiesValue().length() > 0) {
      List<Long> stList = new ArrayList<>();
      for (String string : reportSynthesis.getReportSynthesisFlagshipProgress().getStudiesValue().trim().split(",")) {
        stList.add(Long.parseLong(string.trim()));
      }

      for (Long studyId : selectedStudiesIds) {
        int index = stList.indexOf(studyId);
        if (index < 0) {
          selectedPs.add(studyId);
        }
      }

      for (ReportSynthesisFlagshipProgressStudy flagshipProgressStudy : reportSynthesisFlagshipProgressDB
        .getReportSynthesisFlagshipProgressStudies().stream().filter(rio -> rio.isActive())
        .collect(Collectors.toList())) {
        if (!selectedPs.contains(flagshipProgressStudy.getProjectExpectedStudy().getId())) {
          reportSynthesisFlagshipProgressStudyManager
            .deleteReportSynthesisFlagshipProgressStudy(flagshipProgressStudy.getId());
        }
      }

      for (Long studyId : selectedPs) {
        ProjectExpectedStudy projectExpectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(studyId);

        ReportSynthesisFlagshipProgressStudy flagshipProgressStudyNew = new ReportSynthesisFlagshipProgressStudy();

        flagshipProgressStudyNew.setProjectExpectedStudy(projectExpectedStudy);
        flagshipProgressStudyNew.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

        List<ReportSynthesisFlagshipProgressStudy> flagshipProgressStudies =
          reportSynthesisFlagshipProgressDB.getReportSynthesisFlagshipProgressStudies().stream()
            .filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!flagshipProgressStudies.contains(flagshipProgressStudyNew)) {
          flagshipProgressStudyNew = reportSynthesisFlagshipProgressStudyManager
            .saveReportSynthesisFlagshipProgressStudy(flagshipProgressStudyNew);
          // this.updateLinkedInnovations(projectExpectedStudy.getId(), reportSynthesisFlagshipProgressDB);
          // this.updateLinkedPolicies(projectExpectedStudy.getId(), reportSynthesisFlagshipProgressDB);
        }

      }
    } else {

      // Delete Studies (Save with active=1)
      for (Long studyId : selectedStudiesIds) {
        ProjectExpectedStudy projectExpectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(studyId);

        ReportSynthesisFlagshipProgressStudy flagshipProgressPlannedStudyNew =
          new ReportSynthesisFlagshipProgressStudy();

        flagshipProgressPlannedStudyNew.setProjectExpectedStudy(projectExpectedStudy);
        flagshipProgressPlannedStudyNew.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

        List<ReportSynthesisFlagshipProgressStudy> reportSynthesisFlagshipProgressStudies =
          reportSynthesisFlagshipProgressDB.getReportSynthesisFlagshipProgressStudies().stream()
            .filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!reportSynthesisFlagshipProgressStudies.contains(flagshipProgressPlannedStudyNew)) {
          flagshipProgressPlannedStudyNew = reportSynthesisFlagshipProgressStudyManager
            .saveReportSynthesisFlagshipProgressStudy(flagshipProgressPlannedStudyNew);
          // this.updateLinkedInnovations(projectExpectedStudy.getId(), reportSynthesisFlagshipProgressDB);
          // this.updateLinkedPolicies(projectExpectedStudy.getId(), reportSynthesisFlagshipProgressDB);
        }
      }
    }

  }

  private Path getAutoSaveFilePath() {
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = reportSynthesis.getId() + "_" + composedClassName + "_" + actualPhase.getName() + "_"
      + actualPhase.getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public void getFlagshipsWithMissingFields() {
    SectionStatus sectionStatus = this.sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
      "Reporting", this.getActualPhase().getYear(), false, "synthesis.AR2019Table3");

    if (sectionStatus != null && sectionStatus.getMissingFields() != null && !sectionStatus.getMissingFields().isEmpty()
      && sectionStatus.getMissingFields().length() != 0 && sectionStatus.getSynthesisFlagships() != null
      && !sectionStatus.getSynthesisFlagships().isEmpty()) {
      System.out.println("flagships with missing fields" + sectionStatus.getSynthesisFlagships());
      flagshipsIncomplete = sectionStatus.getSynthesisFlagships();
    }

  }


  public List<ProjectInnovation> getInnovations(long studyID, long phaseID) {
    List<ProjectInnovation> projectInnovationList = new ArrayList<>();

    if (studyID > 0) {
      List<ProjectExpectedStudyInnovation> studyInnovations = new ArrayList<>();
      Phase current = this.getActualPhase();

      studyInnovations = projectExpectedStudyInnovationManager.findAll().stream()
        .filter(i -> i != null && i.getProjectExpectedStudy() != null && studyID != 0
          && i.getProjectExpectedStudy().getId() != null && i.getProjectExpectedStudy().getId().longValue() == studyID
          && i.getPhase() != null && i.getPhase().getId() != null && phaseID != 0
          && i.getPhase().getId().longValue() == phaseID && i.getProjectInnovation() != null
          && i.getProjectInnovation().getId() != null
          && i.getProjectInnovation().getProjectInnovationInfo(current) != null
          && i.getProjectInnovation().getProjectInnovationInfo().getYear() != null
          && i.getProjectInnovation().getProjectInnovationInfo().getYear().longValue() == current.getYear())
        .collect(Collectors.toList());

      if (studyInnovations != null && !studyInnovations.isEmpty()) {
        for (ProjectExpectedStudyInnovation studyInnovation : studyInnovations) {
          if (studyInnovation != null && studyInnovation.getProjectInnovation() != null
            && studyInnovation.getProjectInnovation().getId() != null) {
            ProjectInnovation innovation =
              projectInnovationManager.getProjectInnovationById(studyInnovation.getProjectInnovation().getId());

            if (innovation != null && innovation.isActive()) {
              innovation.getProjectInnovationInfo(this.getActualPhase());
              projectInnovationList.add(innovation);
            }
          }
        }
      }
    }

    return projectInnovationList;
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


  public List<ProjectPolicy> getPolicies(long studyID, long phaseID) {
    List<ProjectPolicy> policyList = new ArrayList<>();

    if (studyID > 0) {
      ProjectExpectedStudy expectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(studyID);
      Phase current = this.getActualPhase();

      if (expectedStudy != null) {
        List<ProjectExpectedStudyPolicy> studyPolicies = projectExpectedStudyPolicyManager.findAll().stream()
          .filter(p -> p != null && p.getProjectExpectedStudy() != null && studyID != 0
            && p.getProjectExpectedStudy().getId() != null && p.getProjectExpectedStudy().getId().longValue() == studyID
            && p.getPhase() != null && p.getPhase().getId() != null && phaseID != 0
            && p.getPhase().getId().longValue() == phaseID && p.getProjectPolicy() != null
            && p.getProjectPolicy().getId() != null && p.getProjectPolicy().getProjectPolicyInfo(current) != null
            && p.getProjectPolicy().getProjectPolicyInfo().getYear() != null
            && p.getProjectPolicy().getProjectPolicyInfo().getYear().longValue() == current.getYear())
          .collect(Collectors.toList());
        /*
         * expectedStudy.getProjectExpectedStudyPolicies().stream()
         * .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(this.getActualPhase().getId()))
         * .collect(Collectors.toList());
         */

        if (studyPolicies != null && !studyPolicies.isEmpty()) {
          for (ProjectExpectedStudyPolicy studyPolicy : studyPolicies) {
            if (studyPolicy != null && studyPolicy.getProjectPolicy() != null
              && studyPolicy.getProjectPolicy().getId() != null) {
              ProjectPolicy policy = projectPolicyManager.getProjectPolicyById(studyPolicy.getProjectPolicy().getId());

              if (policy != null && policy.isActive()) {
                policy.getProjectPolicyInfo(this.getActualPhase());
                policyList.add(studyPolicy.getProjectPolicy());
              }
            }
          }
        }
      }
    }

    return policyList;
  }

  public List<ProjectExpectedStudy> getProjectExpectedStudies() {
    return projectExpectedStudies;
  }

  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }


  public List<ReportSynthesisStudiesByCrpProgramDTO> getReportSynthesisStudiesByCrpProgramDTOs() {
    return reportSynthesisStudiesByCrpProgramDTOs;
  }


  public List<ReportSynthesisStudiesByRepIndStageStudyDTO> getReportSynthesisStudiesByRepIndStageStudyDTOs() {
    return reportSynthesisStudiesByRepIndStageStudyDTOs;
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

  public boolean isInnovationIncludedInReport(long innovationID, long phaseID) {
    // boolean included = false;
    List<ReportSynthesisFlagshipProgressInnovation> synthesisInnovations =
      reportSynthesisFlagshipProgressInnovationManager.findAll().stream()
        .filter(i -> i.getReportSynthesisFlagshipProgress() != null
          && i.getReportSynthesisFlagshipProgress().getReportSynthesis() != null
          && i.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase() != null
          && i.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase().getId() != null
          && i.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase().getId().longValue() == phaseID
          && i.getReportSynthesisFlagshipProgress().getReportSynthesis().getLiaisonInstitution() != null
          && i.getReportSynthesisFlagshipProgress().getReportSynthesis().getLiaisonInstitution().getId() != null
          && i.getReportSynthesisFlagshipProgress().getReportSynthesis().getLiaisonInstitution().getId()
            .equals(this.getLiaisonInstitutionID()))
        .collect(Collectors.toList());

    long matchingInnovations = synthesisInnovations
      .stream().filter(si -> si.isActive() && si.getProjectInnovation() != null
        && si.getProjectInnovation().getId() != null && si.getProjectInnovation().getId().longValue() == innovationID)
      .count();

    return matchingInnovations == 0;
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


  public boolean isPolicyIncludedInReport(long policyID, long phaseID) {
    // boolean included = false;
    List<ReportSynthesisFlagshipProgressPolicy> synthesisPolicies =
      reportSynthesisFlagshipProgressPolicyManager.findAll().stream()
        .filter(p -> p.getReportSynthesisFlagshipProgress() != null
          && p.getReportSynthesisFlagshipProgress().getReportSynthesis() != null
          && p.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase() != null
          && p.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase().getId() != null
          && p.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase().getId().longValue() == phaseID
          && p.getReportSynthesisFlagshipProgress().getReportSynthesis().getLiaisonInstitution() != null
          && p.getReportSynthesisFlagshipProgress().getReportSynthesis().getLiaisonInstitution().getId() != null
          && p.getReportSynthesisFlagshipProgress().getReportSynthesis().getLiaisonInstitution().getId()
            .equals(this.getLiaisonInstitutionID()))
        .collect(Collectors.toList());

    long matchingPolicies = synthesisPolicies.stream().filter(sp -> sp.isActive() && sp.getProjectPolicy() != null
      && sp.getProjectPolicy().getId() != null && sp.getProjectPolicy().getId().longValue() == policyID).count();

    return matchingPolicies == 0;
  }


  /**
   * This method get the status of an specific study depending of the
   * sectionStatuses
   *
   * @param studyID is the study ID to be identified.
   * @return Boolean object with the status of the study
   */
  public Boolean isStudyComplete(long studyID, long phaseID) {

    SectionStatus sectionStatus = this.sectionStatusManager.getSectionStatusByProjectExpectedStudy(studyID, "Reporting",
      this.getActualPhase().getYear(), false, "studies");

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

    this.actualPhase = this.getActualPhase();

    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
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

        if (!reportSynthesis.getPhase().equals(actualPhase)) {
          reportSynthesis = reportSynthesisManager.findSynthesis(actualPhase.getId(), liaisonInstitutionID);
          if (reportSynthesis == null) {
            reportSynthesis = this.createReportSynthesis(actualPhase.getId(), liaisonInstitutionID);
          }
          synthesisID = reportSynthesis.getId();
        }
      } catch (Exception e) {
        reportSynthesis = reportSynthesisManager.findSynthesis(actualPhase.getId(), liaisonInstitutionID);
        if (reportSynthesis == null) {
          reportSynthesis = this.createReportSynthesis(actualPhase.getId(), liaisonInstitutionID);
        }
        synthesisID = reportSynthesis.getId();

      }
    }

    if (reportSynthesis != null) {

      ReportSynthesis reportSynthesisDB = reportSynthesisManager.getReportSynthesisById(synthesisID);
      synthesisID = reportSynthesisDB.getId();
      liaisonInstitutionID = reportSynthesisDB.getLiaisonInstitution().getId();
      liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);

      projectExpectedStudies =
        projectExpectedStudyManager.getProjectStudiesList(liaisonInstitution, this.getActualPhase());

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

        this.getFlagshipsWithMissingFields();

        // if(!this.isPMU()) {
        if (!this.isHttpPost()) {
          this.ensureAllIndicatorsOnSynthesis();
        }
        // }

        /*
         * if (CollectionUtils.emptyIfNull(this.reportSynthesisFlagshipProgressStudyManager.findAll()).stream()
         * .filter(p -> p != null && p.getId() != null && p.getProjectExpectedStudy() != null
         * && p.getReportSynthesisFlagshipProgress() != null && p.getReportSynthesisFlagshipProgress().getId() != null
         * && p.getReportSynthesisFlagshipProgress().getId()
         * .equals(this.reportSynthesis.getReportSynthesisFlagshipProgress().getId()))
         * .count() == 0L) {
         * this.removeAllFromAR();
         * }
         */

        reportSynthesis.getReportSynthesisFlagshipProgress().setProjectStudies(new ArrayList<>());
        if (reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressStudies() != null
          && !reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressStudies()
            .isEmpty()) {
          for (ReportSynthesisFlagshipProgressStudy flagshipProgressStudy : reportSynthesis
            .getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressStudies().stream()
            .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
            reportSynthesis.getReportSynthesisFlagshipProgress().getProjectStudies()
              .add(flagshipProgressStudy.getProjectExpectedStudy());
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
    List<ProjectExpectedStudy> selectedStudies = new ArrayList<>();
    if (projectExpectedStudies != null && !projectExpectedStudies.isEmpty()) {
      projectExpectedStudies.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
      selectedStudies.addAll(projectExpectedStudies);
      // Remove unchecked studies
      if (reportSynthesis.getReportSynthesisFlagshipProgress().getProjectStudies() != null
        && !reportSynthesis.getReportSynthesisFlagshipProgress().getProjectStudies().isEmpty()) {
        for (ProjectExpectedStudy study : reportSynthesis.getReportSynthesisFlagshipProgress().getProjectStudies()) {
          selectedStudies.remove(study);
        }
      }
      total = selectedStudies.size();

      if (selectedStudies != null && !selectedStudies.isEmpty()) {
        // Chart: Studies by CRP Program
        reportSynthesisStudiesByCrpProgramDTOs =
          projectExpectedStudyManager.getProjectStudiesListByFP(liaisonInstitutions, selectedStudies, actualPhase);

        // Chart: Studies by stage process
        reportSynthesisStudiesByRepIndStageStudyDTOs =
          repIndStageStudyManager.getStudiesByStageStudy(selectedStudies, actualPhase);

        // Chat: Policies by investiment type
        /*
         * policiesByRepIndInvestimentTypeDTOs =
         * repIndInvestimentTypeManager.getPoliciesByInvestimentType(selectedProjectPolicies, phase);
         */
      }
    }

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_FLAGSHIP_PROGRESS_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedStudies() != null) {
        reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedStudies().clear();
      }
    }

  }

  /*
   * private void removeAllFromAR() {
   * for (ProjectExpectedStudy study : this.projectExpectedStudies) {
   * this.reportSynthesisFlagshipProgressStudyManager.toAnnualReport(study,
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

        this.flagshipProgressProjectStudiesNewData(reportSynthesisFlagshipProgressDB);

        if (reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedStudies() == null) {
          reportSynthesis.getReportSynthesisFlagshipProgress().setPlannedStudies(new ArrayList<>());
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

        reportSynthesisManager.save(reportSynthesis, this.getActionName(), relationsName, actualPhase);

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

  public void setProjectExpectedStudies(List<ProjectExpectedStudy> projectExpectedStudies) {
    this.projectExpectedStudies = projectExpectedStudies;
  }


  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }

  public void setReportSynthesisStudiesByCrpProgramDTOs(
    List<ReportSynthesisStudiesByCrpProgramDTO> reportSynthesisStudiesByCrpProgramDTOs) {
    this.reportSynthesisStudiesByCrpProgramDTOs = reportSynthesisStudiesByCrpProgramDTOs;
  }


  public void setReportSynthesisStudiesByRepIndStageStudyDTOs(
    List<ReportSynthesisStudiesByRepIndStageStudyDTO> reportSynthesisStudiesByRepIndStageStudyDTOs) {
    this.reportSynthesisStudiesByRepIndStageStudyDTOs = reportSynthesisStudiesByRepIndStageStudyDTOs;
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

  @SuppressWarnings("unused")
  private void updateLinkedInnovations(long studyId, ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress) {
    List<ProjectInnovation> linkedInnovations = projectExpectedStudyInnovationManager.findAll().stream()
      .filter(pesi -> pesi != null && pesi.getId() != null && pesi.getPhase() != null && pesi.getPhase().getId() != null
        && pesi.getPhase().getId().equals(this.getPhaseID()) && pesi.getProjectExpectedStudy() != null
        && pesi.getProjectExpectedStudy().getId() != null && pesi.getProjectExpectedStudy().getId() == studyId
        && pesi.getProjectInnovation() != null && pesi.getProjectInnovation().getId() != null)
      .map(pesi -> pesi.getProjectInnovation()).collect(Collectors.toList());

    List<ReportSynthesisFlagshipProgressInnovation> phaseFlagshipInnovations =
      reportSynthesisFlagshipProgressInnovationManager.findAll().stream()
        .filter(fpi -> fpi != null && fpi.getId() != null && fpi.getProjectInnovation() != null
          && fpi.getProjectInnovation().getId() != null && fpi.getReportSynthesisFlagshipProgress() != null
          && fpi.getReportSynthesisFlagshipProgress().getId() != null
          && fpi.getReportSynthesisFlagshipProgress().getId().equals(synthesisID))
        .collect(Collectors.toList());

    for (ProjectInnovation innovation : linkedInnovations) {
      List<Long> projectExpectedStudiesInnovationIds = projectExpectedStudyInnovationManager.findAll().stream()
        .filter(pesi -> pesi != null && pesi.getId() != null && pesi.getPhase() != null
          && pesi.getPhase().getId() != null && pesi.getPhase().getId().longValue() == this.getPhaseID()
          && pesi.getProjectInnovation() != null && pesi.getProjectInnovation().getId() != null
          && pesi.getProjectInnovation().getId().longValue() == innovation.getId()
          && pesi.getProjectExpectedStudy() != null && pesi.getProjectExpectedStudy().getId() != null)
        .map(pesi -> pesi.getProjectExpectedStudy().getId()).collect(Collectors.toList());

      List<Boolean> flagshipProgressStudiesActiveStatus = reportSynthesisFlagshipProgressStudyManager.findAll().stream()
        .filter(fps -> fps != null && fps.getId() != null && fps.getReportSynthesisFlagshipProgress() != null
          && fps.getReportSynthesisFlagshipProgress().getId() != null
          && fps.getReportSynthesisFlagshipProgress().getId().equals(synthesisID)
          && fps.getProjectExpectedStudy() != null && fps.getProjectExpectedStudy().getId() != null
          && projectExpectedStudiesInnovationIds.contains(fps.getProjectExpectedStudy().getId()))
        .map(fps -> fps.isActive()).collect(Collectors.toList());

      if (flagshipProgressStudiesActiveStatus.size() == projectExpectedStudiesInnovationIds.size()
        // all the studies are excluded from the AR report (isActive = true/1)
        && flagshipProgressStudiesActiveStatus.stream().reduce(true, Boolean::logicalAnd)) {
        ReportSynthesisFlagshipProgressInnovation flagshipInnovation =
          phaseFlagshipInnovations.stream().filter(fpi -> fpi.getProjectInnovation().getId().equals(innovation.getId()))
            .sorted((fpiOne, fpiTwo) -> Long.compare(fpiOne.getId(), fpiTwo.getId())).findFirst().orElse(null);
        if (flagshipInnovation == null) {
          // not found, we have to create one
          flagshipInnovation = new ReportSynthesisFlagshipProgressInnovation();
          flagshipInnovation.setProjectInnovation(innovation);
          flagshipInnovation.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
        }

        // the study was excluded from the AR report, we need to exclude ALL the innovations linked to it too
        flagshipInnovation.setActive(true);
        reportSynthesisFlagshipProgressInnovationManager
          .saveReportSynthesisFlagshipProgressInnovation(flagshipInnovation);
      }
    }

  }


  @SuppressWarnings("unused")
  private void updateLinkedPolicies(long studyId, ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress) {
    List<ProjectPolicy> linkedPolicies = projectExpectedStudyPolicyManager.findAll().stream()
      .filter(pesp -> pesp != null && pesp.getId() != null && pesp.getPhase() != null && pesp.getPhase().getId() != null
        && pesp.getPhase().getId().equals(this.getPhaseID()) && pesp.getProjectExpectedStudy() != null
        && pesp.getProjectExpectedStudy().getId() != null && pesp.getProjectExpectedStudy().getId() == studyId
        && pesp.getProjectPolicy() != null && pesp.getProjectPolicy().getId() != null)
      .map(pesp -> pesp.getProjectPolicy()).collect(Collectors.toList());

    List<ReportSynthesisFlagshipProgressPolicy> phaseFlagshipPolicies =
      reportSynthesisFlagshipProgressPolicyManager.findAll().stream()
        .filter(fpp -> fpp != null && fpp.getId() != null && fpp.getProjectPolicy() != null
          && fpp.getProjectPolicy().getId() != null && fpp.getReportSynthesisFlagshipProgress() != null
          && fpp.getReportSynthesisFlagshipProgress().getId() != null
          && fpp.getReportSynthesisFlagshipProgress().getId().equals(synthesisID))
        .collect(Collectors.toList());

    for (ProjectPolicy policy : linkedPolicies) {
      List<Long> projectExpectedStudiesPolicyIds =
        projectExpectedStudyPolicyManager.findAll().stream()
          .filter(pesp -> pesp != null && pesp.getId() != null && pesp.getPhase() != null
            && pesp.getPhase().getId() != null && pesp.getPhase().getId().longValue() == this.getPhaseID()
            && pesp.getProjectPolicy() != null && pesp.getProjectPolicy().getId() != null
            && pesp.getProjectPolicy().getId().longValue() == policy.getId() && pesp.getProjectExpectedStudy() != null
            && pesp.getProjectExpectedStudy().getId() != null)
          .map(pesp -> pesp.getProjectExpectedStudy().getId()).collect(Collectors.toList());

      List<Boolean> flagshipProgressStudiesActiveStatus = reportSynthesisFlagshipProgressStudyManager.findAll().stream()
        .filter(fpp -> fpp != null && fpp.getId() != null && fpp.getReportSynthesisFlagshipProgress() != null
          && fpp.getReportSynthesisFlagshipProgress().getId() != null
          && fpp.getReportSynthesisFlagshipProgress().getId().equals(synthesisID)
          && fpp.getProjectExpectedStudy() != null && fpp.getProjectExpectedStudy().getId() != null
          && projectExpectedStudiesPolicyIds.contains(fpp.getProjectExpectedStudy().getId()))
        .map(fpp -> fpp.isActive()).collect(Collectors.toList());

      if (flagshipProgressStudiesActiveStatus.size() == projectExpectedStudiesPolicyIds.size()
        // all the studies are excluded from the AR report (isActive = true/1)
        && flagshipProgressStudiesActiveStatus.stream().reduce(true, Boolean::logicalAnd)) {
        ReportSynthesisFlagshipProgressPolicy flagshipPolicy =
          phaseFlagshipPolicies.stream().filter(fpp -> fpp.getProjectPolicy().getId().equals(policy.getId()))
            .sorted((fppOne, fppTwo) -> Long.compare(fppOne.getId(), fppTwo.getId())).findFirst().orElse(null);
        if (flagshipPolicy == null) {
          // not found, we have to create one
          flagshipPolicy = new ReportSynthesisFlagshipProgressPolicy();
          flagshipPolicy.setProjectPolicy(policy);
          flagshipPolicy.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
        }

        // the study was excluded from the AR report, we need to exclude ALL the policies linked to it too
        flagshipPolicy.setActive(true);
        reportSynthesisFlagshipProgressPolicyManager.saveReportSynthesisFlagshipProgressPolicy(flagshipPolicy);
      }
    }
  }

  @Override
  public void validate() {
    if (this.isPMU()) {
      if (save) {
        validator.validateCheckButton(this, reportSynthesis, true, liaisonInstitution);
      }
    } else {
      if (save) {
        validator.validate(this, reportSynthesis, true);
      }
    }
  }
}