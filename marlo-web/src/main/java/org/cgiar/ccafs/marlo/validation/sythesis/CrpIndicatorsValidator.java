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

package org.cgiar.ccafs.marlo.validation.sythesis;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.CrpIndicatorReport;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class CrpIndicatorsValidator extends BaseValidator {

  // This is not thread safe
  BaseAction action;

  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  @Inject
  public CrpIndicatorsValidator(GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(IpLiaisonInstitution ipLiaisonInstitution, long crpID) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = ipLiaisonInstitution.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.CRP_INDICATORS.getStatus().replace("/", "_");
    String autoSaveFile =
      ipLiaisonInstitution.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, List<CrpIndicatorReport> indicatorReports,
    IpLiaisonInstitution ipLiaisonInstitution, boolean saving) {

    action.setInvalidFields(new HashMap<>());
    this.action = action;

    if (!saving) {
      Path path = this.getAutoSaveFilePath(ipLiaisonInstitution, action.getCrpID());

      if (path.toFile().exists()) {
        this.addMissingField("draft");
      }
    }


    int index = 0;
    for (CrpIndicatorReport crpIndicatorReport : indicatorReports.stream()
      .filter(c -> c.getYear() == action.getCurrentCycleYear()).collect(Collectors.toList())) {
      try {
        if (crpIndicatorReport.getActual() == null || Double.parseDouble(crpIndicatorReport.getActual()) < 0) {

          this.addMessage("crpIndicatorReport.validator.target");
          action.getInvalidFields().put("input-currentLiaisonInstitution.indicatorReports[" + index + "].actual",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      } catch (Exception e) {
        this.addMessage("crpIndicatorReport.validator.target");

        action.getInvalidFields().put("input-currentLiaisonInstitution.indicatorReports[" + index + "].actual",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      try {
        if (crpIndicatorReport.getNextTarget() == null || Double.parseDouble(crpIndicatorReport.getNextTarget()) < 0) {
          this.addMessage("crpIndicatorReport.validator.nextTarget");
          action.getInvalidFields().put("input-currentLiaisonInstitution.indicatorReports[" + index + "].nextTarget",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      } catch (Exception e) {
        this.addMessage("crpIndicatorReport.validator.nextTarget");
        action.getInvalidFields().put("input-currentLiaisonInstitution.indicatorReports[" + index + "].nextTarget",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      index++;
    }

    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }
    if (action.isReportingActive()) {
      this.saveMissingFields(ipLiaisonInstitution, APConstants.REPORTING, action.getReportingYear(),
        ProjectSectionStatusEnum.CRP_INDICATORS.getStatus());
    } else {
      this.saveMissingFields(ipLiaisonInstitution, APConstants.PLANNING, action.getPlanningYear(),
        ProjectSectionStatusEnum.CRP_INDICATORS.getStatus());
    }


  }


}
