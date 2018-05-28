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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.FileDB;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Named;


/**
 * @author Christian David Garcia Oviedo. - CIAT/CCAFS
 */
@Named
public class ProjectHighLightValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private BaseAction baseAction;


  public ProjectHighLightValidator(GlobalUnitManager crpManager) {
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
    FileDB file = projectHighlight.getProjectHighlightInfo(baseAction.getActualPhase()).getFile();
    if (file != null) {
      if (file.getFileName() == null && file.getTokenId() == null) {
        // The UI component has instantiated an empty file object instead of null.
        projectHighlight.getProjectHighlightInfo(baseAction.getActualPhase()).setFile(null);
      }
    }

  }

  private Path getAutoSaveFilePath(ProjectHighlight highLigths, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = highLigths.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.HIGHLIGHT.getStatus().replace("/", "_");
    String autoSaveFile = highLigths.getId() + "_" + composedClassName + "_" + action.getActualPhase().getDescription()
      + "_" + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public void validate(BaseAction action, Project project, ProjectHighlight highLigths, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    baseAction = action;
    if (!saving) {
      Path path = this.getAutoSaveFilePath(highLigths, action.getCrpID(), action);

      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }
    this.checkFileIsValid(highLigths);

    this.ValidateHightLigth(action, highLigths);
    this.ValidateHightAuthor(action, highLigths);
    this.ValidateHightTitle(action, highLigths);
    this.ValidateYear(action, highLigths);


    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(project, highLigths, action.getActualPhase().getDescription(),
      action.getActualPhase().getYear(), ProjectSectionStatusEnum.HIGHLIGHTS.getStatus(), action);

  }

  private void ValidateHightAuthor(BaseAction action, ProjectHighlight higligth) {

    if (!this.isValidString(higligth.getProjectHighlightInfo(baseAction.getActualPhase()).getAuthor())) {
      action.addMessage("Author");
      action.addMissingField("reporting.projectHighligth.author");
      action.getInvalidFields().put("input-highlight.projectHighlightInfo.author", InvalidFieldsMessages.EMPTYFIELD);
    }
  }

  private void ValidateHightLigth(BaseAction action, ProjectHighlight higligth) {

    if (higligth.getTypesids().size() == 0) {
      action.addMessage(action.getText("reporting.projectHighligth.types").toLowerCase());
      action.addMissingField("reporting.projectHighligth.types");
      action.getInvalidFields().put("input-highlight.typesids", InvalidFieldsMessages.EMPTYFIELD);
    }


  }

  private void ValidateHightTitle(BaseAction action, ProjectHighlight higligth) {

    if (!this.isValidString(higligth.getProjectHighlightInfo(baseAction.getActualPhase()).getTitle())) {
      action.addMessage(action.getText("Title"));
      action.addMissingField("reporting.projectHighligth.title");
      action.getInvalidFields().put("input-highlight.projectHighlightInfo.title", InvalidFieldsMessages.EMPTYFIELD);
    }
  }


  private void ValidateYear(BaseAction action, ProjectHighlight higligth) {

    if (!(higligth.getProjectHighlightInfo(baseAction.getActualPhase()).getYear() > 0)) {
      action.addMessage("Year");
      action.addMissingField("reporting.projectHighligth.year");
      action.getInvalidFields().put("input-highlight.projectHighlightInfo.year", InvalidFieldsMessages.EMPTYFIELD);

    }
  }
}
