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

package org.cgiar.ccafs.marlo.action.json.powb;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.powb.PowbSynthesisSectionValidator;

import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ValidateSectionStatusPowbSynthesisAction extends BaseAction {


  private static final Logger LOG = LoggerFactory.getLogger(ValidateSectionStatusPowbSynthesisAction.class);


  private static final long serialVersionUID = 6393245587625917548L;

  private SectionStatusManager sectionStatusManager;
  private PowbSynthesisManager powbSynthesisManager;
  private GlobalUnitManager crpManager;

  private SectionStatus sectionStatus;
  private GlobalUnit loggedCrp;
  private boolean validSection;
  private String sectionName;
  private boolean existPowbSynthesis;
  private Map<String, Object> section;
  private final PowbSynthesisSectionValidator<ValidateSectionStatusPowbSynthesisAction> powbSynthesisSectionValidator;

  @Inject
  public ValidateSectionStatusPowbSynthesisAction(APConfig config, SectionStatusManager sectionStatusManager,
    PowbSynthesisManager powbSynthesisManager, GlobalUnitManager crpManager,
    PowbSynthesisSectionValidator<ValidateSectionStatusPowbSynthesisAction> powbSynthesisSectionValidator) {
    super(config);
    this.crpManager = crpManager;
    this.sectionStatusManager = sectionStatusManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.powbSynthesisSectionValidator = powbSynthesisSectionValidator;
  }

  @Override
  public String execute() throws Exception {
    if (validSection) {
      Phase phase = this.getActualPhase();
      switch (PowbSynthesisSectionStatusEnum.value(sectionName.toUpperCase())) {
        case TOC_ADJUSTMENTS:
          powbSynthesisSectionValidator.validateTocAdjustments(this, phase);
          break;
        case CRP_PROGRESS:
          powbSynthesisSectionValidator.validateCrpProgress(this, phase);
          break;
        case EVIDENCES:
          powbSynthesisSectionValidator.validateEvidence(this, phase);
          break;
        case FLAGSHIP_PLANS:
          powbSynthesisSectionValidator.validateFlagshipPlans(this, phase);
          break;
        case CROSS_CUTTING_DIMENSIONS:
          powbSynthesisSectionValidator.validateCrossCuttingDimensions(this, phase);
          break;
        case STAFFING:
          powbSynthesisSectionValidator.validateCrpStaffing(this, phase);
          break;
        case FINANCIAL_PLAN:
          powbSynthesisSectionValidator.validateFinancialPlan(this, phase);
          break;
        case COLABORATION_INTEGRATION:
          powbSynthesisSectionValidator.validateColaborationIntegration(this, phase);
          break;
        case MEL:
          powbSynthesisSectionValidator.validateMEL(this, phase);
          break;
        case MANAGEMENT_RISK:
          powbSynthesisSectionValidator.validateManagementRisk(this, phase);
          break;
        case MANAGEMENT_GOVERNANCE:
          powbSynthesisSectionValidator.validateManagementGovernance(this, phase);
          break;
        default:
          break;
      }

    }

    return SUCCESS;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }


  public Map<String, Object> getSection() {
    return section;
  }

  public String getSectionName() {
    return sectionName;
  }

  public SectionStatus getSectionStatus() {
    return sectionStatus;
  }

  public boolean isExistPowbSynthesis() {
    return existPowbSynthesis;
  }

  // Validators


  public boolean isValidSection() {
    return validSection;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    sectionName = StringUtils.trim(parameters.get(APConstants.SECTION_NAME).getMultipleValues()[0]);

    // Validate if the section exists.
    validSection = PowbSynthesisSectionStatusEnum.value(sectionName) != null;

  }

  public void setExistPowbSynthesis(boolean existPowbSynthesis) {
    this.existPowbSynthesis = existPowbSynthesis;
  }


  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setSection(Map<String, Object> section) {
    this.section = section;
  }

  public void setSectionName(String sectionName) {
    this.sectionName = sectionName;
  }

  public void setSectionStatus(SectionStatus sectionStatus) {
    this.sectionStatus = sectionStatus;
  }


  public void setValidSection(boolean validSection) {
    this.validSection = validSection;
  }

}
