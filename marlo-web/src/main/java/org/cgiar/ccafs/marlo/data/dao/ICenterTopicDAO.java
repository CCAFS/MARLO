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

import org.cgiar.ccafs.marlo.data.dao.mysql.CenterTopicDAO;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CenterTopicDAO.class)
public interface ICenterTopicDAO {

  /**
   * This method removes a specific researchTopic value from the database.
   * 
   * @param researchTopicId is the researchTopic identifier.
   * @return true if the researchTopic was successfully deleted, false otherwise.
   */
  public boolean deleteResearchTopic(long researchTopicId);

  /**
   * This method validate if the researchTopic identify with the given id exists in the system.
   * 
   * @param researchTopicID is a researchTopic identifier.
   * @return true if the researchTopic exists, false otherwise.
   */
  public boolean existResearchTopic(long researchTopicID);

  /**
   * This method gets a researchTopic object by a given researchTopic identifier.
   * 
   * @param researchTopicID is the researchTopic identifier.
   * @return a CenterTopic object.
   */
  public CenterTopic find(long id);

  /**
   * This method gets a list of researchTopic that are active
   * 
   * @return a list from CenterTopic null if no exist records
   */
  public List<CenterTopic> findAll();


  /**
   * This method gets a list of researchTopics belongs of the user
   * 
   * @param userId - the user id
   * @return List of ResearchTopics or null if the user is invalid or not have roles.
   */
  public List<CenterTopic> getResearchTopicsByUserId(long userId);

  /**
   * This method saves the information of the given researchTopic
   * 
   * @param researchTopic - is the researchTopic object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the researchTopic was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CenterTopic researchTopic);
}
