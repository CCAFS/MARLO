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
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class FundingSourceValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;

  private final InstitutionManager institutionManager;

  @Inject
  public FundingSourceValidator(GlobalUnitManager crpManager, InstitutionManager institutionManager) {
    this.crpManager = crpManager;
    this.institutionManager = institutionManager;
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

  public void validate(BaseAction action, FundingSource fundingSource, boolean saving) {
    if (fundingSource.getFundingSourceInfo().getBudgetType() != null
      && fundingSource.getFundingSourceInfo().getBudgetType().getId() == null) {
      fundingSource.getFundingSourceInfo().setBudgetType(null);
    }
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(fundingSource, action.getCrpID());

      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }

    if (!this.isValidString(fundingSource.getFundingSourceInfo().getTitle())) {
      action.addMessage(action.getText("fundingSource.title"));
      action.getInvalidFields().put("input-fundingSource.fundingSourceInfo.title", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (fundingSource.getFundingSourceInfo().getStartDate() == null) {
      action.addMessage(action.getText("fundingSource.startDate"));
      action.getInvalidFields().put("input-fundingSource.fundingSourceInfo.startDate",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (fundingSource.getFundingSourceInfo().getEndDate() == null) {
      action.addMessage(action.getText("fundingSource.endDate"));
      action.getInvalidFields().put("input-fundingSource.fundingSourceInfo.endDate", InvalidFieldsMessages.EMPTYFIELD);
    }

    Calendar cal = Calendar.getInstance();

    if (fundingSource.getFundingSourceInfo().getEndDate() != null
      && fundingSource.getFundingSourceInfo().getStatus() != null) {
      cal.setTime(fundingSource.getFundingSourceInfo().getEndDate());
      if (fundingSource.getFundingSourceInfo().getStatus().longValue() == Long
        .parseLong(ProjectStatusEnum.Ongoing.getStatusId())
        && action.getActualPhase().getYear() > cal.get(Calendar.YEAR)) {
        action.addMessage(action.getText("fundingSource.endDate"));
        action.getInvalidFields().put("input-fundingSource.fundingSourceInfo.endDate",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      if (fundingSource.getFundingSourceInfo().getStatus().longValue() == Long
        .parseLong(ProjectStatusEnum.Ongoing.getStatusId())) {
        action.addMessage(action.getText("fundingSource.endDate"));
        action.getInvalidFields().put("input-fundingSource.fundingSourceInfo.endDate",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }


    if (fundingSource.getFundingSourceInfo().getExtensionDate() != null
      && fundingSource.getFundingSourceInfo().getStatus() != null) {
      cal.setTime(fundingSource.getFundingSourceInfo().getExtensionDate());
      if (fundingSource.getFundingSourceInfo().getStatus().longValue() == Long
        .parseLong(ProjectStatusEnum.Extended.getStatusId())
        && action.getActualPhase().getYear() > cal.get(Calendar.YEAR)) {
        action.addMessage(action.getText("fundingSource.extensionDate"));
        action.getInvalidFields().put("input-fundingSource.fundingSourceInfo.extensionDate",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      if (fundingSource.getFundingSourceInfo().getStatus().longValue() == Long
        .parseLong(ProjectStatusEnum.Extended.getStatusId())) {
        action.addMessage(action.getText("fundingSource.extensionDate"));
        action.getInvalidFields().put("input-fundingSource.fundingSourceInfo.extensionDate",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }
    // Validate the donor with id -1, beacause front end send this when there is not one selected
    if (fundingSource.getFundingSourceInfo().getDirectDonor() == null
      || fundingSource.getFundingSourceInfo().getDirectDonor().getId() == null
      || fundingSource.getFundingSourceInfo().getDirectDonor().getId().longValue() == -1) {
      action.addMessage(action.getText("fundingSource.institution.id"));
      action.getInvalidFields().put("input-fundingSource.fundingSourceInfo.directDonor.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (!this.isValidString(fundingSource.getFundingSourceInfo().getContactPersonName())) {
      action.addMessage(action.getText("fundingSource.contactPersonName"));
      action.getInvalidFields().put("input-fundingSource.fundingSourceInfo.contactPersonName",
        InvalidFieldsMessages.EMPTYFIELD);
    }


    if (this.hasIFPRI(fundingSource)) {
      if (action.hasSpecificities(APConstants.CRP_DIVISION_FS)) {
        if (fundingSource.getFundingSourceInfo().getPartnerDivision() == null) {
          action.addMessage(action.getText("fundingSource.division"));
          action.getInvalidFields().put("input-fundingSource.fundingSourceInfo.partnerDivision.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
        if (fundingSource.getFundingSourceInfo().getPartnerDivision() != null) {
          if (fundingSource.getFundingSourceInfo().getPartnerDivision().getId() == null) {
            action.addMessage(action.getText("fundingSource.division"));
            action.getInvalidFields().put("input-fundingSource.fundingSourceInfo.partnerDivision.id",
              InvalidFieldsMessages.EMPTYFIELD);
          } else {
            if (fundingSource.getFundingSourceInfo().getPartnerDivision().getId().longValue() == -1) {
              action.addMessage(action.getText("fundingSource.division"));
              action.getInvalidFields().put("input-fundingSource.fundingSourceInfo.partnerDivision.id",
                InvalidFieldsMessages.EMPTYFIELD);
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


    if (fundingSource.getFundingSourceInfo().getSynced() != null) {
      if (fundingSource.getFundingSourceInfo().getSynced()) {

        // if (fundingSource.getBudgetType().getId() != APConstants.BUDGET_TYPE) {

        Double grantAmount = fundingSource.getFundingSourceInfo().getGrantAmount();
        List<FundingSourceBudget> budgets = fundingSource.getBudgets();
        double currentBudget = 0;

        for (FundingSourceBudget fundingSourceBudget : budgets) {
          if (fundingSourceBudget.getBudget() != null) {
            currentBudget += fundingSourceBudget.getBudget();
          }

        }

        if (currentBudget > grantAmount) {


          for (int i = 0; i < budgets.size(); i++) {
            action.addMessage(action.getText("fundingSource.budgetWrongValue"));
            action.getInvalidFields().put("input-fundingSource.budgets[" + i + "].budget",
              InvalidFieldsMessages.WRONGVALUE);

          }

        }


        // }


      }
    }


    if (action.hasSpecificities(APConstants.CRP_EMAIL_FUNDING_SOURCE)) {
      if (!this.isValidString(fundingSource.getFundingSourceInfo().getContactPersonEmail())) {
        action.addMessage(action.getText("fundingSource.contactPersonEmail"));
        action.getInvalidFields().put("input-fundingSource.fundingSourceInfo.contactPersonEmail",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    if (action.hasSpecificities(APConstants.CRP_HAS_RESEARCH_HUMAN)) {
      if (fundingSource.getFundingSourceInfo().isHasFileResearch()) {
        if (fundingSource.getFundingSourceInfo().getFileResearch() != null) {
          if (fundingSource.getFundingSourceInfo().getFileResearch().getId() == null
            || fundingSource.getFundingSourceInfo().getFileResearch().getId().longValue() == -1) {

            action.addMessage(action.getText("fundingSource.messageFileResearch"));
            action.getInvalidFields().put("list-fundingSource.fundingSourceInfo.fileResearch",
              action.getText("fundingSource.messageFileResearch"));

          }
        } else {
          action.addMessage(action.getText("fundingSource.messageFileResearch"));
          action.getInvalidFields().put("list-fundingSource.fundingSourceInfo.fileResearch",
            action.getText("fundingSource.messageFileResearch"));
        }
      }
    }


    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
      action.setCanEdit(true);
      action.setEditable(true);

    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(fundingSource, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
      ProjectSectionStatusEnum.FUNDINGSOURCE.getStatus(), action);


  }

}