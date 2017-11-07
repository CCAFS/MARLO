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

import org.cgiar.ccafs.marlo.data.dao.mysql.ProjectPhaseMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectPhaseMySQLDAO.class)
public interface ProjectPhaseDAO {

  /**
   * This method removes a specific projectPhase value from the database.
   * 
   * @param projectPhaseId is the projectPhase identifier.
   * @return true if the projectPhase was successfully deleted, false otherwise.
   */
  public void deleteProjectPhase(long projectPhaseId);

  /**
   * This method validate if the projectPhase identify with the given id exists in the system.
   * 
   * @param projectPhaseID is a projectPhase identifier.
   * @return true if the projectPhase exists, false otherwise.
   */
  public boolean existProjectPhase(long projectPhaseID);

  /**
   * This method gets a projectPhase object by a given projectPhase identifier.
   * 
   * @param projectPhaseID is the projectPhase identifier.
   * @return a ProjectPhase object.
   */
  public ProjectPhase find(long id);

  /**
   * This method gets a list of projectPhase that are active
   * 
   * @return a list from ProjectPhase null if no exist records
   */
  public List<ProjectPhase> findAll();


  /**
   * This method saves the information of the given projectPhase
   * 
   * @param projectPhase - is the projectPhase object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectPhase was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPhase save(ProjectPhase projectPhase);
}
