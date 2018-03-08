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

import org.cgiar.ccafs.marlo.data.model.PowbCollaboration;

import java.util.List;


public interface PowbCollaborationDAO {

  /**
   * This method removes a specific powbCollaboration value from the database.
   * 
   * @param powbCollaborationId is the powbCollaboration identifier.
   * @return true if the powbCollaboration was successfully deleted, false otherwise.
   */
  public void deletePowbCollaboration(long powbCollaborationId);

  /**
   * This method validate if the powbCollaboration identify with the given id exists in the system.
   * 
   * @param powbCollaborationID is a powbCollaboration identifier.
   * @return true if the powbCollaboration exists, false otherwise.
   */
  public boolean existPowbCollaboration(long powbCollaborationID);

  /**
   * This method gets a powbCollaboration object by a given powbCollaboration identifier.
   * 
   * @param powbCollaborationID is the powbCollaboration identifier.
   * @return a PowbCollaboration object.
   */
  public PowbCollaboration find(long id);

  /**
   * This method gets a list of powbCollaboration that are active
   * 
   * @return a list from PowbCollaboration null if no exist records
   */
  public List<PowbCollaboration> findAll();


  /**
   * This method saves the information of the given powbCollaboration
   * 
   * @param powbCollaboration - is the powbCollaboration object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbCollaboration was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbCollaboration save(PowbCollaboration powbCollaboration);
}
