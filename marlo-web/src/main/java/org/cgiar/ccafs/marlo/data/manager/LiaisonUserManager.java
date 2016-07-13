/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.manager.impl.LiaisonUserManagerImpl;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(LiaisonUserManagerImpl.class)
public interface LiaisonUserManager {


  /**
   * This method removes a specific liaisonUser value from the database.
   * 
   * @param liaisonUserId is the liaisonUser identifier.
   * @return true if the liaisonUser was successfully deleted, false otherwise.
   */
  public boolean deleteLiaisonUser(long liaisonUserId);


  /**
   * This method validate if the liaisonUser identify with the given id exists in the system.
   * 
   * @param liaisonUserID is a liaisonUser identifier.
   * @return true if the liaisonUser exists, false otherwise.
   */
  public boolean existLiaisonUser(long liaisonUserID);


  /**
   * This method gets a list of liaisonUser that are active
   * 
   * @return a list from LiaisonUser null if no exist records
   */
  public List<LiaisonUser> findAll();


  /**
   * This method gets a liaisonUser object by a given liaisonUser identifier.
   * 
   * @param liaisonUserID is the liaisonUser identifier.
   * @return a LiaisonUser object.
   */
  public LiaisonUser getLiaisonUserById(long liaisonUserID);

  /**
   * This method saves the information of the given liaisonUser
   * 
   * @param liaisonUser - is the liaisonUser object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the liaisonUser was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveLiaisonUser(LiaisonUser liaisonUser);


}
