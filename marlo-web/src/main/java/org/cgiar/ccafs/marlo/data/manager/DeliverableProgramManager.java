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

import org.cgiar.ccafs.marlo.data.manager.impl.DeliverableProgramManagerImpl;
import org.cgiar.ccafs.marlo.data.model.DeliverableProgram;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(DeliverableProgramManagerImpl.class)
public interface DeliverableProgramManager {


  /**
   * This method removes a specific deliverableProgram value from the database.
   * 
   * @param deliverableProgramId is the deliverableProgram identifier.
   * @return true if the deliverableProgram was successfully deleted, false otherwise.
   */
  public boolean deleteDeliverableProgram(long deliverableProgramId);


  /**
   * This method validate if the deliverableProgram identify with the given id exists in the system.
   * 
   * @param deliverableProgramID is a deliverableProgram identifier.
   * @return true if the deliverableProgram exists, false otherwise.
   */
  public boolean existDeliverableProgram(long deliverableProgramID);


  /**
   * This method gets a list of deliverableProgram that are active
   * 
   * @return a list from DeliverableProgram null if no exist records
   */
  public List<DeliverableProgram> findAll();


  /**
   * This method gets a deliverableProgram object by a given deliverableProgram identifier.
   * 
   * @param deliverableProgramID is the deliverableProgram identifier.
   * @return a DeliverableProgram object.
   */
  public DeliverableProgram getDeliverableProgramById(long deliverableProgramID);

  /**
   * This method saves the information of the given deliverableProgram
   * 
   * @param deliverableProgram - is the deliverableProgram object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableProgram was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveDeliverableProgram(DeliverableProgram deliverableProgram);


}
