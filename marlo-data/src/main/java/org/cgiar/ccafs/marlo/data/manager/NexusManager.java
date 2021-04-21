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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.Nexus;

import java.util.List;


/**
 * @author CCAFS
 */

public interface NexusManager {


  /**
   * This method removes a specific nexus value from the database.
   * 
   * @param nexusId is the nexus identifier.
   * @return true if the nexus was successfully deleted, false otherwise.
   */
  public void deleteNexus(long nexusId);


  /**
   * This method validate if the nexus identify with the given id exists in the system.
   * 
   * @param nexusID is a nexus identifier.
   * @return true if the nexus exists, false otherwise.
   */
  public boolean existNexus(long nexusID);


  /**
   * This method gets a list of nexus that are active
   * 
   * @return a list from Nexus null if no exist records
   */
  public List<Nexus> findAll();


  /**
   * This method gets a nexus object by a given nexus identifier.
   * 
   * @param nexusID is the nexus identifier.
   * @return a Nexus object.
   */
  public Nexus getNexusById(long nexusID);

  /**
   * This method saves the information of the given nexus
   * 
   * @param nexus - is the nexus object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the nexus was
   *         updated
   *         or -1 is some error occurred.
   */
  public Nexus saveNexus(Nexus nexus);


}
