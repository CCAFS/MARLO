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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestone;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestoneLink;
import org.cgiar.ccafs.marlo.rest.dto.EvidenceLinkDTO;
import org.cgiar.ccafs.marlo.rest.dto.StatusPlannedMilestonesDTO;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330", uses = {MilestoneMapper.class, CgiarCrossCuttingMarkersMapper.class,
  MilestoneStatusMapper.class, MilestoneReasonMapper.class})
public abstract class StatusPlannedMilestonesMapper {

  @AfterMapping
  protected void removeOtherReasonIfOther(@MappingTarget StatusPlannedMilestonesDTO statusPlannedMilestonesDTO) {
    if (statusPlannedMilestonesDTO.getReason() == null || (statusPlannedMilestonesDTO.getReason() != null
      && statusPlannedMilestonesDTO.getReason().getId().longValue() != 7)) {
      statusPlannedMilestonesDTO.setOtherReason(null);
    }
  }

  @Mappings({@Mapping(source = "crpMilestone", target = "milestone"),
    @Mapping(source = "markers", target = "crossCuttingMarkerList"),
    @Mapping(source = "links", target = "evidenceLinkList"),
    @Mapping(source = "milestonesStatus", target = "reportedStatus"),
    @Mapping(source = "extendedYear", target = "reportedExtendedYear")})
  public abstract StatusPlannedMilestonesDTO
    reportSynthesisFlagshipProgressOutcomeMilestoneToStatusPlannedMilestonesDTO(
      ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone);

  public abstract EvidenceLinkDTO
    ReportSynthesisOutcomesMilestonesLinkToEvidenceLinkDTO(ReportSynthesisFlagshipProgressOutcomeMilestoneLink link);
}

