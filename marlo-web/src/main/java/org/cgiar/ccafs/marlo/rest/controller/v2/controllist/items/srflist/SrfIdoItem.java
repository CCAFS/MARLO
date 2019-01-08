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

import org.cgiar.ccafs.marlo.data.manager.SrfIdoManager;
import org.cgiar.ccafs.marlo.data.model.SrfIdo;
import org.cgiar.ccafs.marlo.rest.dto.SrfIdoDTO;
import org.cgiar.ccafs.marlo.rest.mappers.SrfIdoMapper;

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
public class SrfIdoItem<T> {

  // Managers
  private SrfIdoManager srfIdoManager;

  // Mappers
  private SrfIdoMapper srfIdoMapper;

  @Inject
  public SrfIdoItem(SrfIdoManager srfIdoManager, SrfIdoMapper srfIdoMapper) {
    this.srfIdoManager = srfIdoManager;
    this.srfIdoMapper = srfIdoMapper;
  }

  /**
   * Find a SRF IDO requesting a MARLO Id
   * 
   * @param id
   * @return a SrfIdoDTO with the SRL-SLO data.
   */
  public ResponseEntity<SrfIdoDTO> findSrfIdobyId(Long id) {
    SrfIdo srfIdo = srfIdoManager.getSrfIdoById(id);
    return Optional.ofNullable(srfIdo).map(srfIdoMapper::srfIdoToSrfIdoDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the SRF IDO items *
   * 
   * @return a List of SrfIdoDTO with all SRF-SLO Items.
   */
  public List<SrfIdoDTO> getAllSrfIdos() {
    if (srfIdoManager.findAll() != null) {
      List<SrfIdo> srfIdos = new ArrayList<>(srfIdoManager.findAll());
      List<SrfIdoDTO> srfIdoDTOs =
        srfIdos.stream().map(srfIdoEntity -> srfIdoMapper.srfIdoToSrfIdoDTO(srfIdoEntity)).collect(Collectors.toList());
      return srfIdoDTOs;
    } else {
      return null;
    }
  }

}
