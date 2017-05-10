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

import org.cgiar.ccafs.marlo.data.dao.mysql.ProjectPartnerLocationMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectPartnerLocationMySQLDAO.class)
public interface ProjectPartnerLocationDAO {

  /**
   * This method removes a specific projectPartnerLocation value from the database.
   * 
   * @param projectPartnerLocationId is the projectPartnerLocation identifier.
   * @return true if the projectPartnerLocation was successfully deleted, false otherwise.
   */
  public boolean deleteProjectPartnerLocation(long projectPartnerLocationId);

  /**
   * This method validate if the projectPartnerLocation identify with the given id exists in the system.
   * 
   * @param projectPartnerLocationID is a projectPartnerLocation identifier.
   * @return true if the projectPartnerLocation exists, false otherwise.
   */
  public boolean existProjectPartnerLocation(long projectPartnerLocationID);

  /**
   * This method gets a projectPartnerLocation object by a given projectPartnerLocation identifier.
   * 
   * @param projectPartnerLocationID is the projectPartnerLocation identifier.
   * @return a ProjectPartnerLocation object.
   */
  public ProjectPartnerLocation find(long id);

  /**
   * This method gets a list of projectPartnerLocation that are active
   * 
   * @return a list from ProjectPartnerLocation null if no exist records
   */
  public List<ProjectPartnerLocation> findAll();


  /**
   * This method saves the information of the given projectPartnerLocation
   * 
   * @param projectPartnerLocation - is the projectPartnerLocation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectPartnerLocation was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(ProjectPartnerLocation projectPartnerLocation);
}
