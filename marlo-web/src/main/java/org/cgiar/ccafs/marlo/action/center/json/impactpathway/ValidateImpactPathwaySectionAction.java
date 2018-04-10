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

package org.cgiar.ccafs.marlo.action.center.json.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterSectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterSectionStatus;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.ImpactPathwaySectionsEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.center.impactpathway.CenterImpactPathwaySectionValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class ValidateImpactPathwaySectionAction extends BaseAction {


  private static final long serialVersionUID = 9053048564427813253L;


  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ValidateImpactPathwaySectionAction.class);


  // Managers
  private CrpProgramManager programServcie;


  private ICenterSectionStatusManager sectionStatusService;

  // Parameters
  private boolean existProgram;
  private boolean validSection;
  private String sectionName;
  private long crpProgramID;
  private Map<String, Object> section;
  // Model
  private CenterSectionStatus sectionStatus;
  private final CenterImpactPathwaySectionValidation<ValidateImpactPathwaySectionAction> centerImpactPathwaySectionValidation;

  @Inject
  public ValidateImpactPathwaySectionAction(APConfig config, CrpProgramManager programServcie,
    ICenterSectionStatusManager sectionStatusService,
    CenterImpactPathwaySectionValidation<ValidateImpactPathwaySectionAction> centerImpactPathwaySectionValidation) {
    super(config);
    this.programServcie = programServcie;
    this.sectionStatusService = sectionStatusService;
    this.centerImpactPathwaySectionValidation = centerImpactPathwaySectionValidation;
  }


  @Override
  public String execute() throws Exception {
    if (existProgram && validSection) {
      switch (ImpactPathwaySectionsEnum.getValue(sectionName)) {
        case PROGRAM_IMPACT:
          this.centerImpactPathwaySectionValidation.validateImpact(this, crpProgramID);
          break;
        case TOPIC:
          this.centerImpactPathwaySectionValidation.validateTopic(this, crpProgramID);
          break;
        case OUTCOMES:
          this.centerImpactPathwaySectionValidation.validateOutcome(this, crpProgramID);
          break;
        case OUTPUTS:
          this.centerImpactPathwaySectionValidation.validateOutput(this, crpProgramID);
          break;
      }

    }

    CrpProgram program = programServcie.getCrpProgramById(crpProgramID);

    switch (ImpactPathwaySectionsEnum.getValue(sectionName)) {
      case OUTCOMES:
        section = new HashMap<String, Object>();
        section.put("sectionName", sectionName);
        section.put("missingFields", "");

        if (program != null) {
          List<CenterTopic> topics = new ArrayList<>(
            program.getResearchTopics().stream().filter(rt -> rt.isActive()).collect(Collectors.toList()));
          if (topics != null && !topics.isEmpty()) {
            for (CenterTopic researchTopic : topics) {
              List<CenterOutcome> outcomes = new ArrayList<>(
                researchTopic.getResearchOutcomes().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));
              if (outcomes != null && !outcomes.isEmpty()) {
                for (CenterOutcome researchOutcome : outcomes) {
                  sectionStatus = sectionStatusService.getSectionStatusByOutcome(program.getId(),
                    researchOutcome.getId(), sectionName, this.getActualPhase().getYear());

                  if (sectionStatus == null) {
                    sectionStatus = new CenterSectionStatus();
                    sectionStatus.setMissingFields("No section");
                  }
                  if (sectionStatus.getMissingFields().length() > 0) {
                    section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());
                  }
                }
              } else {
                sectionStatus = new CenterSectionStatus();
                sectionStatus.setMissingFields("No otucomes");
                section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());
              }
            }
          } else {
            sectionStatus = new CenterSectionStatus();
            sectionStatus.setMissingFields("No research topics");
            section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());
          }
        }


        break;
      case OUTPUTS:
        section = new HashMap<String, Object>();
        section.put("sectionName", sectionName);
        section.put("missingFields", "");
        if (program != null) {
          List<CenterOutput> outputs = new ArrayList<>(
            program.getCenterOutputs().stream().filter(co -> co.isActive()).collect(Collectors.toList()));

          if (outputs != null && !outputs.isEmpty()) {
            for (CenterOutput researchOutput : outputs) {
              sectionStatus = sectionStatusService.getSectionStatusByOutput(program.getId(), researchOutput.getId(),
                sectionName, this.getActualPhase().getYear());

              if (sectionStatus == null) {
                sectionStatus = new CenterSectionStatus();
                sectionStatus.setMissingFields("No section");
              }
              if (sectionStatus.getMissingFields().length() > 0) {
                section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());
              }
            }
          } else {
            sectionStatus = new CenterSectionStatus();
            sectionStatus.setMissingFields("No outputs");
            section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());
          }
        }

        break;
      default:
        sectionStatus =
          sectionStatusService.getSectionStatusByProgram(crpProgramID, sectionName, this.getActualPhase().getYear());
        section = new HashMap<String, Object>();
        section.put("sectionName", sectionStatus.getSectionName());
        section.put("missingFields", sectionStatus.getMissingFields());
        break;

    }
    return SUCCESS;
  }

  public long getCrpProgramID() {
    return crpProgramID;
  }


  public Map<String, Object> getSection() {
    return section;
  }


  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();
    // sectionName = StringUtils.trim(((String[]) parameters.get(APConstants.SECTION_NAME))[0]);
    sectionName = StringUtils.trim(parameters.get(APConstants.SECTION_NAME).getMultipleValues()[0]);
    crpProgramID = -1;

    try {
      // crpProgramID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.CENTER_PROGRAM_ID))[0]));
      crpProgramID =
        Long.parseLong(StringUtils.trim(parameters.get(APConstants.CRP_PROGRAM_ID).getMultipleValues()[0]));
    } catch (Exception e) {
      LOG.error("There was an exception trying to parse the crp program id = {} ",
        // StringUtils.trim(((String[]) parameters.get(APConstants.CENTER_PROGRAM_ID))[0]));
        StringUtils.trim(parameters.get(APConstants.CRP_PROGRAM_ID).getMultipleValues()[0]));
    }

    existProgram = programServcie.existCrpProgram(crpProgramID);

    // Validate if the section exists.
    List<String> sections = new ArrayList<>();
    sections.add("programimpacts");
    sections.add("researchTopics");
    sections.add("outcomesList");
    sections.add("outputsList");

    validSection = sections.contains(sectionName);
  }


  public void setCrpProgramID(long crpProgramID) {
    this.crpProgramID = crpProgramID;
  }

  public void setSection(Map<String, Object> section) {
    this.section = section;
  }

}
