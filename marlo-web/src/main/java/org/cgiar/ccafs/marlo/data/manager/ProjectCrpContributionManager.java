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

import org.cgiar.ccafs.marlo.data.manager.impl.ProjectCrpContributionManagerImpl;
import org.cgiar.ccafs.marlo.data.model.ProjectCrpContribution;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(ProjectCrpContributionManagerImpl.class)
public interface ProjectCrpContributionManager {


  /**
   * This method removes a specific projectCrpContribution value from the database.
   * 
   * @param projectCrpContributionId is the projectCrpContribution identifier.
   * @return true if the projectCrpContribution was successfully deleted, false otherwise.
   */
  public void deleteProjectCrpContribution(long projectCrpContributionId);


  /**
   * This method validate if the projectCrpContribution identify with the given id exists in the system.
   * 
   * @param projectCrpContributionID is a projectCrpContribution identifier.
   * @return true if the projectCrpContribution exists, false otherwise.
   */
  public boolean existProjectCrpContribution(long projectCrpContributionID);


  /**
   * This method gets a list of projectCrpContribution that are active
   * 
   * @return a list from ProjectCrpContribution null if no exist records
   */
  public List<ProjectCrpContribution> findAll();


  /**
   * This method gets a projectCrpContribution object by a given projectCrpContribution identifier.
   * 
   * @param projectCrpContributionID is the projectCrpContribution identifier.
   * @return a ProjectCrpContribution object.
   */
  public ProjectCrpContribution getProjectCrpContributionById(long projectCrpContributionID);

  /**
   * This method saves the information of the given projectCrpContribution
   * 
   * @param projectCrpContribution - is the projectCrpContribution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectCrpContribution was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectCrpContribution saveProjectCrpContribution(ProjectCrpContribution projectCrpContribution);


}
