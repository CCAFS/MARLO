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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluation;
import org.cgiar.ccafs.marlo.rest.dto.RelevantEvaluationDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330", uses = {RelevantEvaluationActionMapper.class})
public interface RelevantEvaluationMapper {


  @Mappings({@Mapping(source = "evidences", target = "evidenceLink"),
    @Mapping(source = "managementResponse", target = "response"),
    @Mapping(source = "meliaEvaluationActions", target = "actionList")})
  public abstract RelevantEvaluationDTO reportSynthesisMeliaEvaluationToRelevantEvaluationDTO(
    ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation);

}
