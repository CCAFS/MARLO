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

import org.cgiar.ccafs.marlo.data.dao.mysql.LiaisonUserMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(LiaisonUserMySQLDAO.class)
public interface LiaisonUserDAO {

  /**
   * This method removes a specific liaisonUser value from the database.
   * 
   * @param liaisonUserId is the liaisonUser identifier.
   * @return true if the liaisonUser was successfully deleted, false otherwise.
   */
  public void deleteLiaisonUser(long liaisonUserId);

  /**
   * This method validate if the liaisonUser identify with the given id exists in the system.
   * 
   * @param liaisonUserID is a liaisonUser identifier.
   * @return true if the liaisonUser exists, false otherwise.
   */
  public boolean existLiaisonUser(long liaisonUserID);

  /**
   * This method gets a liaisonUser object by a given liaisonUser identifier.
   * 
   * @param liaisonUserID is the liaisonUser identifier.
   * @return a LiaisonUser object.
   */
  public LiaisonUser find(long id);

  /**
   * This method gets a list of liaisonUser that are active
   * 
   * @return a list from LiaisonUser null if no exist records
   */
  public List<LiaisonUser> findAll();

  /**
   * This method gets a list of liaisonUser by a given Institution identifier.
   * 
   * @param institutionId is the institution identifier.
   * @return a List of LiaisonUser.
   */
  public List<LiaisonUser> findByInstitutionId(Long institutionId);


  /**
   * This method gets a liaisonUser object by a given user identifier.
   * 
   * @param id is the user identifier.
   * @return a LiaisonUser object.
   */
  public LiaisonUser findByUser(long id, long crpID);

  /**
   * This method saves the information of the given liaisonUser
   * 
   * @param liaisonUser - is the liaisonUser object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the liaisonUser was
   *         updated
   *         or -1 is some error occurred.
   */
  public LiaisonUser save(LiaisonUser liaisonUser);
}
