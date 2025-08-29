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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPRMS;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationPRMSManager {


  /**
   * This method removes a specific ProjectInnovationPRMS value from the database.
   * 
   * @param ProjectInnovationPRMSId is the ProjectInnovationPRMS identifier.
   * @return true if the ProjectInnovationPRMS was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationPRMS(long ProjectInnovationPRMSId);


  /**
   * This method validate if the ProjectInnovationPRMS identify with the given id exists in the system.
   * 
   * @param ProjectInnovationPRMSID is a ProjectInnovationPRMS identifier.
   * @return true if the ProjectInnovationPRMS exists, false otherwise.
   */
  public boolean existProjectInnovationPRMS(long ProjectInnovationPRMSID);


  /**
   * This method gets a list of ProjectInnovationPRMS that are active
   * 
   * @return a list from ProjectInnovationPRMS null if no exist records
   */
  public List<ProjectInnovationPRMS> findAll();

  /**
   * This method gets a list of ProjectInnovationPRMS that are active filtered by ProjectInnovationID and PhaseID
   * 
   * @param ProjectInnovationID is the ProjectInnovation identifier.
   * @param phaseID is the Phase identifier.
   * @return a list from ProjectInnovationPRMS null if no exist records
   */
  public List<ProjectInnovationPRMS> findByInnovationIDAndPhaseID(long projectInnovationID, long phaseID);


  /**
   * This method gets a ProjectInnovationPRMS object by a given ProjectInnovationPRMS identifier.
   * 
   * @param ProjectInnovationPRMSID is the ProjectInnovationPRMS identifier.
   * @param PhaseID is the ProjectInnovationPRMS identifier.
   * @return a ProjectInnovationPRMS object.
   */
  public ProjectInnovationPRMS getProjectInnovationPRMSById(long ProjectInnovationPRMSID);

  /**
   * This method saves the information of the given ProjectInnovationPRMS
   * 
   * @param ProjectInnovationPRMS - is the ProjectInnovationPRMS object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the ProjectInnovationPRMS
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationPRMS saveProjectInnovationPRMS(ProjectInnovationPRMS ProjectInnovationPRMS);


}
