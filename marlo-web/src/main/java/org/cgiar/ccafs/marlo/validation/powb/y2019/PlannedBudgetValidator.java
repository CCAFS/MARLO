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

package org.cgiar.ccafs.marlo.validation.powb.y2019;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialPlannedBudget;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis2019SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;

import com.google.zxing.common.detector.MathUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class PlannedBudgetValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;

  public PlannedBudgetValidator(GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  /**
   * POWB 2019 calculate Word limits to Flagships
   * 
   * @param crpID
   * @param maxlimit
   * @return
   */
  public int flagshipLimitWords(long crpID, int maxlimit) {

    int iFlagShips = this.getFlagshipNnumbers(crpID);

    int maxNumber = MathUtils.round(((maxlimit / (iFlagShips - 1) * 2)));

    return maxNumber;

  }

  private Path getAutoSaveFilePath(PowbSynthesis powbSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = powbSynthesis.getClass().getSimpleName();
    String actionFile = PowbSynthesis2019SectionStatusEnum.PLANNED_BUDGET.getStatus().replace("/", "_");
    String autoSaveFile = powbSynthesis.getId() + "_" + composedClassName + "_" + baseAction.getActualPhase().getName()
      + "_" + baseAction.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  /**
   * Get the # of Flagships in this CRP
   * 
   * @param crpID
   * @return
   */
  public int getFlagshipNnumbers(long crpID) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    // Get the list of liaison institutions Flagships and PMU.
    List<LiaisonInstitution> liaisonInstitutions = crp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
    return liaisonInstitutions.size();
  }

  public boolean isPMU(LiaisonInstitution liaisonInstitution) {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;
  }

  public void validate(BaseAction action, PowbSynthesis powbSynthesis, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(powbSynthesis, action.getCrpID(), action);
      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }


    if (powbSynthesis != null) {


      if (this.isPMU(powbSynthesis.getLiaisonInstitution())) {
        if (!(this.isValidString(powbSynthesis.getFinancialPlan().getFinancialPlanIssues()))
          && this.wordCount(this.removeHtmlTags(powbSynthesis.getFinancialPlan().getFinancialPlanIssues())) <= 500) {
          action.addMessage(action.getText("powbSynthesis.financialPlan.highlight.readText"));
          action.getInvalidFields().put("input-powbSynthesis.financialPlan.financialPlanIssues",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      } else {
        if (!(this.isValidString(powbSynthesis.getFinancialPlan().getFinancialPlanIssues()))
          && this.wordCount(this.removeHtmlTags(powbSynthesis.getFinancialPlan().getFinancialPlanIssues())) <= (this
            .flagshipLimitWords(action.getCrpID(), 500))) {
          action.addMessage(action.getText("powbSynthesis.financialPlan.highlight.readText"));
          action.getInvalidFields().put("input-powbSynthesis.financialPlan.financialPlanIssues",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }


      if (this.isPMU(powbSynthesis.getLiaisonInstitution())) {
        int i = 0;
        for (PowbFinancialPlannedBudget powbFinancialPlannedBudget : powbSynthesis
          .getPowbFinancialPlannedBudgetList()) {
          this.validateFinancialPlannedBudget(powbFinancialPlannedBudget, action, i);
          i++;
        }
        i = 0;
      }

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(powbSynthesis, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
        action.getActualPhase().getUpkeep(), PowbSynthesis2019SectionStatusEnum.PLANNED_BUDGET.getStatus(), action);
    }

  }


  private void validateFinancialPlannedBudget(PowbFinancialPlannedBudget powbFinancialPlannedBudget, BaseAction action,
    int i) {

    if (powbFinancialPlannedBudget.getW1w2() != null && powbFinancialPlannedBudget.getW1w2() < 0) {
      action.addMissingField(action.getText("financialPlan.tableE.w1w2"));
      action.getInvalidFields().put("input-powbSynthesis.powbFinancialPlannedBudgetList[" + i + "].w1w2",
        InvalidFieldsMessages.INVALID_NUMBER);
    }
    if (powbFinancialPlannedBudget.getW3Bilateral() != null && powbFinancialPlannedBudget.getW3Bilateral() < 0) {
      action.addMissingField(action.getText("financialPlan.tableE.w3bilateral"));
      action.getInvalidFields().put("input-powbSynthesis.powbFinancialPlannedBudgetList[" + i + "].w3Bilateral",
        InvalidFieldsMessages.INVALID_NUMBER);
    }
    if (!(this.isValidString(powbFinancialPlannedBudget.getComments()))) {
      // action.addMessage(action.getText("financialPlan.tableE.comments"));
      // action.getInvalidFields().put("input-powbSynthesis.powbFinancialPlannedBudgetList[" + i + "].comments",
      // InvalidFieldsMessages.EMPTYFIELD);
    }
  }

}
