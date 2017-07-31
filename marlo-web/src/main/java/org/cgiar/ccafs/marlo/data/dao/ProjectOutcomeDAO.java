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

import org.cgiar.ccafs.marlo.data.dao.mysql.ProjectOutcomeMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectOutcomeMySQLDAO.class)
public interface ProjectOutcomeDAO {

  /**
   * This method removes a specific projectOutcome value from the database.
   * 
   * @param projectOutcomeId is the projectOutcome identifier.
   * @return true if the projectOutcome was successfully deleted, false otherwise.
   */
  public boolean deleteProjectOutcome(long projectOutcomeId);

  /**
   * This method validate if the projectOutcome identify with the given id exists in the system.
   * 
   * @param projectOutcomeID is a projectOutcome identifier.
   * @return true if the projectOutcome exists, false otherwise.
   */
  public boolean existProjectOutcome(long projectOutcomeID);

  /**
   * This method gets a projectOutcome object by a given projectOutcome identifier.
   * 
   * @param projectOutcomeID is the projectOutcome identifier.
   * @return a ProjectOutcome object.
   */
  public ProjectOutcome find(long id);

  /**
   * This method gets a list of projectOutcome that are active
   * 
   * @return a list from ProjectOutcome null if no exist records
   */
  public List<ProjectOutcome> findAll();


  /**
   * This method saves the information of the given projectOutcome
   * 
   * @param projectOutcome - is the projectOutcome object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(ProjectOutcome projectOutcome);

  public long save(ProjectOutcome projectOutcome, String section, List<String> relationsName, Phase phase);
}
