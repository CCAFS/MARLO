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

import org.cgiar.ccafs.marlo.data.model.PowbFlagshipPlans;

import java.util.List;


public interface PowbFlagshipPlansDAO {

  /**
   * This method removes a specific powbFlagshipPlans value from the database.
   * 
   * @param powbFlagshipPlansId is the powbFlagshipPlans identifier.
   * @return true if the powbFlagshipPlans was successfully deleted, false otherwise.
   */
  public void deletePowbFlagshipPlans(long powbFlagshipPlansId);

  /**
   * This method validate if the powbFlagshipPlans identify with the given id exists in the system.
   * 
   * @param powbFlagshipPlansID is a powbFlagshipPlans identifier.
   * @return true if the powbFlagshipPlans exists, false otherwise.
   */
  public boolean existPowbFlagshipPlans(long powbFlagshipPlansID);

  /**
   * This method gets a powbFlagshipPlans object by a given powbFlagshipPlans identifier.
   * 
   * @param powbFlagshipPlansID is the powbFlagshipPlans identifier.
   * @return a PowbFlagshipPlans object.
   */
  public PowbFlagshipPlans find(long id);

  /**
   * This method gets a list of powbFlagshipPlans that are active
   * 
   * @return a list from PowbFlagshipPlans null if no exist records
   */
  public List<PowbFlagshipPlans> findAll();


  /**
   * This method saves the information of the given powbFlagshipPlans
   * 
   * @param powbFlagshipPlans - is the powbFlagshipPlans object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbFlagshipPlans was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbFlagshipPlans save(PowbFlagshipPlans powbFlagshipPlans);
}
