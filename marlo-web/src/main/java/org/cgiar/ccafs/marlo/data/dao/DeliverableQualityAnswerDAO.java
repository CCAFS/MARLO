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

import org.cgiar.ccafs.marlo.data.dao.mysql.DeliverableQualityAnswerMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityAnswer;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(DeliverableQualityAnswerMySQLDAO.class)
public interface DeliverableQualityAnswerDAO {

  /**
   * This method removes a specific deliverableQualityAnswer value from the database.
   * 
   * @param deliverableQualityAnswerId is the deliverableQualityAnswer identifier.
   * @return true if the deliverableQualityAnswer was successfully deleted, false otherwise.
   */
  public void deleteDeliverableQualityAnswer(long deliverableQualityAnswerId);

  /**
   * This method validate if the deliverableQualityAnswer identify with the given id exists in the system.
   * 
   * @param deliverableQualityAnswerID is a deliverableQualityAnswer identifier.
   * @return true if the deliverableQualityAnswer exists, false otherwise.
   */
  public boolean existDeliverableQualityAnswer(long deliverableQualityAnswerID);

  /**
   * This method gets a deliverableQualityAnswer object by a given deliverableQualityAnswer identifier.
   * 
   * @param deliverableQualityAnswerID is the deliverableQualityAnswer identifier.
   * @return a DeliverableQualityAnswer object.
   */
  public DeliverableQualityAnswer find(long id);

  /**
   * This method gets a list of deliverableQualityAnswer that are active
   * 
   * @return a list from DeliverableQualityAnswer null if no exist records
   */
  public List<DeliverableQualityAnswer> findAll();


  /**
   * This method saves the information of the given deliverableQualityAnswer
   * 
   * @param deliverableQualityAnswer - is the deliverableQualityAnswer object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableQualityAnswer was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableQualityAnswer save(DeliverableQualityAnswer deliverableQualityAnswer);
}
