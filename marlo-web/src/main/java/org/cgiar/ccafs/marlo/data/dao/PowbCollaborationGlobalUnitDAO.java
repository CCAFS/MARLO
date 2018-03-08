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

import org.cgiar.ccafs.marlo.data.model.PowbCollaborationGlobalUnit;

import java.util.List;


public interface PowbCollaborationGlobalUnitDAO {

  /**
   * This method removes a specific powbCollaborationGlobalUnit value from the database.
   * 
   * @param powbCollaborationGlobalUnitId is the powbCollaborationGlobalUnit identifier.
   * @return true if the powbCollaborationGlobalUnit was successfully deleted, false otherwise.
   */
  public void deletePowbCollaborationGlobalUnit(long powbCollaborationGlobalUnitId);

  /**
   * This method validate if the powbCollaborationGlobalUnit identify with the given id exists in the system.
   * 
   * @param powbCollaborationGlobalUnitID is a powbCollaborationGlobalUnit identifier.
   * @return true if the powbCollaborationGlobalUnit exists, false otherwise.
   */
  public boolean existPowbCollaborationGlobalUnit(long powbCollaborationGlobalUnitID);

  /**
   * This method gets a powbCollaborationGlobalUnit object by a given powbCollaborationGlobalUnit identifier.
   * 
   * @param powbCollaborationGlobalUnitID is the powbCollaborationGlobalUnit identifier.
   * @return a PowbCollaborationGlobalUnit object.
   */
  public PowbCollaborationGlobalUnit find(long id);

  /**
   * This method gets a list of powbCollaborationGlobalUnit that are active
   * 
   * @return a list from PowbCollaborationGlobalUnit null if no exist records
   */
  public List<PowbCollaborationGlobalUnit> findAll();


  /**
   * This method saves the information of the given powbCollaborationGlobalUnit
   * 
   * @param powbCollaborationGlobalUnit - is the powbCollaborationGlobalUnit object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbCollaborationGlobalUnit was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbCollaborationGlobalUnit save(PowbCollaborationGlobalUnit powbCollaborationGlobalUnit);
}
