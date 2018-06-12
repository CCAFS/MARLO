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

package org.cgiar.ccafs.marlo.data.mapper;

import org.cgiar.ccafs.marlo.action.funding.dto.FundingSourceInfoSummary;
import org.cgiar.ccafs.marlo.action.funding.dto.FundingSourceInstitutionSummary;
import org.cgiar.ccafs.marlo.action.funding.dto.FundingSourceSummary;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper for mapping FundingSources to FundingSourceSummaries
 * 
 * @author GrantL
 */
@Mapper(componentModel = "jsr330")
public abstract class FundingSourceSummaryMapper {

  public Double fundingSourceBudgetsToFundingSourceBudgetPerPhase(Set<FundingSourceBudget> fundingSourceBudgets) {
    FundingSourceBudget fundingSourceBudget;
    if (fundingSourceBudgets != null && !fundingSourceBudgets.isEmpty()) {
      fundingSourceBudget = fundingSourceBudgets.iterator().next();
    } else {
      return 0.0;
    }

    return fundingSourceBudget.getBudget();

  }

  /**
   * Our HQL query will only return one FundingSourceInfo (the one that matches the Phase)
   * 
   * @param fundingSourceInfos
   * @return
   */
  public FundingSourceInfoSummary
    fundingSourceInfosToFundingSourceInfoSummary(Set<FundingSourceInfo> fundingSourceInfos) {
    FundingSourceInfo info;
    if (fundingSourceInfos != null && !fundingSourceInfos.isEmpty()) {
      info = fundingSourceInfos.iterator().next();
    } else {
      return null;
    }

    return this.fundingSourceInfoToFundingSourceInfoSummary(info);

  }


  @Mappings({@Mapping(source = "budgetType.name", target = "budgetTypeName"),
    @Mapping(source = "directDonor.composedNameLoc", target = "directDonorName"),
    @Mapping(source = "originalDonor.composedNameLoc", target = "originalDonorName"),})
  public abstract FundingSourceInfoSummary
    fundingSourceInfoToFundingSourceInfoSummary(FundingSourceInfo fundingSourceInfo);

  @Mappings({@Mapping(source = "institution.acronym", target = "acronym"),
    @Mapping(source = "institution.name", target = "name")})
  public abstract FundingSourceInstitutionSummary
    fundingSourceInstitutionToFundingSourceInsitutionSummary(FundingSourceInstitution fundingSourceInstitution);

  /**
   * This is the entry point to map the List of FundingSources to the List of FundingSourceSummaries
   * 
   * @param fundingSources
   * @return
   */
  public abstract List<FundingSourceSummary> fundingSourcesToFundingSourceSummaries(List<FundingSource> fundingSources);

  @Mappings({@Mapping(source = "fundingSourceInfos", target = "fundingSourceInfo"),
    @Mapping(source = "fundingSourceInstitutions", target = "institutions"),
    @Mapping(source = "sectionStatuses", target = "hasRequiredFields"),
    @Mapping(source = "fundingSourceBudgets", target = "fundingSourceBudgetPerPhase")})
  public abstract FundingSourceSummary fundingSourceToFundingSourceSummary(FundingSource fundingSource);


  public boolean sectionStatusesToHasRequiredFields(Set<SectionStatus> sectionStatuses) {
    SectionStatus sectionStatus;
    if (sectionStatuses != null && !sectionStatuses.isEmpty()) {
      sectionStatus = sectionStatuses.iterator().next();
    } else {
      return false;
    }

    if (sectionStatus.getMissingFields().length() == 0) {
      return true;
    }

    return false;

  }
}
