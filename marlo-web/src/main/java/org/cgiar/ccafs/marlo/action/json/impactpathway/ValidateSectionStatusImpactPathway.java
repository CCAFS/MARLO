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


package org.cgiar.ccafs.marlo.action.json.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.SectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.impactpathway.ClusterActivitiesValidator;
import org.cgiar.ccafs.marlo.validation.impactpathway.OutcomeValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class validates that all the fields are filled in a specific section of a project or crp program.
 * 
 * @author Christian Garcia
 */
public class ValidateSectionStatusImpactPathway extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 6703347614114120597L;
  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ValidateSectionStatusImpactPathway.class);

  private SectionStatus sectionStatus;
  @Inject
  private SectionStatusManager sectionStatusManager;
  @Inject
  private CrpProgramManager crpProgramManager;
  long crpProgramID;
  private boolean existCrpProgram;
  private boolean validSection;
  private String sectionName;

  private Map<String, Object> section;

  // Validators
  @Inject
  private OutcomeValidator outcomesValidator;


  @Inject
  private ClusterActivitiesValidator clusterActivitiesValidator;


  @Inject
  public ValidateSectionStatusImpactPathway(APConfig config) {
    super(config);

  }

  @Override
  public String execute() throws Exception {
    if (existCrpProgram && validSection) {
      // sectionName = sectionName.toLowerCase();
      // getting the current section status.
      switch (SectionStatusEnum.getValue(sectionName)) {
        case OUTCOMES:
          this.validateOutcomes();
          break;
        case CLUSTERACTIVITES:
          this.validateClusterOfActivites();
          break;


      }

    }
    sectionStatus = sectionStatusManager.getSectionStatusByCrpProgam(crpProgramID, sectionName);
    section = new HashMap<String, Object>();
    section.put("sectionName", sectionStatus.getSectionName());
    section.put("missingFields", sectionStatus.getMissingFields());
    Thread.sleep(500);
    return SUCCESS;
  }

  public Map<String, Object> getSection() {
    return section;
  }


  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();

    // Validating parameters.
    // sectionName = StringUtils.trim(((String[]) parameters.get(APConstants.SECTION_NAME))[0]);
    sectionName = StringUtils.trim(parameters.get(APConstants.SECTION_NAME).getMultipleValues()[0]);

    crpProgramID = -1;

    try {
      // crpProgramID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.CRP_PROGRAM_ID))[0]));
      crpProgramID =
        Long.parseLong(StringUtils.trim(parameters.get(APConstants.CRP_PROGRAM_ID).getMultipleValues()[0]));
    } catch (Exception e) {
      LOG.error("There was an exception trying to parse the crp program id = {} ",
        // StringUtils.trim(((String[]) parameters.get(APConstants.CRP_PROGRAM_ID))[0]));
        StringUtils.trim(parameters.get(APConstants.CRP_PROGRAM_ID).getMultipleValues()[0]));

    }

    // Validate if project exists.
    existCrpProgram = crpProgramManager.existCrpProgram(crpProgramID);

    // Validate if the section exists.
    List<String> sections = new ArrayList<>();
    sections.add("outcomes");
    sections.add("clusterActivities");

    validSection = sections.contains(sectionName);

  }

  public void setSection(Map<String, Object> section) {
    this.section = section;
  }


  private void validateClusterOfActivites() {
    // Getting information.
    CrpProgram crpProgram = crpProgramManager.getCrpProgramById(crpProgramID);
    if (crpProgram != null) {

      crpProgram.setClusterofActivities(
        crpProgram.getCrpClusterOfActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getClusterofActivities()) {

        crpClusterOfActivity.setLeaders(crpClusterOfActivity.getCrpClusterActivityLeaders().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList()));

        crpClusterOfActivity.setKeyOutputs(crpClusterOfActivity.getCrpClusterKeyOutputs().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList()));
        for (CrpClusterKeyOutput crpClusterKeyOutput : crpClusterOfActivity.getKeyOutputs()) {
          crpClusterKeyOutput.setKeyOutputOutcomes(crpClusterKeyOutput.getCrpClusterKeyOutputOutcomes().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList()));
        }
      }
    }


    // Validate.
    clusterActivitiesValidator.validate(this, crpProgram.getClusterofActivities(), crpProgram, false);

  }


  private void validateOutcomes() {
    // Getting information.
    CrpProgram crpProgram = crpProgramManager.getCrpProgramById(crpProgramID);
    if (crpProgram != null) {

      crpProgram.setOutcomes(
        crpProgram.getCrpProgramOutcomes().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      for (CrpProgramOutcome crpProgramOutcome : crpProgram.getOutcomes()) {
        crpProgramOutcome.setMilestones(
          crpProgramOutcome.getCrpMilestones().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        crpProgramOutcome.setSubIdos(
          crpProgramOutcome.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

        if (crpProgramOutcome.getSubIdos() != null) {
          for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcome.getSubIdos()) {
            crpOutcomeSubIdo.setAssumptions(
              crpOutcomeSubIdo.getCrpAssumptions().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
          }
        }
      }

    }


    // Validate.
    outcomesValidator.validate(this, crpProgram.getOutcomes(), crpProgram, false);

  }
}
