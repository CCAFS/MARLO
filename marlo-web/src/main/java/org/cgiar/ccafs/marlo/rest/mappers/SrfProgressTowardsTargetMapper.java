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

package org.cgiar.ccafs.marlo.rest.mappers;

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetCases;
import org.cgiar.ccafs.marlo.rest.dto.SrfProgressTowardsTargetDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 * @author Diego F. Perez - CIAT/CCAFS
 **************/
@Mapper(componentModel = "jsr330", uses = {SrfSloIndicatorTargetMapper.class, FlagshipProgramMapper.class,
  GeographicScopeMapper.class, LocationMapper.class})
public interface SrfProgressTowardsTargetMapper {

  @Mappings({@Mapping(source = "briefSummary", target = "briefSummary"),
    @Mapping(source = "srfSloIndicatorTarget", target = "srfSloTarget"),
    @Mapping(source = "geographicScopes", target = "geographicScope"),
    @Mapping(source = "geographicRegions", target = "regions"),
    @Mapping(source = "geographicCountries", target = "countries")})
  public abstract SrfProgressTowardsTargetDTO reportSynthesisSrfProgressCasesTargetToSrfProgressTowardsTargetsDTO(
    ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTargetCases);

  // @Mappings({@Mapping(source = "birefSummary", target = "briefSummary"),
  // @Mapping(source = "srfSloIndicatorTarget", target = "srfSloTarget")})
  // public abstract ReportSynthesisSrfProgressTarget srfProgressTowardsTargetsDTOToReportSynthesisSrfProgressTarget(
  // SrfProgressTowardsTargetsDTO srfProgressTowardsTargetsDTO);

}
