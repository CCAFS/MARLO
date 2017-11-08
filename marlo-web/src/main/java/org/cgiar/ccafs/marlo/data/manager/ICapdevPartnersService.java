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

import org.cgiar.ccafs.marlo.data.manager.impl.CapdevPartnersService;
import org.cgiar.ccafs.marlo.data.model.CapdevPartners;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CapdevPartnersService.class)
public interface ICapdevPartnersService {


  /**
   * This method removes a specific capdevPartners value from the database.
   * 
   * @param capdevPartnersId is the capdevPartners identifier.
   */
  public void deleteCapdevPartners(long capdevPartnersId);


  /**
   * This method validate if the capdevPartners identify with the given id exists in the system.
   * 
   * @param capdevPartnersID is a capdevPartners identifier.
   * @return true if the capdevPartners exists, false otherwise.
   */
  public boolean existCapdevPartners(long capdevPartnersID);


  /**
   * This method gets a list of capdevPartners that are active
   * 
   * @return a list from CapdevPartners null if no exist records
   */
  public List<CapdevPartners> findAll();


  /**
   * This method gets a capdevPartners object by a given capdevPartners identifier.
   * 
   * @param capdevPartnersID is the capdevPartners identifier.
   * @return a CapdevPartners object.
   */
  public CapdevPartners getCapdevPartnersById(long capdevPartnersID);

  /**
   * This method gets a list of capdevPartnerss belongs of the user
   * 
   * @param userId - the user id
   * @return List of CapdevPartnerss or null if the user is invalid or not have roles.
   */
  public List<CapdevPartners> getCapdevPartnerssByUserId(Long userId);

  /**
   * This method saves the information of the given capdevPartners
   * 
   * @param capdevPartners - is the capdevPartners object with the new information to be added/updated.
   * @return a object.
   */
  public CapdevPartners saveCapdevPartners(CapdevPartners capdevPartners);

  /**
   * This method saves the information of the given capdevPartners
   * 
   * @param capdevPartners - is the capdevPartners object with the new information to be added/updated.
   * @return a object.
   */
  public CapdevPartners saveCapdevPartners(CapdevPartners capdevPartners, String actionName,
    List<String> relationsName);


}
