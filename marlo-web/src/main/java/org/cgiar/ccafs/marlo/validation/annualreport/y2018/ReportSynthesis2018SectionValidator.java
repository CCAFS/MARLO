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

package org.cgiar.ccafs.marlo.validation.annualreport.y2018;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIntellectualAsset;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ReportSynthesis2018SectionValidator<T extends BaseAction> extends BaseValidator {

  private ReportSynthesisManager reportSynthesisManager;

  // Validations
  private final IntellectualAssetsValidator intellectualAssetsValidator;
  private final CCDimension2018Validator ccDimensionValidator;


  @Inject
  public ReportSynthesis2018SectionValidator(ReportSynthesisManager reportSynthesisManager,
    IntellectualAssetsValidator intellectualAssetsValidator, CCDimension2018Validator ccDimensionValidator) {
    super();
    this.reportSynthesisManager = reportSynthesisManager;
    this.intellectualAssetsValidator = intellectualAssetsValidator;
    this.ccDimensionValidator = ccDimensionValidator;
  }


  /**
   * Check if the Liaison Institution is PMU
   * 
   * @return true if liaisonInstitution exist and is PMU
   */
  public boolean isPMU(LiaisonInstitution liaisonInstitution) {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;
  }


  public void validateCrossCuttingDimensionValidator(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisCrossCuttingDimension() == null) {
      ReportSynthesisCrossCuttingDimension crossCuttingDimension = new ReportSynthesisCrossCuttingDimension();

      // create one to one relation
      reportSynthesis.setReportSynthesisCrossCuttingDimension(crossCuttingDimension);
      crossCuttingDimension.setReportSynthesis(reportSynthesis);

      ccDimensionValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      ccDimensionValidator.validate(action, reportSynthesis, false);
    }

  }

  public void validateIntellectualAssets(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisIntellectualAsset() == null) {
      ReportSynthesisIntellectualAsset intellectualAsset = new ReportSynthesisIntellectualAsset();

      // create one to one relation
      reportSynthesis.setReportSynthesisIntellectualAsset(intellectualAsset);
      intellectualAsset.setReportSynthesis(reportSynthesis);

      intellectualAssetsValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      intellectualAssetsValidator.validate(action, reportSynthesis, false);
    }

  }


}