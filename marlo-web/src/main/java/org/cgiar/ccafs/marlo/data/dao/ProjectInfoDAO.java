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

import org.cgiar.ccafs.marlo.data.dao.mysql.ProjectInfoMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectInfoMySQLDAO.class)
public interface ProjectInfoDAO {

  /**
   * This method removes a specific projectInfo value from the database.
   * 
   * @param projectInfoId is the projectInfo identifier.
   * @return true if the projectInfo was successfully deleted, false otherwise.
   */
  public boolean deleteProjectInfo(long projectInfoId);

  /**
   * This method validate if the projectInfo identify with the given id exists in the system.
   * 
   * @param projectInfoID is a projectInfo identifier.
   * @return true if the projectInfo exists, false otherwise.
   */
  public boolean existProjectInfo(long projectInfoID);

  /**
   * This method gets a projectInfo object by a given projectInfo identifier.
   * 
   * @param projectInfoID is the projectInfo identifier.
   * @return a ProjectInfo object.
   */
  public ProjectInfo find(long id);

  /**
   * This method gets a list of projectInfo that are active
   * 
   * @return a list from ProjectInfo null if no exist records
   */
  public List<ProjectInfo> findAll();


  /**
   * This method saves the information of the given projectInfo
   * 
   * @param projectInfo - is the projectInfo object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInfo was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(ProjectInfo projectInfo);
}
