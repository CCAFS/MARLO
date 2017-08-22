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


package org.cgiar.ccafs.marlo.validation.fundingSource;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class FundingSourceValidator extends BaseValidator {

  private boolean hasErros;

  BaseAction action;


  @Inject
  private CrpManager crpManager;
  @Inject
  private InstitutionManager institutionManager;

  @Inject
  private FundingSourceManager fundingSourceManager;

  private Path getAutoSaveFilePath(FundingSource fundingSource, long crpID) {
    Crp crp = crpManager.getCrpById(crpID);
    String composedClassName = fundingSource.getClass().getSimpleName();
    String actionFile = "fundingSource";
    String autoSaveFile =
      fundingSource.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public boolean hasIFPRI(FundingSource fundingSource) {


    if (fundingSource.getInstitutions() != null) {
      for (FundingSourceInstitution fundingSourceInstitution : fundingSource.getInstitutions().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {
        fundingSourceInstitution
          .setInstitution(institutionManager.getInstitutionById(fundingSourceInstitution.getInstitution().getId()));
        if (fundingSourceInstitution.getInstitution().getAcronym().equals("IFPRI")) {
          return true;
        }
      }
    }


    return false;
  }

  public boolean isHasErros() {
    return hasErros;
  }

  public void setHasErros(boolean hasErros) {
    this.hasErros = hasErros;
  }

  public void validate(BaseAction action, FundingSource fundingSource, boolean saving) {

    action.setInvalidFields(new HashMap<>());
    this.action = action;
    if (!saving) {
      Path path = this.getAutoSaveFilePath(fundingSource, action.getCrpID());

      if (path.toFile().exists()) {
        this.addMissingField("draft");
      }
    }

    if (!this.isValidString(fundingSource.getTitle())) {
      this.addMessage(action.getText("fundingSource.title"));
      action.getInvalidFields().put("input-fundingSource.title", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (fundingSource.getStartDate() == null) {
      this.addMessage(action.getText("fundingSource.startDate"));
      action.getInvalidFields().put("input-fundingSource.startDate", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (fundingSource.getEndDate() == null) {
      this.addMessage(action.getText("fundingSource.endDate"));
      action.getInvalidFields().put("input-fundingSource.endDate", InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate the donor with id -1, beacause front end send this when there is not one selected
    if (fundingSource.getInstitution() == null || fundingSource.getInstitution().getId() == null
      || fundingSource.getInstitution().getId().longValue() == -1) {
      this.addMessage(action.getText("fundingSource.institution.id"));
      action.getInvalidFields().put("input-fundingSource.institution.id", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (!this.isValidString(fundingSource.getContactPersonName())) {
      this.addMessage(action.getText("fundingSource.contactPersonName"));
      action.getInvalidFields().put("input-fundingSource.contactPersonName", InvalidFieldsMessages.EMPTYFIELD);
    }


    if (this.hasIFPRI(fundingSource)) {
      if (action.hasSpecificities(APConstants.CRP_DIVISION_FS)) {
        if (fundingSource.getPartnerDivision() == null) {
          this.addMessage(action.getText("fundingSource.division"));
          action.getInvalidFields().put("input-fundingSource.partnerDivision.id", InvalidFieldsMessages.EMPTYFIELD);
        }
        if (fundingSource.getPartnerDivision() != null) {
          if (fundingSource.getPartnerDivision().getId() == null) {
            this.addMessage(action.getText("fundingSource.division"));
            action.getInvalidFields().put("input-fundingSource.partnerDivision.id", InvalidFieldsMessages.EMPTYFIELD);
          } else {
            if (fundingSource.getPartnerDivision().getId().longValue() == -1) {
              this.addMessage(action.getText("fundingSource.division"));
              action.getInvalidFields().put("input-fundingSource.partnerDivision.id", InvalidFieldsMessages.EMPTYFIELD);
            }
          }

        }
      }
    }

    // Validate Grant Amount only if the Funding source is Synced
    if (fundingSource.getSynced()) {
      // TODO: Validate the budget amount do not exceed the grant total amount (fundingSource.getGrantAmount())
    }

    if (action.hasSpecificities(APConstants.CRP_EMAIL_FUNDING_SOURCE)) {
      if (!this.isValidString(fundingSource.getContactPersonEmail())) {
        this.addMessage(action.getText("fundingSource.contactPersonEmail"));
        action.getInvalidFields().put("input-fundingSource.contactPersonEmail", InvalidFieldsMessages.EMPTYFIELD);
      }
    }


    if (!action.getFieldErrors().isEmpty()) {
      hasErros = true;
      action.addActionError(action.getText("saving.fields.required"));
      action.setCanEdit(true);
      action.setEditable(true);

    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }

    this.saveMissingFields(fundingSource, null, null, ProjectSectionStatusEnum.FUNDINGSOURCE.getStatus());


  }

}
