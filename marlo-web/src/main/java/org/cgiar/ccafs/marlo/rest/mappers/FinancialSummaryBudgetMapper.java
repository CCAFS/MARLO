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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.mappers;

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummaryBudget;
import org.cgiar.ccafs.marlo.rest.dto.FinancialSummaryBudgetAreaDTO;
import org.cgiar.ccafs.marlo.rest.dto.FinancialSummaryBudgetDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330", uses = GlobalUnitMapper.class)
public interface FinancialSummaryBudgetMapper {

  @Mappings({@Mapping(source = "expenditureArea.id", target = "expenditureAreaID"),
    @Mapping(source = "expenditureArea.expenditureArea", target = "expenditureAreaName"),
    @Mapping(source = "w1Planned", target = "plannedBudgetW1W2"),
    @Mapping(source = "bilateralPlanned", target = "plannedBudgetW3Bilateral"),
    @Mapping(source = "w1Actual", target = "actualExpenditureW1W2"),
    @Mapping(source = "bilateralActual", target = "actualExpendituretW3Bilateral")})
  public abstract FinancialSummaryBudgetAreaDTO reportSynthesisFinancialSummaryBudgetToFinancialSummaryBudgetAreaDTO(
    ReportSynthesisFinancialSummaryBudget financialSumaryBudget);

  @Mappings({@Mapping(source = "liaisonInstitution.crpProgram.smoCode", target = "flagshipID"),
    @Mapping(source = "liaisonInstitution.crpProgram.name", target = "flagshipName"),
    @Mapping(source = "w1Planned", target = "plannedBudgetW1W2"),
    @Mapping(source = "bilateralPlanned", target = "plannedBudgetW3Bilateral"),
    @Mapping(source = "w1Actual", target = "actualExpenditureW1W2"),
    @Mapping(source = "bilateralActual", target = "actualExpendituretW3Bilateral")})
  public abstract FinancialSummaryBudgetDTO reportSynthesisFinancialSummaryBudgetToFinancialSummaryBudgetDTO(
    ReportSynthesisFinancialSummaryBudget financialSumaryBudget);


}
