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

import org.cgiar.ccafs.marlo.data.dao.impl.CenterOutputsNextUserDAO;
import org.cgiar.ccafs.marlo.data.model.CenterOutputsNextUser;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CenterOutputsNextUserDAO.class)
public interface ICenterOutputsNextUserDAO {

  /**
   * This method removes a specific researchOutputsNextUser value from the database.
   * 
   * @param researchOutputsNextUserId is the researchOutputsNextUser identifier.
   * @return true if the researchOutputsNextUser was successfully deleted, false otherwise.
   */
  public boolean deleteResearchOutputsNextUser(long researchOutputsNextUserId);

  /**
   * This method validate if the researchOutputsNextUser identify with the given id exists in the system.
   * 
   * @param researchOutputsNextUserID is a researchOutputsNextUser identifier.
   * @return true if the researchOutputsNextUser exists, false otherwise.
   */
  public boolean existResearchOutputsNextUser(long researchOutputsNextUserID);

  /**
   * This method gets a researchOutputsNextUser object by a given researchOutputsNextUser identifier.
   * 
   * @param researchOutputsNextUserID is the researchOutputsNextUser identifier.
   * @return a CenterOutputsNextUser object.
   */
  public CenterOutputsNextUser find(long id);

  /**
   * This method gets a list of researchOutputsNextUser that are active
   * 
   * @return a list from CenterOutputsNextUser null if no exist records
   */
  public List<CenterOutputsNextUser> findAll();


  /**
   * This method gets a list of researchOutputsNextUsers belongs of the user
   * 
   * @param userId - the user id
   * @return List of ResearchOutputsNextUsers or null if the user is invalid or not have roles.
   */
  public List<CenterOutputsNextUser> getResearchOutputsNextUsersByUserId(long userId);

  /**
   * This method saves the information of the given researchOutputsNextUser
   * 
   * @param researchOutputsNextUser - is the researchOutputsNextUser object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the researchOutputsNextUser was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CenterOutputsNextUser researchOutputsNextUser);
}
