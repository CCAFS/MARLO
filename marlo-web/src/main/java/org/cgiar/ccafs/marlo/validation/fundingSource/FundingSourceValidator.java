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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.FileDB;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
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

@Named
public class FundingSourceValidator extends BaseValidator {

  // This is not thread safe.
  private boolean hasErros;

  // This is not thread safe.
  BaseAction action;

  @Inject
  private GlobalUnitManager crpManager;
  @Inject
  private InstitutionManager institutionManager;

  @Inject
  public FundingSourceValidator(GlobalUnitManager crpManager, InstitutionManager institutionManager) {
    this.crpManager = crpManager;
    this.institutionManager = institutionManager;
  }

  /**
   * Until I work out why the File is being set to an empty file instead of null, this will temporarily
   * fix the problem.
   * 
   * @param fundingSource
   */
  private void checkFileIsValid(FundingSource fundingSource) {
    FileDB file = fundingSource.getFile();
    if (file != null) {

      if (file.getId() == null) {
        // The UI component has instantiated an empty file object instead of null.
        fundingSource.setFile(null);

      }
    }

  }

  private Path getAutoSaveFilePath(FundingSource fundingSource, long crpID) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
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

    this.checkFileIsValid(fundingSource);

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

    // Validate the direct donor with id -1, beacause front end send this when there is not one selected

    if (fundingSource.getDirectDonor() == null || fundingSource.getDirectDonor().getId() == null
      || fundingSource.getDirectDonor().getId().longValue() == -1) {
      this.addMessage(action.getText("fundingSource.directDonor.id"));
      action.getInvalidFields().put("input-fundingSource.directDonor.id", InvalidFieldsMessages.EMPTYFIELD);
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


    /**
     * Validate Grant Amount only if the Funding source is Synced.
     * If budgets are larger than the total amount, the funding source is pending for validation.
     * A message is sent to the user indicating that there is something to modify. *
     * 
     * @author Julián Rodríguez CCAFS/CIAT
     * @date 23/08/2017
     * @update Added null field validation when you calculate de currentBudget
     * @author Julián Rodríguez CCAFS/CIAT
     * @date 25/08/2017
     * @update Exclude the validation to W1W2
     * @author Julián Rodríguez CCAFS/CIAT
     * @date 06/09/2017
     * @update Remove the validation to W1W2 *
     * @author Julián Rodríguez
     * @date 03/10/2017
     */


    if (fundingSource.getSynced() != null) {
      if (fundingSource.getSynced()) {

        // if (fundingSource.getBudgetType().getId() != APConstants.BUDGET_TYPE) {

        Double grantAmount = fundingSource.getGrantAmount();
        List<FundingSourceBudget> budgets = fundingSource.getBudgets();
        double currentBudget = 0;

        for (FundingSourceBudget fundingSourceBudget : budgets) {
          if (fundingSourceBudget.getBudget() != null) {
            currentBudget += fundingSourceBudget.getBudget();
          }

        }

        if (currentBudget > grantAmount) {


          for (int i = 0; i < budgets.size(); i++) {
            this.addMessage(action.getText("fundingSource.budgetWrongValue"));
            action.getInvalidFields().put("input-fundingSource.budgets[" + i + "].budget",
              InvalidFieldsMessages.WRONGVALUE);

          }

        }


        // }


      }
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
