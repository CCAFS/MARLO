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

import org.cgiar.ccafs.marlo.data.model.PowbCollaborationGlobalUnitPmu;

import java.util.List;


public interface PowbCollaborationGlobalUnitPmuDAO {

  /**
   * This method removes a specific powbCollaborationGlobalUnitPmu value from the database.
   * 
   * @param powbCollaborationGlobalUnitPmuId is the powbCollaborationGlobalUnitPmu identifier.
   * @return true if the powbCollaborationGlobalUnitPmu was successfully deleted, false otherwise.
   */
  public void deletePowbCollaborationGlobalUnitPmu(long powbCollaborationGlobalUnitPmuId);

  /**
   * This method validate if the powbCollaborationGlobalUnitPmu identify with the given id exists in the system.
   * 
   * @param powbCollaborationGlobalUnitPmuID is a powbCollaborationGlobalUnitPmu identifier.
   * @return true if the powbCollaborationGlobalUnitPmu exists, false otherwise.
   */
  public boolean existPowbCollaborationGlobalUnitPmu(long powbCollaborationGlobalUnitPmuID);

  /**
   * This method gets a powbCollaborationGlobalUnitPmu object by a given powbCollaborationGlobalUnitPmu identifier.
   * 
   * @param powbCollaborationGlobalUnitPmuID is the powbCollaborationGlobalUnitPmu identifier.
   * @return a PowbCollaborationGlobalUnitPmu object.
   */
  public PowbCollaborationGlobalUnitPmu find(long id);

  /**
   * This method gets a list of powbCollaborationGlobalUnitPmu that are active
   * 
   * @return a list from PowbCollaborationGlobalUnitPmu null if no exist records
   */
  public List<PowbCollaborationGlobalUnitPmu> findAll();


  /**
   * This method saves the information of the given powbCollaborationGlobalUnitPmu
   * 
   * @param powbCollaborationGlobalUnitPmu - is the powbCollaborationGlobalUnitPmu object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbCollaborationGlobalUnitPmu was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbCollaborationGlobalUnitPmu save(PowbCollaborationGlobalUnitPmu powbCollaborationGlobalUnitPmu);
}
