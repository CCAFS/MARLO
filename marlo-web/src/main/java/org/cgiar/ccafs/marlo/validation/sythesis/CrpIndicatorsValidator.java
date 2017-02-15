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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpIndicatorReport;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpIndicatorsValidator extends BaseValidator {

  BaseAction action;

  @Inject
  private CrpManager crpManager;

  @Inject
  public CrpIndicatorsValidator() {
    // TODO Auto-generated constructor stub
  }

  private Path getAutoSaveFilePath(IpLiaisonInstitution ipLiaisonInstitution, long crpID) {
    Crp crp = crpManager.getCrpById(crpID);
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
    for (CrpIndicatorReport crpIndicatorReport : indicatorReports) {
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
        ProjectSectionStatusEnum.SYNTHESISMOG.getStatus());
    } else {
      this.saveMissingFields(ipLiaisonInstitution, APConstants.PLANNING, action.getPlanningYear(),
        ProjectSectionStatusEnum.SYNTHESISMOG.getStatus());
    }


  }


}
