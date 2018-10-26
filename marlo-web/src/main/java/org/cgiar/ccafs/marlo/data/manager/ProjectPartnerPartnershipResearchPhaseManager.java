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

import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnershipResearchPhase;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectPartnerPartnershipResearchPhaseManager {


  /**
   * This method removes a specific projectPartnerPartnershipResearchPhase value from the database.
   * 
   * @param projectPartnerPartnershipResearchPhaseId is the projectPartnerPartnershipResearchPhase identifier.
   * @return true if the projectPartnerPartnershipResearchPhase was successfully deleted, false otherwise.
   */
  public void deleteProjectPartnerPartnershipResearchPhase(long projectPartnerPartnershipResearchPhaseId);


  /**
   * This method validate if the projectPartnerPartnershipResearchPhase identify with the given id exists in the system.
   * 
   * @param projectPartnerPartnershipResearchPhaseID is a projectPartnerPartnershipResearchPhase identifier.
   * @return true if the projectPartnerPartnershipResearchPhase exists, false otherwise.
   */
  public boolean existProjectPartnerPartnershipResearchPhase(long projectPartnerPartnershipResearchPhaseID);


  /**
   * This method gets a list of projectPartnerPartnershipResearchPhase that are active
   * 
   * @return a list from ProjectPartnerPartnershipResearchPhase null if no exist records
   */
  public List<ProjectPartnerPartnershipResearchPhase> findAll();


  /**
   * This method gets a list of ProjectPartnerPartnershipResearchPhase that are active with the given id exists in the
   * system
   * 
   * @return a list from ProjectPartnerPartnershipResearchPhase null if no exist records
   */
  public List<ProjectPartnerPartnershipResearchPhase>
    findParnershipResearchPhaseByPartnership(long projectPartnerPartnershipnId);

  /**
   * This method gets a projectPartnerPartnershipResearchPhase object by a given projectPartnerPartnershipResearchPhase
   * identifier.
   * 
   * @param projectPartnerPartnershipResearchPhaseID is the projectPartnerPartnershipResearchPhase identifier.
   * @return a ProjectPartnerPartnershipResearchPhase object.
   */
  public ProjectPartnerPartnershipResearchPhase
    getProjectPartnerPartnershipResearchPhaseById(long projectPartnerPartnershipResearchPhaseID);

  /**
   * This method saves the information of the given projectPartnerPartnershipResearchPhase
   * 
   * @param projectPartnerPartnershipResearchPhase - is the projectPartnerPartnershipResearchPhase object with the new
   *        information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectPartnerPartnershipResearchPhase was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPartnerPartnershipResearchPhase saveProjectPartnerPartnershipResearchPhase(
    ProjectPartnerPartnershipResearchPhase projectPartnerPartnershipResearchPhase);
}
