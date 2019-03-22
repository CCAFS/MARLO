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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists;

import org.cgiar.ccafs.marlo.data.manager.RepIndPhaseResearchPartnershipManager;
import org.cgiar.ccafs.marlo.data.model.RepIndPhaseResearchPartnership;
import org.cgiar.ccafs.marlo.rest.dto.ResearchPartnershipDTO;
import org.cgiar.ccafs.marlo.rest.mappers.ResearchPartnershipMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class ResearchPartnershipItem<T> {


  private RepIndPhaseResearchPartnershipManager repIndPhaseResearchPartnershipManager;
  private ResearchPartnershipMapper researchPartnershipsMapper;

  @Inject
  public ResearchPartnershipItem(RepIndPhaseResearchPartnershipManager repIndPhaseResearchPartnershipManager,
    ResearchPartnershipMapper researchPartnershipsMapper) {
    this.repIndPhaseResearchPartnershipManager = repIndPhaseResearchPartnershipManager;
    this.researchPartnershipsMapper = researchPartnershipsMapper;
  }

  /**
   * Find a Research Partnership requesting a MARLO id
   * 
   * @param id
   * @return a ResearchPartnershipDTO with the Research Partnership data.
   */
  public ResponseEntity<ResearchPartnershipDTO> findResearchPartnershipById(Long id) {
    RepIndPhaseResearchPartnership repIndPhaseResearchPartnership =
      this.repIndPhaseResearchPartnershipManager.getRepIndPhaseResearchPartnershipById(id);
    return Optional.ofNullable(repIndPhaseResearchPartnership)
      .map(this.researchPartnershipsMapper::repIndPhaseResearchPartnershipToResearchPartnershipsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }


  /**
   * Get All the Innovation Types Items *
   * 
   * @return a List of InnovationTypesDTO with all repIndInnovationType Items.
   */
  public List<ResearchPartnershipDTO> getAllResearchPartnerships() {
    if (this.repIndPhaseResearchPartnershipManager.findAll() != null) {
      List<RepIndPhaseResearchPartnership> repIndPhaseResearchPartnerships =
        new ArrayList<>(this.repIndPhaseResearchPartnershipManager.findAll());
      List<ResearchPartnershipDTO> researchPartnershipsDTOs = repIndPhaseResearchPartnerships.stream()
        .map(repIndPhaseResearchPartnershipEntity -> this.researchPartnershipsMapper
          .repIndPhaseResearchPartnershipToResearchPartnershipsDTO(repIndPhaseResearchPartnershipEntity))
        .collect(Collectors.toList());
      return researchPartnershipsDTOs;
    } else {
      return null;
    }
  }


}
