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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.crossCgiarCollabs;

import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.CrossCGIARCollaborationDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewCrossCGIARCollaborationDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class CrossCGIARCollaborationsItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(CrossCGIARCollaborationsItem.class);

  private GlobalUnitManager globalUnitManager;
  private PhaseManager phaseManager;
  private CrpProgramManager crpProgramManager;

  @Inject
  public CrossCGIARCollaborationsItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager) {
    super();
    this.globalUnitManager = globalUnitManager;
    this.phaseManager = phaseManager;
    this.crpProgramManager = crpProgramManager;
  }

  public Long createCrossCGIARCollaboration(NewCrossCGIARCollaborationDTO newCrossCGIARCollaborationDTO,
    String entityAcronym, User user) {
    Long crossCGIARCollaborationID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createCrossCGIARCollaboration", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }

    Phase phase = phaseManager.findAll().stream()
      .filter(p -> p.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && p.getYear() == newCrossCGIARCollaborationDTO.getPhase().getYear()
        && p.getName().equalsIgnoreCase(newCrossCGIARCollaborationDTO.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createCrossCGIARCollaboration", "Phase",
        newCrossCGIARCollaborationDTO.getPhase().getYear() + " is an invalid year."));
    }

    if (fieldErrors.isEmpty()) {
      Set<GlobalUnit> collaborationCrps = new HashSet<>();

      // start GlobalUnit
      if (newCrossCGIARCollaborationDTO.getCollaborationCrpIds() != null
        && !newCrossCGIARCollaborationDTO.getCollaborationCrpIds().isEmpty()) {

      }
      // end GlobalUnit
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return crossCGIARCollaborationID;
  }

  public ResponseEntity<CrossCGIARCollaborationDTO> deleteCrossCGIARCollaborationById(Long id,
    String CGIARentityAcronym, Integer repoYear, String repoPhase, User user) {

    // TODO implement DELETE

    return null;
    // return Optional.ofNullable(keyPartnershipExternal)
    // .map(this.keyExternalPartnershipMapper::keyPartnershipExternalToKeyExternalPartnershipDTO)
    // .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public List<CrossCGIARCollaborationDTO> findAllCrossCGIARCollaborationsByGlobalUnit(String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    List<CrossCGIARCollaborationDTO> crossCGIARCollaborations = new ArrayList<>();

    // TODO implement GET All

    return crossCGIARCollaborations;
  }

  public ResponseEntity<CrossCGIARCollaborationDTO> findCrossCGIARCollaborationById(Long id, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {

    // TODO implement GET

    return null;
    // return Optional.ofNullable(keyExternalPartnership)
    // .map(this.keyExternalPartnershipMapper::keyPartnershipExternalToKeyExternalPartnershipDTO)
    // .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public Long putCrossCGIARCollaborationById(Long idCrossCGIARCollaboration,
    NewCrossCGIARCollaborationDTO newCrossCGIARCollaborationDTO, String CGIARentityAcronym, User user) {
    Long crossCGIARCollaborationDB = null;
    // TODO implement PUT
    return crossCGIARCollaborationDB;
  }

}
