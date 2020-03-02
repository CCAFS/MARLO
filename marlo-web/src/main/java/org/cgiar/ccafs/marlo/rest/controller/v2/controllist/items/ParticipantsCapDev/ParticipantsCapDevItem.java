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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.ParticipantsCapDev;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewParticipantsCapDevDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ParticipantsCapDevItem<T> {

  // Managers and mappers
  private static final String ACRONYM_PMU = "PMU";
  private GlobalUnitManager globalUnitManager;
  private PhaseManager phaseManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;

  // Variables
  // private List<FieldErrorDTO> fieldErrors;
  // private ProjectInnovation projectInnovation;
  // private long innovationID;


  @Inject
  public ParticipantsCapDevItem() {

  }

  /**
   * Create a new ParticipantsCapDev
   * 
   * @param newParticipantsCapDevDTO all Participants CapDev data
   * @param CGIAR entity acronym who is requesting
   * @param year of reporting
   * @param Logged user on system
   * @return innovation id created
   */
  public Long createParticipantsCapDev(NewParticipantsCapDevDTO newParticipantsCapDevDTO, String entityAcronym,
    User user) {

    // TODO: Add the save to history
    // TODO: Include all data validations
    // TODO: return an innovationDTO
    Long reportSynCrossCutDimId = new Long(1);
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);

    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createParticipantsCapDev", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }

    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == newParticipantsCapDevDTO.getPhase().getYear()
        && c.getName().equalsIgnoreCase(newParticipantsCapDevDTO.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createParticipantsCapDev", "phase",
        new NewParticipantsCapDevDTO().getPhase().getYear() + " is an invalid year"));
    }

    LiaisonInstitution liaisonInstitution =
      this.liaisonInstitutionManager.findByAcronymAndCrp(ACRONYM_PMU, globalUnitEntity.getId());

    if (liaisonInstitution == null) {
      fieldErrors
        .add(new FieldErrorDTO("createParticipantsCapDev", "LiaisonInstitution", "invalid liaison institution"));
    }


    return reportSynCrossCutDimId;
  }

}
