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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.RepIndPhaseResearchPartnership;

import java.util.List;


public interface RepIndPhaseResearchPartnershipDAO {

  /**
   * This method removes a specific repIndPhaseResearchPartnership value from the database.
   * 
   * @param repIndPhaseResearchPartnershipId is the repIndPhaseResearchPartnership identifier.
   * @return true if the repIndPhaseResearchPartnership was successfully deleted, false otherwise.
   */
  public void deleteRepIndPhaseResearchPartnership(long repIndPhaseResearchPartnershipId);

  /**
   * This method validate if the repIndPhaseResearchPartnership identify with the given id exists in the system.
   * 
   * @param repIndPhaseResearchPartnershipID is a repIndPhaseResearchPartnership identifier.
   * @return true if the repIndPhaseResearchPartnership exists, false otherwise.
   */
  public boolean existRepIndPhaseResearchPartnership(long repIndPhaseResearchPartnershipID);

  /**
   * This method gets a repIndPhaseResearchPartnership object by a given repIndPhaseResearchPartnership identifier.
   * 
   * @param repIndPhaseResearchPartnershipID is the repIndPhaseResearchPartnership identifier.
   * @return a RepIndPhaseResearchPartnership object.
   */
  public RepIndPhaseResearchPartnership find(long id);

  /**
   * This method gets a list of repIndPhaseResearchPartnership that are active
   * 
   * @return a list from RepIndPhaseResearchPartnership null if no exist records
   */
  public List<RepIndPhaseResearchPartnership> findAll();


  /**
   * This method saves the information of the given repIndPhaseResearchPartnership
   * 
   * @param repIndPhaseResearchPartnership - is the repIndPhaseResearchPartnership object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndPhaseResearchPartnership was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndPhaseResearchPartnership save(RepIndPhaseResearchPartnership repIndPhaseResearchPartnership);
}
