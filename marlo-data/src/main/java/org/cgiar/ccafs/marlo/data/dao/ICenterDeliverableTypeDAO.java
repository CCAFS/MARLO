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


import org.cgiar.ccafs.marlo.data.dao.mysql.CenterDeliverableTypeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableType;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CenterDeliverableTypeDAO.class)
public interface ICenterDeliverableTypeDAO {

  /**
   * This method removes a specific deliverableType value from the database.
   * 
   * @param deliverableTypeId is the deliverableType identifier.
   * @return true if the deliverableType was successfully deleted, false otherwise.
   */
  public boolean deleteDeliverableType(long deliverableTypeId);

  /**
   * This method validate if the deliverableType identify with the given id exists in the system.
   * 
   * @param deliverableTypeID is a deliverableType identifier.
   * @return true if the deliverableType exists, false otherwise.
   */
  public boolean existDeliverableType(long deliverableTypeID);

  /**
   * This method gets a deliverableType object by a given deliverableType identifier.
   * 
   * @param deliverableTypeID is the deliverableType identifier.
   * @return a CenterDeliverableType object.
   */
  public CenterDeliverableType find(long id);

  /**
   * This method gets a list of deliverableType that are active
   * 
   * @return a list from CenterDeliverableType null if no exist records
   */
  public List<CenterDeliverableType> findAll();

  /**
   * This method gets a list of deliverableTypes belongs of the user
   * 
   * @param userId - the user id
   * @return List of DeliverableTypes or null if the user is invalid or not have roles.
   */
  public List<CenterDeliverableType> getDeliverableTypesByUserId(long userId);


  /**
   * This method gets a list of SubdeliverableType
   * 
   * @param deliverableId is the deliverableType identifier.
   * @return a list from CenterDeliverableType (SubType) null if no exist records
   */
  public List<CenterDeliverableType> getSubDeliverableType(Long deliverableId);

  /**
   * This method saves the information of the given deliverableType
   * 
   * @param deliverableType - is the deliverableType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableType was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CenterDeliverableType deliverableType);
}
