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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;
import org.cgiar.ccafs.marlo.rest.dto.StatusPlannedOutcomesDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330",
  uses = {CrpProgramMapper.class, OutcomeMapper.class, StatusPlannedMilestonesMapper.class, SrfSubIdoMapper.class})
public interface StatusPlannedOutcomesMapper {

  @Mappings({@Mapping(source = "crpProgramOutcome", target = "outcome"),
    @Mapping(source = "crpProgramOutcome.crpProgram", target = "crpProgram"),
    @Mapping(source = "crpProgramOutcome.phase", target = "phase"), @Mapping(source = "summary", target = "summary"),
    @Mapping(source = "crpProgramOutcome.subIdos", target = "subIdos"),
    @Mapping(source = "milestones", target = "milestones")})
  public abstract StatusPlannedOutcomesDTO reportSynthesisFlagshipProgressOutcomeToStatusPlannedOutcomesDTO(
    ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome);

}
