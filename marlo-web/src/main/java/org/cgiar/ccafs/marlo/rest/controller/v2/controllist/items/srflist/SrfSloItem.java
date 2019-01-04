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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.srflist;

import org.cgiar.ccafs.marlo.data.manager.SrfSloManager;
import org.cgiar.ccafs.marlo.data.model.SrfSlo;
import org.cgiar.ccafs.marlo.rest.dto.SrfSloDTO;
import org.cgiar.ccafs.marlo.rest.mappers.SrfSloMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class SrfSloItem<T> {

  private SrfSloManager srfSloManager;
  private SrfSloMapper srfSloMapper;

  public SrfSloItem(SrfSloManager srfSloManager, SrfSloMapper srfSloMapper) {
    this.srfSloManager = srfSloManager;
    this.srfSloMapper = srfSloMapper;
  }

  /**
   * Find a SRF-SLO requesting a MARLO Id
   * 
   * @param id
   * @return a SrfSloDTO with the SRL-SLO data.
   */
  public ResponseEntity<SrfSloDTO> findSrfSlobyId(Long id) {
    SrfSlo srfSlo = srfSloManager.getSrfSloById(id);
    return Optional.ofNullable(srfSlo).map(srfSloMapper::SrfSloToSrfSloDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the SRF-SLO items *
   * 
   * @return a List of SrfSloDTO with all SRF-SLO Items.
   */
  public List<SrfSloDTO> getAllSrfSlos() {
    if (srfSloManager.findAll() != null) {
      List<SrfSlo> srfSlos = new ArrayList<>(srfSloManager.findAll());
      List<SrfSloDTO> srfSloDTOs =
        srfSlos.stream().map(srfSloEntity -> srfSloMapper.SrfSloToSrfSloDTO(srfSloEntity)).collect(Collectors.toList());
      return srfSloDTOs;
    } else {
      return null;
    }
  }


}
