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

import org.cgiar.ccafs.marlo.data.model.ActionAreaOutcome;
import org.cgiar.ccafs.marlo.data.model.ActionAreaOutcomeIndicator;
import org.cgiar.ccafs.marlo.rest.dto.ActionAreaOutcomeDTO;
import org.cgiar.ccafs.marlo.rest.dto.ActionAreaOutcomeIndicatorDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330")
public interface ActionAreaOutcomesMapper {

  @Mappings({@Mapping(source = "actionAreaOutcome.actionArea.id", target = "actionAreaId"),
    @Mapping(source = "actionAreaOutcome.actionArea.name", target = "actionAreaName"),
    @Mapping(source = "actionAreaOutcome.outcomeStatement", target = "outcomeStatement"),
    @Mapping(source = "actionAreaOutcome.id", target = "outcomeId"),
    @Mapping(source = "actionAreaOutcome.smoCode", target = "outcomeSMOcode"),
    @Mapping(source = "outcomeIndicator.id", target = "outcomeIndicatorId"),
    @Mapping(source = "outcomeIndicator.smoCode", target = "outcomeIndicatorSMOcode"),
    @Mapping(source = "outcomeIndicator.outcomeIndicatorStatement", target = "outcomeIndicatorStatement")})
  public abstract ActionAreaOutcomeIndicatorDTO
    actionAreaOutcomeIndicatorToActionAreaOutcomeIndicatorDTO(ActionAreaOutcomeIndicator actionAreaOutcomeIndicator);


  @Mappings({@Mapping(source = "actionArea.id", target = "actionAreaId"),
    @Mapping(source = "actionArea.name", target = "actionAreaName"), @Mapping(source = "id", target = "outcomeId")})
  public abstract ActionAreaOutcomeDTO ActionAreaOutcomesToActionAreaOutcomeDTO(ActionAreaOutcome actionAreaOutcome);
}
