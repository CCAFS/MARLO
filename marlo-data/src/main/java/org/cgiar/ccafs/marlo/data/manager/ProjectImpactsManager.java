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

import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectImpacts;
import org.cgiar.ccafs.marlo.data.model.ReportProjectImpactsCovid19DTO;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectImpactsManager {


  /**
   * This method removes a specific projectImpacts value from the database.
   * 
   * @param projectImpactsId is the projectImpacts identifier.
   * @return true if the projectImpacts was successfully deleted, false otherwise.
   */
  public void deleteProjectImpacts(long projectImpactsId);


  /**
   * This method validate if the projectImpacts identify with the given id exists in the system.
   * 
   * @param projectImpactsID is a projectImpacts identifier.
   * @return true if the projectImpacts exists, false otherwise.
   */
  public boolean existProjectImpacts(long projectImpactsID);


  /**
   * This method gets a list of projectImpacts that are active
   * 
   * @return a list from ProjectImpacts null if no exist records
   */
  public List<ProjectImpacts> findAll();


  /**
   * This method gets a projectImpacts object by a given projectImpacts identifier.
   * 
   * @param projectImpactsID is the projectImpacts identifier.
   * @return a ProjectImpacts object.
   */
  public ProjectImpacts getProjectImpactsById(long projectImpactsID);

  /**
   * This method gets a projectImpacts object by phase.
   * 
   * @param phase is a object phase.
   * @return a ProjectImpacts object.
   */
  public List<ProjectImpacts> getProjectImpactsByPhase(Phase phase);

  /**
   * This method gets a list of projectImpacts that are active and in a phase specific
   * 
   * @return a list from ReportProjectImpactsCovid19DTO null if no exist records
   */
  public List<ReportProjectImpactsCovid19DTO> getProjectImpactsByProjectAndYears(Phase selectedPhase);

  /**
   * This method saves the information of the given projectImpacts
   * 
   * @param projectImpacts - is the projectImpacts object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectImpacts was
   *         updated
   *         or -1 is some error occurred.
   */

  /**
   * This method gets a projectImpacts object by a given project identifier.
   * 
   * @param projectId is the projectImpacts identifier.
   * @return a ProjectImpacts object.
   */
  public List<ProjectImpacts> getProjectImpactsByProjectId(long projectId);

  /**
   * This method saves the information of the given projectImpacts
   * 
   * @param projectImpacts - is the projectImpacts object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectImpacts was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectImpacts saveProjectImpacts(ProjectImpacts projectImpacts);
}
