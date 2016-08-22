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

import org.cgiar.ccafs.marlo.data.dao.mysql.ProjectCommunicationMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectCommunication;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectCommunicationMySQLDAO.class)
public interface ProjectCommunicationDAO {

  /**
   * This method removes a specific projectCommunication value from the database.
   * 
   * @param projectCommunicationId is the projectCommunication identifier.
   * @return true if the projectCommunication was successfully deleted, false otherwise.
   */
  public boolean deleteProjectCommunication(long projectCommunicationId);

  /**
   * This method validate if the projectCommunication identify with the given id exists in the system.
   * 
   * @param projectCommunicationID is a projectCommunication identifier.
   * @return true if the projectCommunication exists, false otherwise.
   */
  public boolean existProjectCommunication(long projectCommunicationID);

  /**
   * This method gets a projectCommunication object by a given projectCommunication identifier.
   * 
   * @param projectCommunicationID is the projectCommunication identifier.
   * @return a ProjectCommunication object.
   */
  public ProjectCommunication find(long id);

  /**
   * This method gets a list of projectCommunication that are active
   * 
   * @return a list from ProjectCommunication null if no exist records
   */
  public List<ProjectCommunication> findAll();


  /**
   * This method saves the information of the given projectCommunication
   * 
   * @param projectCommunication - is the projectCommunication object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectCommunication was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(ProjectCommunication projectCommunication);
}
