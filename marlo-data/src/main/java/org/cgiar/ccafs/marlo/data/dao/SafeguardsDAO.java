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

import org.cgiar.ccafs.marlo.data.model.Safeguards;

import java.util.List;


public interface SafeguardsDAO {

  /**
   * This method removes a specific safeguards value from the database.
   * 
   * @param safeguardsId is the safeguards identifier.
   * @return true if the safeguards was successfully deleted, false otherwise.
   */
  public void deleteSafeguards(long safeguardsId);

  /**
   * This method validate if the safeguards identify with the given id exists in the system.
   * 
   * @param safeguardsID is a safeguards identifier.
   * @return true if the safeguards exists, false otherwise.
   */
  public boolean existSafeguards(long safeguardsID);

  /**
   * This method gets a safeguards object by a given safeguards identifier.
   * 
   * @param safeguardsID is the safeguards identifier.
   * @return a Safeguards object.
   */
  public Safeguards find(long id);

  /**
   * This method gets a list of safeguards that are active
   * 
   * @return a list from Safeguards null if no exist records
   */
  public List<Safeguards> findAll();


  /**
   * This method saves the information of the given safeguards
   * 
   * @param safeguards - is the safeguards object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the safeguards was
   *         updated
   *         or -1 is some error occurred.
   */
  public Safeguards save(Safeguards safeguards);
}
