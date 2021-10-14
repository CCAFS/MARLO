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
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ProjectImpacts;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Luis Benavides -CIAT/CCAFS
 */
@Named
public class ProjectImpactsValidator extends BaseValidator {

	@Inject
	// GlobalUnit Manager
	private GlobalUnitManager crpManager;

	@Inject
	public ProjectImpactsValidator() {

	}

	private Path getAutoSaveFilePath(ProjectImpacts projectImpacts, long crpID, BaseAction action) {
		GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
		String composedClassName = projectImpacts.getClass().getSimpleName();
		String actionFile = ProjectSectionStatusEnum.IMPACTS.getStatus().replace("/", "_");
		String autoSaveFile = projectImpacts.getId() + "_" + composedClassName + "_" + action.getActualPhase().getName()
				+ "_" + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

		return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
	}

	public void validate(BaseAction action, ProjectImpacts projectImpacts, boolean saving) {
		action.setInvalidFields(new HashMap<>());

		if (!saving) {
			Path path = this.getAutoSaveFilePath(projectImpacts, action.getCrpID(), action);

			if (path.toFile().exists()) {
				action.addMissingField("draft");
			}
		}

		if (action.hasSpecificities(APConstants.CRP_COVID_REQUIRED)) {
			this.validateImpacts(action, projectImpacts);
		}

		if (!action.getFieldErrors().isEmpty()) {
			action.addActionError(action.getText("saving.fields.required"));
		} else if (action.getValidationMessage().length() > 0) {
			action.addActionMessage(" " + action.getText("saving.missingFields",
					new String[] { action.getValidationMessage().toString() }));
		}

		this.saveMissingFields(projectImpacts, action.getActualPhase().getDescription(),
				action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
				ProjectSectionStatusEnum.IMPACTS.getStatus(), action);
	}

	private void validateImpacts(BaseAction action, ProjectImpacts projectImpacts) {
		if (!(this.isValidString(projectImpacts.getAnswer()) && this.wordCount(projectImpacts.getAnswer()) <= 300)) {
			action.addMessage(action.getText("projects.impacts.covid19.answer"));
			action.getInvalidFields().put("input-projects.impacts.covid19.answer", InvalidFieldsMessages.EMPTYFIELD);
		}

		if (projectImpacts.getProjectImpactCategoryId() != null) {
			if (projectImpacts.getProjectImpactCategoryId() == -1) {
				action.addMessage(action.getText("projects.impacts.covid19CategoryTitle"));
				action.getInvalidFields().put("input-actualProjectImpact.projectImpactCategoryId",
						InvalidFieldsMessages.EMPTYFIELD);
			} else if (projectImpacts.getProjectImpactCategoryId() == 3L && action.isSelectedPhaseAR2021()) {
				action.addMessage(action.getText("projects.impacts.covid19CategoryTitle"));
				action.getInvalidFields().put("input-actualProjectImpact.projectImpactCategoryId",
						InvalidFieldsMessages.WRONGVALUE);
			}
		} else {
			action.addMessage(action.getText("projects.impacts.covid19CategoryTitle"));
			action.getInvalidFields().put("input-actualProjectImpact.projectImpactCategoryId",
					InvalidFieldsMessages.EMPTYFIELD);
		}

	}

}
