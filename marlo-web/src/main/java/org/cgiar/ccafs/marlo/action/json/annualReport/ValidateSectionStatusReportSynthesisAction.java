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

package org.cgiar.ccafs.marlo.action.json.annualReport;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.annualreport.ReportSynthesisSectionValidator;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ValidateSectionStatusReportSynthesisAction extends BaseAction {


  private static final Logger LOG = LoggerFactory.getLogger(ValidateSectionStatusReportSynthesisAction.class);


  private static final long serialVersionUID = 6393245587625917548L;

  private SectionStatusManager sectionStatusManager;
  private ReportSynthesisManager reportSynthesisManager;
  private GlobalUnitManager crpManager;
  private PhaseManager phaseManager;
  private CrpProgramManager crpProgramManager;

  private SectionStatus sectionStatus;
  private GlobalUnit loggedCrp;
  private boolean validSection;
  private String sectionName;
  private boolean existReportSynthesis;
  private Map<String, Object> section;
  private final ReportSynthesisSectionValidator<ValidateSectionStatusReportSynthesisAction> reportSynthesisSectionValidator;
  private long phaseID;
  private long synthesisID;

  @Inject
  public ValidateSectionStatusReportSynthesisAction(APConfig config, SectionStatusManager sectionStatusManager,
    ReportSynthesisManager reportSynthesisManager, GlobalUnitManager crpManager,
    ReportSynthesisSectionValidator<ValidateSectionStatusReportSynthesisAction> reportSynthesisSectionValidator,
    PhaseManager phaseManager, CrpProgramManager crpProgramManager) {
    super(config);
    this.crpManager = crpManager;
    this.sectionStatusManager = sectionStatusManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisSectionValidator = reportSynthesisSectionValidator;
    this.phaseManager = phaseManager;
    this.crpProgramManager = crpProgramManager;
  }

  @Override
  public String execute() throws Exception {
    Phase phase = phaseManager.getPhaseById(phaseID);
    ReportSynthesis reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);
    if (validSection && existReportSynthesis) {

      switch (ReportSynthesisSectionStatusEnum.value(sectionName.toUpperCase())) {
        case CRP_PROGRESS:
          reportSynthesisSectionValidator.validateCrpProgress(this, reportSynthesis);
          break;
        case FLAGSHIP_PROGRESS:
          reportSynthesisSectionValidator.validateFlagshipProgress(this, reportSynthesis);
          break;
        case CROSS_CUTTING:
          reportSynthesisSectionValidator.validateCrossCuttingDimensions(this, reportSynthesis);
          break;
        case VARIANCE:
          reportSynthesisSectionValidator.validateVariance(this, reportSynthesis);
          break;
        case FUNDING_USE:
          reportSynthesisSectionValidator.validateFunding(this, reportSynthesis);
          break;
        case EXTERNAL_PARTNERSHIP:
          reportSynthesisSectionValidator.validateKeyExternal(this, reportSynthesis);
          break;
        case CROSS_CGIAR:
          reportSynthesisSectionValidator.validateCgiarPartnership(this, reportSynthesis);
          break;
        case MELIA:
          reportSynthesisSectionValidator.validateMelia(this, reportSynthesis);
          break;
        case EFFICIENCY:
          reportSynthesisSectionValidator.validateEfficency(this, reportSynthesis);
          break;
        case GOVERNANCE:
          reportSynthesisSectionValidator.validateGobernance(this, reportSynthesis);
          break;
        case RISKS:
          reportSynthesisSectionValidator.validateRisk(this, reportSynthesis);
          break;
        case FINANCIAL_SUMMARY:
          reportSynthesisSectionValidator.validateFinancial(this, reportSynthesis);
          break;
        case INFLUENCE:
          reportSynthesisSectionValidator.validateIndicatorsInfluence(this, reportSynthesis);
          break;
        case CONTROL:
          reportSynthesisSectionValidator.validateControlIndicators(this, reportSynthesis);
          break;


        default:
          break;
      }


    }

    String cycle = "";
    if (this.isPlanningActive()) {
      cycle = APConstants.PLANNING;
    } else {
      cycle = APConstants.REPORTING;
    }

    section = new HashMap<String, Object>();
    section.put("sectionName", sectionName);
    section.put("missingFields", "");


    switch (ReportSynthesisSectionStatusEnum.value(sectionName.toUpperCase())) {

      case FLAGSHIP_PROGRESS:
        if (this.isFlagship(reportSynthesis.getLiaisonInstitution())) {
          sectionStatus = sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(), cycle,
            phase.getYear(), phase.getUpkeep(), sectionName);

          if (sectionStatus == null) {
            sectionStatus = new SectionStatus();
            sectionStatus.setMissingFields("No section");
          }
          if (sectionStatus.getMissingFields().length() > 0) {
            section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());
          }
        }
        break;

      case VARIANCE:
      case FUNDING_USE:
      case EFFICIENCY:
      case GOVERNANCE:
      case RISKS:
      case FINANCIAL_SUMMARY:
      case INFLUENCE:
      case CONTROL:
        if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
          sectionStatus = sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(), cycle,
            phase.getYear(), phase.getUpkeep(), sectionName);

          if (sectionStatus == null) {
            sectionStatus = new SectionStatus();
            sectionStatus.setMissingFields("No section");
          }
          if (sectionStatus.getMissingFields().length() > 0) {
            section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());
          }
        }

        break;

      default:
        sectionStatus = sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(), cycle,
          phase.getYear(), phase.getUpkeep(), sectionName);
        if (sectionStatus == null) {
          sectionStatus = new SectionStatus();
          sectionStatus.setMissingFields("No section");
        }
        if (sectionStatus.getMissingFields().length() > 0) {
          section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());
        }
        break;
    }


    return SUCCESS;
  }

  public Map<String, Object> getSection() {
    return section;
  }

  public String getSectionName() {
    return sectionName;
  }


  public boolean isFlagship(LiaisonInstitution liaisonInstitution) {
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


  public boolean isPMU(LiaisonInstitution liaisonInstitution) {
    boolean isFP = false;
    if (liaisonInstitution.getCrpProgram() == null) {
      isFP = true;
    }
    return isFP;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();


    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    sectionName = StringUtils.trim(parameters.get(APConstants.SECTION_NAME).getMultipleValues()[0]);
    phaseID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
    synthesisID =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.REPORT_SYNTHESIS_ID).getMultipleValues()[0]));
    // Validate if the section exists.
    validSection = ReportSynthesisSectionStatusEnum.value(sectionName) != null;

    existReportSynthesis = reportSynthesisManager.existReportSynthesis(synthesisID);

  }

  public void setSection(Map<String, Object> section) {
    this.section = section;
  }

  public void setSectionName(String sectionName) {
    this.sectionName = sectionName;
  }


}
