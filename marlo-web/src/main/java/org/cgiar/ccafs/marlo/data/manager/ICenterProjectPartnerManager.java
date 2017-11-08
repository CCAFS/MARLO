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

import org.cgiar.ccafs.marlo.data.manager.impl.CenterProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.CenterProjectPartner;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterProjectPartnerManager.class)
public interface ICenterProjectPartnerManager {


  /**
   * This method removes a specific projectPartner value from the database.
   * 
   * @param projectPartnerId is the projectPartner identifier.
   * @return true if the projectPartner was successfully deleted, false otherwise.
   */
  public void deleteProjectPartner(long projectPartnerId);


  /**
   * This method validate if the projectPartner identify with the given id exists in the system.
   * 
   * @param projectPartnerID is a projectPartner identifier.
   * @return true if the projectPartner exists, false otherwise.
   */
  public boolean existProjectPartner(long projectPartnerID);


  /**
   * This method gets a list of projectPartner that are active
   * 
   * @return a list from CenterProjectPartner null if no exist records
   */
  public List<CenterProjectPartner> findAll();


  /**
   * This method gets a projectPartner object by a given projectPartner identifier.
   * 
   * @param projectPartnerID is the projectPartner identifier.
   * @return a CenterProjectPartner object.
   */
  public CenterProjectPartner getProjectPartnerById(long projectPartnerID);

  /**
   * This method gets a list of projectPartners belongs of the user
   * 
   * @param userId - the user id
   * @return List of ProjectPartners or null if the user is invalid or not have roles.
   */
  public List<CenterProjectPartner> getProjectPartnersByUserId(Long userId);

  /**
   * This method saves the information of the given projectPartner
   * 
   * @param projectPartner - is the projectPartner object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectPartner was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterProjectPartner saveProjectPartner(CenterProjectPartner projectPartner);


}
