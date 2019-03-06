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
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis2018SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.ReportSynthesis2018SectionValidator;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
@Named
public class ValidateSectionStatusReportSynthesis2018Action extends BaseAction {

  private static final long serialVersionUID = 5506654702402026819L;

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
  private final ReportSynthesis2018SectionValidator<ValidateSectionStatusReportSynthesis2018Action> reportSynthesisSectionValidator2018;
  private long phaseID;
  private long synthesisID;

  @Inject
  public ValidateSectionStatusReportSynthesis2018Action(APConfig config, SectionStatusManager sectionStatusManager,
    ReportSynthesisManager reportSynthesisManager, GlobalUnitManager crpManager,
    ReportSynthesis2018SectionValidator<ValidateSectionStatusReportSynthesis2018Action> reportSynthesisSectionValidator2018,
    PhaseManager phaseManager, CrpProgramManager crpProgramManager) {
    super(config);
    this.crpManager = crpManager;
    this.sectionStatusManager = sectionStatusManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisSectionValidator2018 = reportSynthesisSectionValidator2018;
    this.phaseManager = phaseManager;
    this.crpProgramManager = crpProgramManager;
  }

  @Override
  public String execute() throws Exception {
    Phase phase = phaseManager.getPhaseById(phaseID);
    ReportSynthesis reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);
    if (validSection && existReportSynthesis) {

      switch (ReportSynthesis2018SectionStatusEnum.value(sectionName.toUpperCase())) {
        // case CRP_PROGRESS:
        // reportSynthesisSectionValidator2018.validateCrpProgress(this, reportSynthesis);
        // break;
        case FLAGSHIP_PROGRESS:
          reportSynthesisSectionValidator2018.validateFlagshipProgressValidator(this, reportSynthesis);
          break;
        case CC_DIMENSIONS:
          reportSynthesisSectionValidator2018.validateCrossCuttingDimensionValidator(this, reportSynthesis);
          break;
        // case GOVERNANCE:
        // reportSynthesisSectionValidator2018.validateGovernance(this, reportSynthesis);
        // break;
        // case EXTERNAL_PARTNERSHIPS:
        // reportSynthesisSectionValidator2018.validateExternalPartnerships(this, reportSynthesis);
        // break;
        case INTELLECTUAL_ASSETS:
          reportSynthesisSectionValidator2018.validateIntellectualAssets(this, reportSynthesis);
          break;
        // case MELIA:
        // reportSynthesisSectionValidator2018.validateMelia(this, reportSynthesis);
        // break;
        // case EFFICIENCY:
        // reportSynthesisSectionValidator2018.validateEfficency(this, reportSynthesis);
        // break;
        // case RISKS:
        // reportSynthesisSectionValidator2018.validateRisk(this, reportSynthesis);
        // break;
        case FUNDING_USE:
          reportSynthesisSectionValidator2018.validateFundingUse(this, reportSynthesis);
          break;
        // case FINANCIAL:
        // reportSynthesisSectionValidator2018.validateFinancial(this, reportSynthesis);
        // break;
        // case INFLUENCE:
        // reportSynthesisSectionValidator2018.validateIndicatorsInfluence(this, reportSynthesis);
        // break;
        // case CONTROL:
        // reportSynthesisSectionValidator2018.validateControlIndicators(this, reportSynthesis);
        // break;
        // case NARRATIVE:
        // reportSynthesisSectionValidator2018.validateNarrative(this, reportSynthesis);
        // break;
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


    switch (ReportSynthesis2018SectionStatusEnum.value(sectionName.toUpperCase())) {
      case INTELLECTUAL_ASSETS:
      case FUNDING_USE:
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
    validSection = ReportSynthesis2018SectionStatusEnum.value(sectionName) != null;

    existReportSynthesis = reportSynthesisManager.existReportSynthesis(synthesisID);

  }

  public void setSection(Map<String, Object> section) {
    this.section = section;
  }

  public void setSectionName(String sectionName) {
    this.sectionName = sectionName;
  }

}