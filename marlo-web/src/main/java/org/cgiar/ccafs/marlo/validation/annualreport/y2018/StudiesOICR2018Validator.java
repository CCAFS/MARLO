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

package org.cgiar.ccafs.marlo.validation.annualreport.y2018;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis2018SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudy;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
@Named
public class StudiesOICR2018Validator extends BaseValidator {

  private static Logger LOG = LoggerFactory.getLogger(StudiesOICR2018Validator.class);

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;
  private final SectionStatusManager sectionStatusManager;
  private final ProjectExpectedStudyManager projectExpectedStudyManager;
  private final PhaseManager phaseManager;
  private final ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager;
  private final ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager;
  private final ReportSynthesisFlagshipProgressInnovationManager reportSynthesisFlagshipProgressInnovationManager;
  private final ReportSynthesisFlagshipProgressPolicyManager reportSynthesisFlagshipProgressPolicyManager;
  private final ProjectInnovationManager projectInnovationManager;
  private final ProjectPolicyManager projectPolicyManager;

  public StudiesOICR2018Validator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager,
    SectionStatusManager sectionStatusManager, ProjectExpectedStudyManager projectExpectedStudyManager,
    PhaseManager phaseManager, ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager,
    ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager,
    ReportSynthesisFlagshipProgressInnovationManager reportSynthesisFlagshipProgressInnovationManager,
    ReportSynthesisFlagshipProgressPolicyManager reportSynthesisFlagshipProgressPolicyManager,
    ProjectInnovationManager projectInnovationManager, ProjectPolicyManager projectPolicyManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.sectionStatusManager = sectionStatusManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.phaseManager = phaseManager;
    this.projectExpectedStudyInnovationManager = projectExpectedStudyInnovationManager;
    this.projectExpectedStudyPolicyManager = projectExpectedStudyPolicyManager;
    this.reportSynthesisFlagshipProgressInnovationManager = reportSynthesisFlagshipProgressInnovationManager;
    this.reportSynthesisFlagshipProgressPolicyManager = reportSynthesisFlagshipProgressPolicyManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectPolicyManager = projectPolicyManager;
  }


  private boolean canBeRemovedFromAR(long studyID, long phaseID, LiaisonInstitution liaisonInsitution) {
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
              .equals(liaisonInsitution.getId()))
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
              .equals(liaisonInsitution.getId()))
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

  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesis2018SectionStatusEnum.OICR.getStatus().replace("/", "_");
    String autoSaveFile =
      reportSynthesis.getId() + "_" + composedClassName + "_" + baseAction.getActualPhase().getName() + "_"
        + baseAction.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  private List<ProjectInnovation> getInnovations(long studyID, long phaseID) {
    List<ProjectInnovation> projectInnovationList = new ArrayList<>();

    if (studyID > 0) {
      List<ProjectExpectedStudyInnovation> studyInnovations = new ArrayList<>();
      Phase current = this.phaseManager.getPhaseById(phaseID);

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
              innovation.getProjectInnovationInfo(current);
              projectInnovationList.add(innovation);
            }
          }
        }
      }
    }

    return projectInnovationList;
  }


  public LiaisonInstitution getLiaisonInstitution(BaseAction action, long synthesisID) {
    ReportSynthesis reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);
    LiaisonInstitution liaisonInstitution = reportSynthesis.getLiaisonInstitution();
    return liaisonInstitution;
  }

  private List<ProjectPolicy> getPolicies(long studyID, long phaseID) {
    List<ProjectPolicy> policyList = new ArrayList<>();

    if (studyID > 0) {
      ProjectExpectedStudy expectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(studyID);
      Phase current = this.phaseManager.getPhaseById(phaseID);

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
                policy.getProjectPolicyInfo(current);
                policyList.add(studyPolicy.getProjectPolicy());
              }
            }
          }
        }
      }
    }

    return policyList;
  }

  public boolean isPMU(LiaisonInstitution liaisonInstitution) {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;
  }

  public void validate(BaseAction action, ReportSynthesis reportSynthesis, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (reportSynthesis != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(reportSynthesis, action.getCrpID(), action);
        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }

      // Save Synthesis Flagship
      if (reportSynthesis.getLiaisonInstitution() != null
        && reportSynthesis.getLiaisonInstitution().getAcronym() != null
        && !this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
          String sSynthesisFlagships = action.getSynthesisFlagships().toString();


          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("1")) {
            if (!sSynthesisFlagships.contains("1")) {
              action.addSynthesisFlagship("F1");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("2")) {
            if (!sSynthesisFlagships.contains("2")) {
              action.addSynthesisFlagship("F2");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("3")) {
            if (!sSynthesisFlagships.contains("3")) {
              action.addSynthesisFlagship("F3");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("4")) {
            if (!sSynthesisFlagships.contains("4")) {
              action.addSynthesisFlagship("F4");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("5")) {
            if (!sSynthesisFlagships.contains("5")) {
              action.addSynthesisFlagship("F5");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("6")) {
            if (!sSynthesisFlagships.contains("6")) {
              action.addSynthesisFlagship("F6");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("PMU")) {
            if (!sSynthesisFlagships.contains("PMU")) {
              action.addSynthesisFlagship("PMU");
            }
          }

        }
      }

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
        action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
        ReportSynthesis2018SectionStatusEnum.OICR.getStatus(), action);
    }

  }

  public void validateCheckButton(BaseAction action, ReportSynthesis reportSynthesis, boolean saving,
    LiaisonInstitution liaisonInsitution) {
    action.setInvalidFields(new HashMap<>());
    if (reportSynthesis != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(reportSynthesis, action.getCrpID(), action);
        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      List<ProjectExpectedStudy> projectExpectedStudiesTable3 = null;
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        // Get mark policies list

        projectExpectedStudiesTable3 = new ArrayList<>(projectExpectedStudyManager
          .getProjectStudiesList(reportSynthesis.getLiaisonInstitution(), action.getActualPhase()));

        if (reportSynthesis != null && reportSynthesis.getReportSynthesisFlagshipProgress() != null
          && reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressStudies() != null
          && !reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressStudies()
            .isEmpty()) {
          for (ReportSynthesisFlagshipProgressStudy flagshipProgressStudy : reportSynthesis
            .getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressStudies().stream()
            .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
            ProjectExpectedStudy excludedStudy = flagshipProgressStudy.getProjectExpectedStudy();
            if (!this.canBeRemovedFromAR(excludedStudy.getId(), action.getActualPhase().getId(), liaisonInsitution)) {
              action.addMissingField(action.getText("annualReport2018.oicr.table3.studyExcludedWithRelations",
                new String[] {String.valueOf(excludedStudy.getId())}));
            }

            projectExpectedStudiesTable3.remove(excludedStudy);
          }
        }
        // System.out.println(projectPoliciesTable2.size() + "policies Marcados");
      }

      boolean tableComplete = false;
      SectionStatus sectionStatus = sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
        "Reporting", 2019, false, "oicr");

      if (sectionStatus == null) {
        tableComplete = true;
        // sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
      } else

      if (sectionStatus != null && sectionStatus.getMissingFields() != null
        && sectionStatus.getMissingFields().length() != 0 && sectionStatus.getId() != null
        && sectionStatus.getId() != 0) {
        if (sectionStatus.getMissingFields().contains("synthesis.AR2019Table3")) {
          sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
          tableComplete = true;
        } else {
          tableComplete = false;
        }
      } else {
        tableComplete = true;
        if (sectionStatus != null && sectionStatus.getId() != null && sectionStatus.getId() != 0) {
          sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
        }
      }

      int count = 0;
      List<ProjectExpectedStudy> expectedStudies = null;
      if (projectExpectedStudiesTable3 != null) {
        expectedStudies = new ArrayList<>(projectExpectedStudiesTable3);
      } else {
        expectedStudies = projectExpectedStudyManager.getProjectStudiesList(reportSynthesis.getLiaisonInstitution(),
          action.getActualPhase());
      }
      if (expectedStudies != null && !expectedStudies.isEmpty()) {
        for (ProjectExpectedStudy expected : expectedStudies) {
          if (expected != null && expected.getId() != null) {
            sectionStatus = this.sectionStatusManager.getSectionStatusByProjectExpectedStudy(expected.getId(),
              "Reporting", action.getActualPhase().getYear(), false, "studies");

            if (sectionStatus != null && sectionStatus.getMissingFields() != null
              && sectionStatus.getMissingFields().length() != 0) {
              action.addMissingField(action.getText("annualReport2018.oicr.table3.studyIncomplete",
                new String[] {String.valueOf(expected.getId())}));
              count++;
            }

          }
        }

        if (count > 0) {
          // action.addMissingField("synthesis.AR2019Table3");
        }
      }

      // Save Synthesis Flagship
      if (reportSynthesis.getLiaisonInstitution() != null
        && reportSynthesis.getLiaisonInstitution().getAcronym() != null
        && !this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
          String sSynthesisFlagships = action.getSynthesisFlagships().toString();


          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("1")) {
            if (!sSynthesisFlagships.contains("1")) {
              action.addSynthesisFlagship("F1");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("2")) {
            if (!sSynthesisFlagships.contains("2")) {
              action.addSynthesisFlagship("F2");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("3")) {
            if (!sSynthesisFlagships.contains("3")) {
              action.addSynthesisFlagship("F3");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("4")) {
            if (!sSynthesisFlagships.contains("4")) {
              action.addSynthesisFlagship("F4");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("5")) {
            if (!sSynthesisFlagships.contains("5")) {
              action.addSynthesisFlagship("F5");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("6")) {
            if (!sSynthesisFlagships.contains("6")) {
              action.addSynthesisFlagship("F6");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("PMU")) {
            if (!sSynthesisFlagships.contains("PMU")) {
              action.addSynthesisFlagship("PMU");
            }
          }

        }
      }
      try {
        this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
          action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
          ReportSynthesis2018SectionStatusEnum.OICR.getStatus(), action);
      } catch (Exception e) {
        LOG.error("Error getting studies list: " + e.getMessage());
      }
    }

  }

  public void validatePMU(BaseAction action, ReportSynthesis reportSynthesis, boolean saving, boolean tableComplete) {
    action.setInvalidFields(new HashMap<>());
    if (reportSynthesis != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(reportSynthesis, action.getCrpID(), action);
        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }

      if (tableComplete == false) {
        // action.addMessage(action.getText("Incomplete OICRs"));
        action.addMissingField("synthesis.AR2019Table3");
      }

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
        action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
        ReportSynthesis2018SectionStatusEnum.OICR.getStatus(), action);
    }

  }

}