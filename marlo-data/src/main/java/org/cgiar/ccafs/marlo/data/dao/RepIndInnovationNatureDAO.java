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

import org.cgiar.ccafs.marlo.data.model.RepIndInnovationNature;

import java.util.List;


public interface RepIndInnovationNatureDAO {

  /**
   * This method removes a specific repIndInnovationNature value from the database.
   * 
   * @param repIndInnovationNatureId is the repIndInnovationNature identifier.
   * @return true if the repIndInnovationNature was successfully deleted, false otherwise.
   */
  public void deleteRepIndInnovationNature(long repIndInnovationNatureId);

  /**
   * This method validate if the repIndInnovationNature identify with the given id exists in the system.
   * 
   * @param repIndInnovationNatureID is a repIndInnovationNature identifier.
   * @return true if the repIndInnovationNature exists, false otherwise.
   */
  public boolean existRepIndInnovationNature(long repIndInnovationNatureID);

  /**
   * This method gets a repIndInnovationNature object by a given repIndInnovationNature identifier.
   * 
   * @param repIndInnovationNatureID is the repIndInnovationNature identifier.
   * @return a RepIndInnovationNature object.
   */
  public RepIndInnovationNature find(long id);

  /**
   * This method gets a list of repIndInnovationNature that are active
   * 
   * @return a list from RepIndInnovationNature null if no exist records
   */
  public List<RepIndInnovationNature> findAll();


  /**
   * This method saves the information of the given repIndInnovationNature
   * 
   * @param repIndInnovationNature - is the repIndInnovationNature object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndInnovationNature was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndInnovationNature save(RepIndInnovationNature repIndInnovationNature);
}
