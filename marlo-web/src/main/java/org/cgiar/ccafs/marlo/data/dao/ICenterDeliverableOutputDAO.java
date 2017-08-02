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


import org.cgiar.ccafs.marlo.data.dao.mysql.CenterDeliverableOutputDAO;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableOutput;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CenterDeliverableOutputDAO.class)
public interface ICenterDeliverableOutputDAO {

  /**
   * This method removes a specific deliverableOutput value from the database.
   * 
   * @param deliverableOutputId is the deliverableOutput identifier.
   * @return true if the deliverableOutput was successfully deleted, false otherwise.
   */
  public boolean deleteDeliverableOutput(long deliverableOutputId);

  /**
   * This method validate if the deliverableOutput identify with the given id exists in the system.
   * 
   * @param deliverableOutputID is a deliverableOutput identifier.
   * @return true if the deliverableOutput exists, false otherwise.
   */
  public boolean existDeliverableOutput(long deliverableOutputID);

  /**
   * This method gets a deliverableOutput object by a given deliverableOutput identifier.
   * 
   * @param deliverableOutputID is the deliverableOutput identifier.
   * @return a CenterDeliverableOutput object.
   */
  public CenterDeliverableOutput find(long id);

  /**
   * This method gets a list of deliverableOutput that are active
   * 
   * @return a list from CenterDeliverableOutput null if no exist records
   */
  public List<CenterDeliverableOutput> findAll();


  /**
   * This method gets a list of deliverableOutputs belongs of the user
   * 
   * @param userId - the user id
   * @return List of DeliverableOutputs or null if the user is invalid or not have roles.
   */
  public List<CenterDeliverableOutput> getDeliverableOutputsByUserId(long userId);

  /**
   * This method saves the information of the given deliverableOutput
   * 
   * @param deliverableOutput - is the deliverableOutput object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableOutput was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CenterDeliverableOutput deliverableOutput);

  /**
   * This method saves the information of the given deliverableOutput
   * 
   * @param deliverableOutput - is the deliverableOutput object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableOutput was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CenterDeliverableOutput deliverableOutput, String actionName, List<String> relationsName);
}
