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
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisEfficiency;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestone;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestoneLink;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisGovernance;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaboration;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaborationCrp;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternal;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalMainArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipPmu;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMelia;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaStudy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisNarrative;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisRisk;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgress;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ReportSynthesis2018SectionValidator<T extends BaseAction> extends BaseValidator {

  private ReportSynthesisManager reportSynthesisManager;
  private CrpMilestoneManager crpMilestoneManager;
  private GeneralStatusManager generalStatusManager;
  private ReportSynthesisFlagshipProgressCrossCuttingMarkerManager reportSynthesisFlagshipProgressCrossCuttingMarkerManager;
  private ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManager reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager;

  // Validations
  private final IntellectualAssetsValidator intellectualAssetsValidator;
  private final CCDimension2018Validator ccDimensionValidator;
  private final FundingUse2018Validator fundingUse2018Validator;
  private final FlagshipProgress2018Validator flagshipProgress2018Validator;
  private final Policies2018Validator policies2018Validator;
  private final StudiesOICR2018Validator studiesOICR2018Validator;
  private final Innovations2018Validator innovations2018Validator;
  private final Publications2018Validator publications2018Validator;
  private final FinancialSummary2018Validator financialSummary2018Validator;
  private final NarrativeValidator narrativeValidator;
  private final Efficiency2018Validator efficiency2018Validator;
  private final Risk2018Validator risk2018Validator;
  private final Governance2018Validator governance2018Validator;
  private final SrfProgressValidator srfProgressValidator;
  private final PartnershipValidator partnershipValidator;
  private final FileDBManager fileDBManager;
  private final MonitoringEvaluationValidator monitoringEvaluationValidator;
  private final OutcomeMilestonesValidator outcomeMilestonesValidator;


  @Inject
  public ReportSynthesis2018SectionValidator(ReportSynthesisManager reportSynthesisManager,
    IntellectualAssetsValidator intellectualAssetsValidator, CCDimension2018Validator ccDimensionValidator,
    FundingUse2018Validator fundingUse2018Validator, FlagshipProgress2018Validator flagshipProgress2018Validator,
    Policies2018Validator policies2018Validator, StudiesOICR2018Validator studiesOICR2018Validator,
    Innovations2018Validator innovations2018Validator, Publications2018Validator publications2018Validator,
    FinancialSummary2018Validator financialSummary2018Validator, NarrativeValidator narrativeValidator,
    Efficiency2018Validator efficiency2018Validator, Risk2018Validator risk2018Validator,
    Governance2018Validator governance2018Validator, SrfProgressValidator srfProgressValidator,
    PartnershipValidator partnershipValidator, FileDBManager fileDBManager,
    MonitoringEvaluationValidator monitoringEvaluationValidator, OutcomeMilestonesValidator outcomeMilestonesValidator,
    CrpMilestoneManager crpMilestoneManager, GeneralStatusManager generalStatusManager,
    ReportSynthesisFlagshipProgressCrossCuttingMarkerManager reportSynthesisFlagshipProgressCrossCuttingMarkerManager,
    ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManager reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager) {
    super();
    this.reportSynthesisManager = reportSynthesisManager;
    this.intellectualAssetsValidator = intellectualAssetsValidator;
    this.ccDimensionValidator = ccDimensionValidator;
    this.fundingUse2018Validator = fundingUse2018Validator;
    this.flagshipProgress2018Validator = flagshipProgress2018Validator;
    this.policies2018Validator = policies2018Validator;
    this.studiesOICR2018Validator = studiesOICR2018Validator;
    this.innovations2018Validator = innovations2018Validator;
    this.publications2018Validator = publications2018Validator;
    this.financialSummary2018Validator = financialSummary2018Validator;
    this.narrativeValidator = narrativeValidator;
    this.efficiency2018Validator = efficiency2018Validator;
    this.risk2018Validator = risk2018Validator;
    this.governance2018Validator = governance2018Validator;
    this.srfProgressValidator = srfProgressValidator;
    this.partnershipValidator = partnershipValidator;
    this.fileDBManager = fileDBManager;
    this.monitoringEvaluationValidator = monitoringEvaluationValidator;
    this.outcomeMilestonesValidator = outcomeMilestonesValidator;
    this.crpMilestoneManager = crpMilestoneManager;
    this.generalStatusManager = generalStatusManager;
    this.reportSynthesisFlagshipProgressCrossCuttingMarkerManager =
      reportSynthesisFlagshipProgressCrossCuttingMarkerManager;
    this.reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager =
      reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager;
  }


  /**
   * Get the information for the Cross Cutting marker in the form
   * 
   * @param markerID
   * @return
   */
  public List<ReportSynthesisFlagshipProgressCrossCuttingMarker>
    getCrossCuttingMarker(ReportSynthesisFlagshipProgressOutcomeMilestone progressOutcomeMilestone) {
    Long progressOutcomeMilestoneID = progressOutcomeMilestone.getId();
    if (progressOutcomeMilestoneID != -1) {
      List<ReportSynthesisFlagshipProgressCrossCuttingMarker> crossCuttingMarkers =
        reportSynthesisFlagshipProgressCrossCuttingMarkerManager.getMarkersPerMilestone(progressOutcomeMilestoneID);
      return (crossCuttingMarkers != null && !crossCuttingMarkers.isEmpty()) ? crossCuttingMarkers : null;
    } else {
      return null;
    }
  }

  private GeneralStatus getCurrentMilestoneStatus(CrpMilestone milestone) {
    Long milestoneID = milestone.getId();
    GeneralStatus milestoneStatus = null;
    if (milestoneID != null && milestoneID != -1) {
      CrpMilestone currentMilestone = crpMilestoneManager.getCrpMilestoneById(milestoneID);
      if (currentMilestone != null && currentMilestone.getMilestonesStatus() != null
        && currentMilestone.getMilestonesStatus().getId() != null) {
        milestoneStatus = generalStatusManager.getGeneralStatusById(currentMilestone.getMilestonesStatus().getId());
      }
    }

    return milestoneStatus;
  }


  /**
   * Get the information for the Cross Cutting marker in the form
   * 
   * @param markerID
   * @return
   */
  public List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink>
    getLinks(ReportSynthesisFlagshipProgressOutcomeMilestone progressOutcomeMilestone) {
    Long progressOutcomeMilestoneID = progressOutcomeMilestone.getId();
    if (progressOutcomeMilestoneID != -1) {
      List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink> links =
        reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager
          .getLinksByProgressOutcomeMilestone(progressOutcomeMilestoneID);
      return (links != null && !links.isEmpty()) ? links : null;
    } else {
      return null;
    }
  }

  /**
   * Check if the Liaison Institution is PMU
   * 
   * @return true if liaisonInstitution exist and is PMU
   */
  public boolean isPMU(LiaisonInstitution liaisonInstitution) {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;
  }

  public void validateCrossCuttingDimensionValidator(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisCrossCuttingDimension() == null) {
      ReportSynthesisCrossCuttingDimension crossCuttingDimension = new ReportSynthesisCrossCuttingDimension();

      // create one to one relation
      reportSynthesis.setReportSynthesisCrossCuttingDimension(crossCuttingDimension);
      crossCuttingDimension.setReportSynthesis(reportSynthesis);

      ccDimensionValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      ccDimensionValidator.validate(action, reportSynthesis, false);
    }

  }

  public void validateCrpProgress(BaseAction action, ReportSynthesis reportSynthesis) {

    // Check if relation is null -create it
    if (reportSynthesis.getReportSynthesisSrfProgress() == null) {
      ReportSynthesisSrfProgress srfProgress = new ReportSynthesisSrfProgress();
      // create one to one relation
      reportSynthesis.setReportSynthesisSrfProgress(srfProgress);
      srfProgress.setReportSynthesis(reportSynthesis);

      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        srfProgressValidator.validateCheckButton(action, reportSynthesis, false, null);
      } else {
        srfProgressValidator.validate(action, reportSynthesis, false, null);
      }
      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {

      // Srf Targets List
      if (reportSynthesis.getReportSynthesisSrfProgress().getReportSynthesisSrfProgressTargets() != null) {
        reportSynthesis.getReportSynthesisSrfProgress()
          .setSloTargets(new ArrayList<>(reportSynthesis.getReportSynthesisSrfProgress()
            .getReportSynthesisSrfProgressTargets().stream().filter(t -> t.isActive()).collect(Collectors.toList())));
      }

      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        srfProgressValidator.validateCheckButton(action, reportSynthesis, false, null);
      } else {
        srfProgressValidator.validate(action, reportSynthesis, false, null);
      }
    }


  }

  public void validateEfficiency(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisEfficiency() == null) {
      ReportSynthesisEfficiency efficiency = new ReportSynthesisEfficiency();

      // create one to one relation
      reportSynthesis.setReportSynthesisEfficiency(efficiency);
      efficiency.setReportSynthesis(reportSynthesis);

      efficiency2018Validator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      efficiency2018Validator.validate(action, reportSynthesis, false);
    }

  }

  public void validateExternalPartnerships(BaseAction action, ReportSynthesis reportSynthesis) {

    // Check if relation is null -create it
    if (reportSynthesis.getReportSynthesisKeyPartnership() == null) {
      ReportSynthesisKeyPartnership keyPartnership = new ReportSynthesisKeyPartnership();
      // create one to one relation
      reportSynthesis.setReportSynthesisKeyPartnership(keyPartnership);
      keyPartnership.setReportSynthesis(reportSynthesis);

      partnershipValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {


      if (!this.isPMU(reportSynthesis.getLiaisonInstitution())) {

        // Key External Partnership List
        reportSynthesis.getReportSynthesisKeyPartnership().setPartnerships(new ArrayList<>());

        if (reportSynthesis.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals() != null
          && !reportSynthesis.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals()
            .isEmpty()) {
          for (ReportSynthesisKeyPartnershipExternal keyPartnershipExternal : reportSynthesis
            .getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals().stream()
            .filter(ro -> ro.isActive()).collect(Collectors.toList())) {

            // Setup Main Areas And Institutions
            keyPartnershipExternal.setMainAreas(new ArrayList<>());
            keyPartnershipExternal.setInstitutions(new ArrayList<>());

            if (keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalInstitutions() != null
              && !keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalInstitutions().isEmpty()) {

              for (ReportSynthesisKeyPartnershipExternalInstitution institution : keyPartnershipExternal
                .getReportSynthesisKeyPartnershipExternalInstitutions().stream().filter(ro -> ro.isActive())
                .collect(Collectors.toList())) {
                keyPartnershipExternal.getInstitutions().add(institution);
              }

            }


            if (keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalMainAreas() != null
              && !keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalMainAreas().isEmpty()) {

              for (ReportSynthesisKeyPartnershipExternalMainArea mainArea : keyPartnershipExternal
                .getReportSynthesisKeyPartnershipExternalMainAreas().stream().filter(ro -> ro.isActive())
                .collect(Collectors.toList())) {
                keyPartnershipExternal.getMainAreas().add(mainArea);
              }

            }

            // Load File
            if (keyPartnershipExternal.getFile() != null) {
              if (keyPartnershipExternal.getFile().getId() != null) {
                keyPartnershipExternal.setFile(fileDBManager.getFileDBById(keyPartnershipExternal.getFile().getId()));
              }
            }


            reportSynthesis.getReportSynthesisKeyPartnership().getPartnerships().add(keyPartnershipExternal);
          }

          reportSynthesis.getReportSynthesisKeyPartnership().getPartnerships()
            .sort(Comparator.comparing(ReportSynthesisKeyPartnershipExternal::getId));

        }


      } else {

        // Load Pmu External Partnerships
        reportSynthesis.getReportSynthesisKeyPartnership().setSelectedExternalPartnerships(new ArrayList<>());
        if (reportSynthesis.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipPmus() != null
          && !reportSynthesis.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipPmus().isEmpty()) {
          for (ReportSynthesisKeyPartnershipPmu plannedPmu : reportSynthesis.getReportSynthesisKeyPartnership()
            .getReportSynthesisKeyPartnershipPmus().stream().filter(ro -> ro.isActive()).collect(Collectors.toList())) {
            reportSynthesis.getReportSynthesisKeyPartnership().getSelectedExternalPartnerships()
              .add(plannedPmu.getReportSynthesisKeyPartnershipExternal());
          }
        }


      }
      // Load CGIAR collaborations
      reportSynthesis.getReportSynthesisKeyPartnership().setCollaborations(new ArrayList<>());

      if (reportSynthesis.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborations() != null
        && !reportSynthesis.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborations()
          .isEmpty()) {

        for (ReportSynthesisKeyPartnershipCollaboration keyPartnershipCollaboration : reportSynthesis
          .getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborations().stream()
          .filter(ro -> ro.isActive()).collect(Collectors.toList())) {

          keyPartnershipCollaboration.setCrps(new ArrayList<>());

          if (keyPartnershipCollaboration.getReportSynthesisKeyPartnershipCollaborationCrps() != null
            && !keyPartnershipCollaboration.getReportSynthesisKeyPartnershipCollaborationCrps().isEmpty()) {

            for (ReportSynthesisKeyPartnershipCollaborationCrp crp : keyPartnershipCollaboration
              .getReportSynthesisKeyPartnershipCollaborationCrps().stream().filter(c -> c.isActive())
              .collect(Collectors.toList())) {
              keyPartnershipCollaboration.getCrps().add(crp);
            }
          }


          reportSynthesis.getReportSynthesisKeyPartnership().getCollaborations().add(keyPartnershipCollaboration);
        }

        reportSynthesis.getReportSynthesisKeyPartnership().getCollaborations()
          .sort(Comparator.comparing(ReportSynthesisKeyPartnershipCollaboration::getId));
      }

      partnershipValidator.validate(action, reportSynthesis, false);
    }

  }


  public void validateFinancial(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFinancialSummary() == null) {
      ReportSynthesisFinancialSummary financial = new ReportSynthesisFinancialSummary();

      // create one to one relation
      reportSynthesis.setReportSynthesisFinancialSummary(financial);
      financial.setReportSynthesis(reportSynthesis);
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        // Flagships Financial Budgets
        if (reportSynthesis.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets() != null
          && !reportSynthesis.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets()
            .isEmpty()) {
          reportSynthesis.getReportSynthesisFinancialSummary()
            .setBudgets(new ArrayList<>(
              reportSynthesis.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets().stream()
                .filter(t -> t.isActive()).collect(Collectors.toList())));
        }

        financialSummary2018Validator.validate(action, reportSynthesis, false);

        // save the changes
        reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
      }
    } else {
      financialSummary2018Validator.validate(action, reportSynthesis, false);
    }
  }


  public void validateFlagshipProgressValidator(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
      ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();

      // create one to one relation
      reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
      flagshipProgress.setReportSynthesis(reportSynthesis);

      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        flagshipProgress2018Validator.validateCheckButton(action, reportSynthesis, false, false);
      } else {
        flagshipProgress2018Validator.validate(action, reportSynthesis, false, false);
      }
      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        flagshipProgress2018Validator.validateCheckButton(action, reportSynthesis, false, false);
      } else {
        flagshipProgress2018Validator.validate(action, reportSynthesis, false, false);
      }
    }

  }

  public void validateFundingUse(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFundingUseSummary() == null) {
      ReportSynthesisFundingUseSummary fundingUseSummary = new ReportSynthesisFundingUseSummary();

      // create one to one relation
      reportSynthesis.setReportSynthesisFundingUseSummary(fundingUseSummary);
      fundingUseSummary.setReportSynthesis(reportSynthesis);

      fundingUse2018Validator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      fundingUse2018Validator.validate(action, reportSynthesis, false);
    }

  }

  public void validateGovernance(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisGovernance() == null) {
      ReportSynthesisGovernance intellectualAsset = new ReportSynthesisGovernance();

      // create one to one relation
      reportSynthesis.setReportSynthesisGovernance(intellectualAsset);
      intellectualAsset.setReportSynthesis(reportSynthesis);

      governance2018Validator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      governance2018Validator.validate(action, reportSynthesis, false);
    }

  }

  public void validateInnovations(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
      ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();

      // create one to one relation
      reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
      flagshipProgress.setReportSynthesis(reportSynthesis);

      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        innovations2018Validator.validateCheckButton(action, reportSynthesis, false);
      } else {
        innovations2018Validator.validate(action, reportSynthesis, false);
      }

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        innovations2018Validator.validateCheckButton(action, reportSynthesis, false);
      } else {
        innovations2018Validator.validate(action, reportSynthesis, false);
      }

    }

  }

  public void validateIntellectualAssets(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisIntellectualAsset() == null) {
      ReportSynthesisIntellectualAsset intellectualAsset = new ReportSynthesisIntellectualAsset();

      // create one to one relation
      reportSynthesis.setReportSynthesisIntellectualAsset(intellectualAsset);
      intellectualAsset.setReportSynthesis(reportSynthesis);

      intellectualAssetsValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      intellectualAssetsValidator.validate(action, reportSynthesis, false);
    }

  }

  public void validateMelia(BaseAction action, ReportSynthesis reportSynthesis) {

    // Check if relation is null -create it
    if (reportSynthesis.getReportSynthesisMelia() == null) {
      ReportSynthesisMelia melia = new ReportSynthesisMelia();
      // create one to one relation
      reportSynthesis.setReportSynthesisMelia(melia);;
      melia.setReportSynthesis(reportSynthesis);

      monitoringEvaluationValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      // Crp Progress Studies
      reportSynthesis.getReportSynthesisMelia().setExpectedStudies(new ArrayList<>());
      if (reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaStudies() != null
        && !reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaStudies().isEmpty()) {
        for (ReportSynthesisMeliaStudy plannedStudy : reportSynthesis.getReportSynthesisMelia()
          .getReportSynthesisMeliaStudies().stream().filter(ro -> ro.isActive()).collect(Collectors.toList())) {
          reportSynthesis.getReportSynthesisMelia().getExpectedStudies().add(plannedStudy.getProjectExpectedStudy());
        }
      }

      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        if (reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaEvaluations() != null
          && !reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaEvaluations().isEmpty()) {
          reportSynthesis.getReportSynthesisMelia()
            .setEvaluations(new ArrayList<>(reportSynthesis.getReportSynthesisMelia()
              .getReportSynthesisMeliaEvaluations().stream().filter(e -> e.isActive()).collect(Collectors.toList())));
          reportSynthesis.getReportSynthesisMelia().getEvaluations()
            .sort(Comparator.comparing(ReportSynthesisMeliaEvaluation::getId));

          // load evaluation actions
          if (reportSynthesis.getReportSynthesisMelia().getEvaluations() != null
            && !reportSynthesis.getReportSynthesisMelia().getEvaluations().isEmpty()) {
            for (ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation : reportSynthesis
              .getReportSynthesisMelia().getEvaluations()) {
              if (reportSynthesisMeliaEvaluation.getReportSynthesisMeliaEvaluationActions() != null
                && !reportSynthesisMeliaEvaluation.getReportSynthesisMeliaEvaluationActions().isEmpty()) {
                reportSynthesisMeliaEvaluation.setMeliaEvaluationActions(
                  new ArrayList<>(reportSynthesisMeliaEvaluation.getReportSynthesisMeliaEvaluationActions().stream()
                    .filter(e -> e.isActive()).collect(Collectors.toList())));
              }
            }
          }

        }
      }

      monitoringEvaluationValidator.validate(action, reportSynthesis, false);
    }


  }

  public void validateNarrative(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisNarrative() == null) {
      ReportSynthesisNarrative narrative = new ReportSynthesisNarrative();

      // create one to one relation
      reportSynthesis.setReportSynthesisNarrative(narrative);
      narrative.setReportSynthesis(reportSynthesis);

      narrativeValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      narrativeValidator.validate(action, reportSynthesis, false);
    }

  }

  public void validateOutcomeMilestones(BaseAction action, ReportSynthesis reportSynthesis) {

    // Check if relation is null -create it
    if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
      ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();
      // create one to one relation
      reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
      flagshipProgress.setReportSynthesis(reportSynthesis);

      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        outcomeMilestonesValidator.validate(action, reportSynthesis, false);
      } else {
        outcomeMilestonesValidator.validate(action, reportSynthesis, false);
      }

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {


      if (!this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        // Setu up Milestones Flagship Table
        if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {


          List<ReportSynthesisFlagshipProgressOutcome> reportOutcomes = new ArrayList<>(
            reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressOutcomes().stream()
              .filter(c -> c.isActive() && c.getCrpProgramOutcome() != null).collect(Collectors.toList()));

          reportSynthesis.getReportSynthesisFlagshipProgress().setOutcomeList(reportOutcomes);

          for (ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome : reportOutcomes) {

            List<ReportSynthesisFlagshipProgressOutcomeMilestone> milestones = new ArrayList<>();
            for (ReportSynthesisFlagshipProgressOutcomeMilestone progressOutcomeMilestone : reportSynthesisFlagshipProgressOutcome
              .getReportSynthesisFlagshipProgressOutcomeMilestones()) {

              if (progressOutcomeMilestone != null && progressOutcomeMilestone.getId() != null
                && progressOutcomeMilestone.isActive() && progressOutcomeMilestone.getCrpMilestone() != null
                && progressOutcomeMilestone.getCrpMilestone().getId() != null) {

                if (progressOutcomeMilestone.getMilestonesStatus() == null
                  || progressOutcomeMilestone.getMilestonesStatus().getId() == null) {
                  progressOutcomeMilestone
                    .setMilestonesStatus(this.getCurrentMilestoneStatus(progressOutcomeMilestone.getCrpMilestone()));
                }

                progressOutcomeMilestone.setMarkers(this.getCrossCuttingMarker(progressOutcomeMilestone));

                if (action.isSelectedPhaseAR2021()) {
                  progressOutcomeMilestone.setLinks(this.getLinks(progressOutcomeMilestone));
                }

                milestones.add(progressOutcomeMilestone);
              }
            }

            reportSynthesisFlagshipProgressOutcome.setMilestones(milestones);

            reportSynthesisFlagshipProgressOutcome.getMilestones()
              .sort((p1, p2) -> p1.getCrpMilestone().getId().compareTo(p2.getCrpMilestone().getId()));
          }

          reportOutcomes
            .sort((p1, p2) -> p1.getCrpProgramOutcome().getId().compareTo(p2.getCrpProgramOutcome().getId()));
        }

      } else {

        List<ReportSynthesisFlagshipProgressOutcome> reportOutcomes = new ArrayList<>(
          reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressOutcomes().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList()));

        reportSynthesis.getReportSynthesisFlagshipProgress().setOutcomeList(reportOutcomes);

      }
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        outcomeMilestonesValidator.validate(action, reportSynthesis, false);
      } else {
        outcomeMilestonesValidator.validate(action, reportSynthesis, false);
      }
    }


  }

  public void validatePolicies(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {

      ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();

      // create one to one relation
      reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
      flagshipProgress.setReportSynthesis(reportSynthesis);
      if (reportSynthesis.getLiaisonInstitution() != null
        && reportSynthesis.getLiaisonInstitution().getCrpProgram() == null) {

      }
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        policies2018Validator.validateCheckButton(action, reportSynthesis, false);
      } else {
        policies2018Validator.validate(action, reportSynthesis, false);
      }

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        policies2018Validator.validateCheckButton(action, reportSynthesis, false);
      } else {
        policies2018Validator.validate(action, reportSynthesis, false);
      }
    }

  }

  public void validatePublications(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
      ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();

      // create one to one relation
      reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
      flagshipProgress.setReportSynthesis(reportSynthesis);

      publications2018Validator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        publications2018Validator.validate(action, reportSynthesis, false);
      }
    }

  }

  public void validateRisk(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisRisk() == null) {
      ReportSynthesisRisk intellectualAsset = new ReportSynthesisRisk();

      // create one to one relation
      reportSynthesis.setReportSynthesisRisk(intellectualAsset);
      intellectualAsset.setReportSynthesis(reportSynthesis);

      risk2018Validator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        risk2018Validator.validate(action, reportSynthesis, false);
      }
    }

  }

  public void validateStudiesOICR(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
      ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();

      // create one to one relation
      reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
      flagshipProgress.setReportSynthesis(reportSynthesis);
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        studiesOICR2018Validator.validateCheckButton(action, reportSynthesis, false,
          reportSynthesis.getLiaisonInstitution());
      } else {
        studiesOICR2018Validator.validate(action, reportSynthesis, false);
      }
      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        studiesOICR2018Validator.validateCheckButton(action, reportSynthesis, false,
          reportSynthesis.getLiaisonInstitution());
      } else {
        studiesOICR2018Validator.validate(action, reportSynthesis, false);
      }
    }

  }

}