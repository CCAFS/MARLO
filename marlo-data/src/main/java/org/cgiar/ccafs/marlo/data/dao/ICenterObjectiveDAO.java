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

import org.cgiar.ccafs.marlo.data.dao.mysql.CenterObjectiveDAO;
import org.cgiar.ccafs.marlo.data.model.CenterObjective;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CenterObjectiveDAO.class)
public interface ICenterObjectiveDAO {

  /**
   * This method removes a specific researchObjective value from the database.
   * 
   * @param researchObjectiveId is the researchObjective identifier.
   * @return true if the researchObjective was successfully deleted, false otherwise.
   */
  public boolean deleteResearchObjective(long researchObjectiveId);

  /**
   * This method validate if the researchObjective identify with the given id exists in the system.
   * 
   * @param researchObjectiveID is a researchObjective identifier.
   * @return true if the researchObjective exists, false otherwise.
   */
  public boolean existResearchObjective(long researchObjectiveID);

  /**
   * This method gets a researchObjective object by a given researchObjective identifier.
   * 
   * @param researchObjectiveID is the researchObjective identifier.
   * @return a CenterObjective object.
   */
  public CenterObjective find(long id);

  /**
   * This method gets a list of researchObjective that are active
   * 
   * @return a list from CenterObjective null if no exist records
   */
  public List<CenterObjective> findAll();


  /**
   * This method gets a list of researchObjectives belongs of the user
   * 
   * @param userId - the user id
   * @return List of ResearchObjectives or null if the user is invalid or not have roles.
   */
  public List<CenterObjective> getResearchObjectivesByUserId(long userId);

  /**
   * This method saves the information of the given researchObjective
   * 
   * @param researchObjective - is the researchObjective object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the researchObjective was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CenterObjective researchObjective);
}
