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

import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewParticipantsCapDevDTO;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ParticipantsCapDevItem<T> {

  // Managers and mappers

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


    return reportSynCrossCutDimId;
  }

}
