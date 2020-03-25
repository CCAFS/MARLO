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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;
import org.cgiar.ccafs.marlo.rest.dto.ParticipantsCapDevDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
@Mapper(componentModel = "jsr330")
public interface ReportSynthesisCrossCuttingDimensionMapper {

  @Mappings({@Mapping(source = "reportSynthesisCrossCuttingDimension.id", target = "id"),
    @Mapping(source = "reportSynthesisCrossCuttingDimension.traineesLongTermFemale", target = "traineesLongTermFemale"),
    @Mapping(source = "reportSynthesisCrossCuttingDimension.traineesLongTermMale", target = "traineesLongTermMale"),
    @Mapping(source = "reportSynthesisCrossCuttingDimension.traineesShortTermFemale",
      target = "traineesShortTermFemale"),
    @Mapping(source = "reportSynthesisCrossCuttingDimension.traineesShortTermMale", target = "traineesShortTermMale"),
    @Mapping(source = "reportSynthesisCrossCuttingDimension.traineesPhdMale", target = "traineesPhdMale"),
    @Mapping(source = "reportSynthesisCrossCuttingDimension.traineesPhdFemale", target = "traineesPhdFemale"),
    @Mapping(source = "reportSynthesisCrossCuttingDimension.reportSynthesis.phase.name", target = "phase.name"),
    @Mapping(source = "reportSynthesisCrossCuttingDimension.reportSynthesis.phase.year", target = "phase.year")})
  public abstract ParticipantsCapDevDTO reportSynthesisCrossCuttingDimensionToParticipantsCapDevDTO(
    ReportSynthesisCrossCuttingDimension reportSynthesisCrossCuttingDimension);
}
