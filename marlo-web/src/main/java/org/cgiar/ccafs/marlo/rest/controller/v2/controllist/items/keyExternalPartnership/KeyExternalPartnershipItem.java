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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.keyExternalPartnership;

import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewKeyExternalPartnershipDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

@Named
public class KeyExternalPartnershipItem<T> {

  private GlobalUnitManager globalUnitManager;
  private PhaseManager phaseManager;
  private CrpProgramManager crpProgramManager;

  public KeyExternalPartnershipItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager) {
    this.globalUnitManager = globalUnitManager;
    this.phaseManager = phaseManager;
    this.crpProgramManager = crpProgramManager;
  }

  public Long createKeyExternalPartnership(NewKeyExternalPartnershipDTO newKeyExternalPartnershipDTO,
    String entityAcronym, User user) {
    Long keyExternalPartnershipID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == newKeyExternalPartnershipDTO.getPhase().getYear()
        && c.getName().equalsIgnoreCase(newKeyExternalPartnershipDTO.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createPolicy", "phase",
        newKeyExternalPartnershipDTO.getPhase().getYear() + " is an invalid year"));
    }
    if (fieldErrors.size() == 0) {
      if (newKeyExternalPartnershipDTO.getFlagshipProgram() != null) {
        CrpProgram crpProgram = crpProgramManager
          .getCrpProgramById(Long.valueOf(newKeyExternalPartnershipDTO.getFlagshipProgram().getCode()));
        if (crpProgram == null) {
          fieldErrors.add(new FieldErrorDTO("KeyExternalPartnership", "crpProgram",
            newKeyExternalPartnershipDTO.getFlagshipProgram().getCode() + " is an invalid flagship code"));
        }
      } else {

      }

    }

    return keyExternalPartnershipID;
  }
}
