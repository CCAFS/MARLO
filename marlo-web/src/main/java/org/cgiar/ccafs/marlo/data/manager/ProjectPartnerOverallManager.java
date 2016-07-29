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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.manager.impl.ProjectPartnerOverallManagerImpl;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerOverall;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(ProjectPartnerOverallManagerImpl.class)
public interface ProjectPartnerOverallManager {


  /**
   * This method removes a specific projectPartnerOverall value from the database.
   * 
   * @param projectPartnerOverallId is the projectPartnerOverall identifier.
   * @return true if the projectPartnerOverall was successfully deleted, false otherwise.
   */
  public boolean deleteProjectPartnerOverall(long projectPartnerOverallId);


  /**
   * This method validate if the projectPartnerOverall identify with the given id exists in the system.
   * 
   * @param projectPartnerOverallID is a projectPartnerOverall identifier.
   * @return true if the projectPartnerOverall exists, false otherwise.
   */
  public boolean existProjectPartnerOverall(long projectPartnerOverallID);


  /**
   * This method gets a list of projectPartnerOverall that are active
   * 
   * @return a list from ProjectPartnerOverall null if no exist records
   */
  public List<ProjectPartnerOverall> findAll();


  /**
   * This method gets a projectPartnerOverall object by a given projectPartnerOverall identifier.
   * 
   * @param projectPartnerOverallID is the projectPartnerOverall identifier.
   * @return a ProjectPartnerOverall object.
   */
  public ProjectPartnerOverall getProjectPartnerOverallById(long projectPartnerOverallID);

  /**
   * This method saves the information of the given projectPartnerOverall
   * 
   * @param projectPartnerOverall - is the projectPartnerOverall object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectPartnerOverall was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveProjectPartnerOverall(ProjectPartnerOverall projectPartnerOverall);


}
