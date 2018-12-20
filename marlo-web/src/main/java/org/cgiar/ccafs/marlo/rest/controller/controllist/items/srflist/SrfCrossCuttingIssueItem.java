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

package org.cgiar.ccafs.marlo.rest.controller.controllist.items.srflist;

import org.cgiar.ccafs.marlo.data.manager.SrfCrossCuttingIssueManager;
import org.cgiar.ccafs.marlo.data.model.SrfCrossCuttingIssue;
import org.cgiar.ccafs.marlo.rest.dto.SrfCrossCuttingIssueDTO;
import org.cgiar.ccafs.marlo.rest.mappers.SrfCrossCuttingIssueMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class SrfCrossCuttingIssueItem<T> {

  // Managers
  private SrfCrossCuttingIssueManager srfCrossCuttingIssueManager;

  // Mappers
  private SrfCrossCuttingIssueMapper srfCrossCuttingIssueMapper;

  @Inject
  public SrfCrossCuttingIssueItem(SrfCrossCuttingIssueManager srfCrossCuttingIssueManager,
    SrfCrossCuttingIssueMapper srfCrossCuttingIssueMapper) {
    this.srfCrossCuttingIssueManager = srfCrossCuttingIssueManager;
    this.srfCrossCuttingIssueMapper = srfCrossCuttingIssueMapper;
  }

  /**
   * Find a SRF Cross Cutting Issue requesting a MARLO Id
   * 
   * @param id
   * @return a SrfCrossCuttingIssueDTO with the SRL-SLO data.
   */
  public ResponseEntity<SrfCrossCuttingIssueDTO> findSrfCrossCuttingIssuebyId(Long id) {
    SrfCrossCuttingIssue srfCrossCuttingIssue = srfCrossCuttingIssueManager.getSrfCrossCuttingIssueById(id);
    return Optional.ofNullable(srfCrossCuttingIssue)
      .map(srfCrossCuttingIssueMapper::srfCrossCuttingIssueToSrfCrossCuttingIssueDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the SRF Cross Cutting Issue items *
   * 
   * @return a List of SrfCrossCuttingIssueDTO with all SRF-SLO Items.
   */
  public List<SrfCrossCuttingIssueDTO> getAllSrfCrossCuttingIssues() {
    if (srfCrossCuttingIssueManager.findAll() != null) {
      List<SrfCrossCuttingIssue> srfCrossCuttingIssues = new ArrayList<>(srfCrossCuttingIssueManager.findAll());
      List<SrfCrossCuttingIssueDTO> srfCrossCuttingIssuesDTOs =
        srfCrossCuttingIssues.stream().map(srfCrossCuttingIssueEntity -> srfCrossCuttingIssueMapper
          .srfCrossCuttingIssueToSrfCrossCuttingIssueDTO(srfCrossCuttingIssueEntity)).collect(Collectors.toList());
      return srfCrossCuttingIssuesDTOs;
    } else {
      return null;
    }
  }

}
