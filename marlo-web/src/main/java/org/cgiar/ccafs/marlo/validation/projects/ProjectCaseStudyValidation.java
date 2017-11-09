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

package org.cgiar.ccafs.marlo.validation.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.CaseStudy;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectCaseStudyValidation extends BaseValidator {

  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  @Inject
  public ProjectCaseStudyValidation(GlobalUnitManager crpManager) {
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(Project project, long crpID) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.CASESTUDIES.getStatus().replace("/", "_");
    String autoSaveFile =
      project.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, Project project, CaseStudy caseStudy, boolean saving) {

    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(project, action.getCrpID());

      if (path.toFile().exists()) {
        this.addMissingField("draft");
      }
    }
    if (project != null) {

      if (!(this.isValidString(caseStudy.getTitle()) && this.wordCount(caseStudy.getTitle()) <= 15)) {
        this.addMessage("Case Study #" + caseStudy.getId() + ": Title");
        action.getInvalidFields().put("input-caseStudy.title", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!(this.isValidString(caseStudy.getOutcomeStatement())
        && this.wordCount(caseStudy.getOutcomeStatement()) <= 80)) {
        this.addMessage("Case Study #" + caseStudy.getId() + ": Outcome Statement");
        action.getInvalidFields().put("input-caseStudy.outcomeStatement", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!(this.isValidString(caseStudy.getResearchOutputs())
        && this.wordCount(caseStudy.getResearchOutputs()) <= 150)) {
        this.addMessage("Case Study #" + caseStudy.getId() + ": Research Output");
        action.getInvalidFields().put("input-caseStudy.researchOutputs", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!(this.isValidString(caseStudy.getResearchPartners())
        && this.wordCount(caseStudy.getResearchPartners()) <= 150)) {
        this.addMessage("Case Study #" + caseStudy.getId() + ": Research Partners");
        action.getInvalidFields().put("input-caseStudy.researchPartners", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!(this.isValidString(caseStudy.getResearchPartners())
        && this.wordCount(caseStudy.getResearchPartners()) <= 150)) {
        this.addMessage("Case Study #" + caseStudy.getId() + ": Research Partners");
        action.getInvalidFields().put("input-caseStudy.researchPartners", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!(this.isValidString(caseStudy.getActivities()) && this.wordCount(caseStudy.getActivities()) <= 150)) {
        this.addMessage("Case Study #" + caseStudy.getId() + ": Activities");
        action.getInvalidFields().put("input-caseStudy.activities", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!(this.isValidString(caseStudy.getNonResearchPartneres())
        && this.wordCount(caseStudy.getNonResearchPartneres()) <= 80)) {
        this.addMessage("Case Study #" + caseStudy.getId() + ": Non R Partners");
        action.getInvalidFields().put("input-caseStudy.nonResearchPartneres", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!(this.isValidString(caseStudy.getOutputUsers()) && this.wordCount(caseStudy.getOutputUsers()) <= 50)) {
        this.addMessage("Case Study #" + caseStudy.getId() + ": Output User");
        action.getInvalidFields().put("input-caseStudy.outputUsers", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!(this.isValidString(caseStudy.getOutputUsed()) && this.wordCount(caseStudy.getOutputUsed()) <= 50)) {
        this.addMessage("Case Study #" + caseStudy.getId() + ": Output Used");
        action.getInvalidFields().put("input-caseStudy.outputUsed", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!(this.isValidString(caseStudy.getOutputUsed()) && this.wordCount(caseStudy.getOutputUsed()) <= 50)) {
        this.addMessage("Case Study #" + caseStudy.getId() + ": Output Used");
        action.getInvalidFields().put("input-caseStudy.outputUsed", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!(this.isValidString(caseStudy.getEvidenceOutcome())
        && this.wordCount(caseStudy.getEvidenceOutcome()) <= 50)) {
        this.addMessage("Case Study #" + caseStudy.getId() + ": Evidence Outcome");
        action.getInvalidFields().put("input-caseStudy.evidenceOutcome", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!(this.isValidString(caseStudy.getReferencesCase())
        && this.wordCount(caseStudy.getReferencesCase()) <= 150)) {
        this.addMessage("Case Study #" + caseStudy.getId() + ": References");
        action.getInvalidFields().put("input-caseStudy.referencesCase", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (caseStudy.getCaseStudyIndicatorsIds() != null) {
        if (caseStudy.getCaseStudyIndicatorsIds().isEmpty()) {
          this.addMessage(action.getText("reporting.caseStudy.caseStudyIndicatorsIds").toLowerCase());

          action.getInvalidFields().put("input-caseStudy.caseStudyIndicatorsIds", InvalidFieldsMessages.EMPTYFIELD);
        }
      } else {
        this.addMessage(action.getText("reporting.caseStudy.caseStudyIndicatorsIds").toLowerCase());

        action.getInvalidFields().put("input-caseStudy.caseStudyIndicatorsIds", InvalidFieldsMessages.EMPTYFIELD);
      }


    }
    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }

    if (action.isReportingActive()) {
      this.saveMissingFields(project, caseStudy, APConstants.REPORTING, action.getReportingYear(),
        ProjectSectionStatusEnum.CASESTUDIES.getStatus());
    }
  }


}
