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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis2019SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.powb.y2019.PowbSynthesisSection2019Validator;

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
public class ValidateSectionStatusPowbSynthesis2019Action extends BaseAction {


  private static final Logger LOG = LoggerFactory.getLogger(ValidateSectionStatusPowbSynthesis2019Action.class);


  private static final long serialVersionUID = 6393245587625917548L;

  private SectionStatusManager sectionStatusManager;
  private PowbSynthesisManager powbSynthesisManager;
  private GlobalUnitManager crpManager;
  private PhaseManager phaseManager;
  private CrpProgramManager crpProgramManager;

  private SectionStatus sectionStatus;
  private GlobalUnit loggedCrp;
  private boolean validSection;
  private String sectionName;
  private boolean existPowbSynthesis;
  private Map<String, Object> section;
  private final PowbSynthesisSection2019Validator<ValidateSectionStatusPowbSynthesis2019Action> powbSynthesisSectionValidator;
  private long phaseID;
  private long powbSynthesisID;

  @Inject
  public ValidateSectionStatusPowbSynthesis2019Action(APConfig config, SectionStatusManager sectionStatusManager,
    PowbSynthesisManager powbSynthesisManager, GlobalUnitManager crpManager,
    PowbSynthesisSection2019Validator<ValidateSectionStatusPowbSynthesis2019Action> powbSynthesisSectionValidator,
    PhaseManager phaseManager, CrpProgramManager crpProgramManager) {
    super(config);
    this.crpManager = crpManager;
    this.sectionStatusManager = sectionStatusManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.powbSynthesisSectionValidator = powbSynthesisSectionValidator;
    this.phaseManager = phaseManager;
    this.crpProgramManager = crpProgramManager;
  }

  @Override
  public String execute() throws Exception {
    Phase phase = phaseManager.getPhaseById(phaseID);
    PowbSynthesis powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
    if (validSection && existPowbSynthesis) {

      switch (PowbSynthesis2019SectionStatusEnum.value(sectionName.toUpperCase())) {
        case TOC:
          powbSynthesisSectionValidator.validateTocAdjustments(this, powbSynthesis);
          break;
        case PROGRAM_CHANGES:
          powbSynthesisSectionValidator.validateProgramChanges(this, powbSynthesis);
          break;
        case PROGRESS_OUTCOMES:
          powbSynthesisSectionValidator.validateCrpProgress(this, powbSynthesis);
          break;
        case PLANNED_STUDIES:
          powbSynthesisSectionValidator.validatePlannedStudies(this, powbSynthesis);
          break;
        case PLANNED_COLLABORATIONS:
          powbSynthesisSectionValidator.validatePlannedColaboration(this, powbSynthesis);
          break;
        case PLANNED_BUDGET:
          powbSynthesisSectionValidator.validatePlannedBudget(this, powbSynthesis);
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


    switch (PowbSynthesis2019SectionStatusEnum.value(sectionName.toUpperCase())) {

      default:
        sectionStatus = sectionStatusManager.getSectionStatusByPowbSynthesis(powbSynthesis.getId(), cycle,
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
    powbSynthesisID =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.POWB_SYNTHESIS_ID).getMultipleValues()[0]));
    // Validate if the section exists.
    validSection = PowbSynthesis2019SectionStatusEnum.value(sectionName) != null;

    existPowbSynthesis = powbSynthesisManager.existPowbSynthesis(powbSynthesisID);

  }

  public void setSection(Map<String, Object> section) {
    this.section = section;
  }

  public void setSectionName(String sectionName) {
    this.sectionName = sectionName;
  }


}
