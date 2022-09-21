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

import org.cgiar.ccafs.marlo.rest.dto.EndOfInitiativeOutcomeDTO;
import org.cgiar.ccafs.marlo.rest.dto.InitiativeOutcomeDTO;
import org.cgiar.ccafs.marlo.rest.services.submissionTools.eoi.EndOfInitiativeOutcome;
import org.cgiar.ccafs.marlo.rest.services.submissionTools.eoi.InitiativeOutcome;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Mapper(componentModel = "jsr330")
public interface EOIOutcomeMapper {

  @Mappings({@Mapping(source = "eoi_o", target = "initiativeOutcomes"),
    @Mapping(source = "initiative_id", target = "initiativeId"),
    @Mapping(source = "initiative_name", target = "initiativeName"),
    @Mapping(source = "initiative_official_code", target = "initiativeOfficialCode"),
    @Mapping(source = "stage_name", target = "initiativeStageName"),})
  public abstract EndOfInitiativeOutcomeDTO
    endOfInitiativeOutcomeToEndOfInitiativeOutcomeDTO(EndOfInitiativeOutcome eoio);

  public abstract List<InitiativeOutcomeDTO>
    initiativeOutcomeListToInitiativeOutcomeDTOList(List<InitiativeOutcome> ios);

  @Mappings({@Mapping(source = "outcome_statement", target = "eoiOutcomeStatement"),
    @Mapping(source = "short_title", target = "eoiShortTitle"),
    @Mapping(source = "toc_result_id", target = "eoiOutcomeId")})
  public abstract InitiativeOutcomeDTO initiativeOutcomeToInitiativeOutcomeDTO(InitiativeOutcome io);
}
