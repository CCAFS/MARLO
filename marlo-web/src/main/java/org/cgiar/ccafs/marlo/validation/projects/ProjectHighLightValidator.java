/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.validation.projects;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.FileDB;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * @author Christian David Garcia Oviedo. - CIAT/CCAFS
 */
@Named
public class ProjectHighLightValidator extends BaseValidator {

  private final CrpManager crpManager;

  @Inject
  public ProjectHighLightValidator(CrpManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  /**
   * Until I work out why the File is being set to an empty file instead of null, this will temporarily
   * fix the problem.
   * 
   * @param projectHighlight
   */
  private void checkFileIsValid(ProjectHighlight projectHighlight) {
    FileDB file = projectHighlight.getFile();
    if (file != null) {
      if (file.getFileName() == null && file.getTokenId() == null) {
        // The UI component has instantiated an empty file object instead of null.
        projectHighlight.setFile(null);
      }
    }

  }

  private Path getAutoSaveFilePath(Project project, long crpID) {
    Crp crp = crpManager.getCrpById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.DESCRIPTION.getStatus().replace("/", "_");
    String autoSaveFile =
      project.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public void validate(BaseAction action, Project project, ProjectHighlight highLigths, boolean saving) {
    // BaseValidator does not Clean this variables.. so before validate the section, it be clear these variables
    this.missingFields.setLength(0);
    this.validationMessage.setLength(0);
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(project, action.getCrpID());

      if (path.toFile().exists()) {
        this.addMissingField("draft");
      }
    }
    this.checkFileIsValid(highLigths);

    this.ValidateHightLigth(action, highLigths);
    this.ValidateHightAuthor(action, highLigths);
    this.ValidateHightTitle(action, highLigths);
    this.ValidateYear(action, highLigths);


    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }

    if (action.isReportingActive()) {
      this.saveMissingFields(project, highLigths, APConstants.REPORTING, action.getReportingYear(),
        ProjectSectionStatusEnum.HIGHLIGHT.getStatus());
    } else {
      this.saveMissingFields(project, highLigths, APConstants.PLANNING, action.getPlanningYear(),
        ProjectSectionStatusEnum.HIGHLIGHT.getStatus());
    }

  }

  private void ValidateHightAuthor(BaseAction action, ProjectHighlight higligth) {

    if (!this.isValidString(higligth.getAuthor())) {
      this.addMessage("Author");
      this.addMissingField("reporting.projectHighligth.author");
      action.getInvalidFields().put("input-highlight.author", InvalidFieldsMessages.EMPTYFIELD);
    }
  }

  private void ValidateHightLigth(BaseAction action, ProjectHighlight higligth) {

    if (higligth.getTypesids().size() == 0) {
      this.addMessage(action.getText("reporting.projectHighligth.types").toLowerCase());
      this.addMissingField("reporting.projectHighligth.types");
      action.getInvalidFields().put("input-highlight.typesids", InvalidFieldsMessages.EMPTYFIELD);
    }


  }

  private void ValidateHightTitle(BaseAction action, ProjectHighlight higligth) {

    if (!this.isValidString(higligth.getTitle())) {
      this.addMessage(action.getText("Title"));
      this.addMissingField("reporting.projectHighligth.title");
      action.getInvalidFields().put("input-highlight.title", InvalidFieldsMessages.EMPTYFIELD);
    }
  }


  private void ValidateYear(BaseAction action, ProjectHighlight higligth) {

    if (!(higligth.getYear() > 0)) {
      this.addMessage("Year");
      this.addMissingField("reporting.projectHighligth.year");
      action.getInvalidFields().put("input-highlight.year", InvalidFieldsMessages.EMPTYFIELD);

    }
  }
}
