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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSubIdo;

import java.util.List;


public interface ProjectInnovationSubIdoDAO {

  /**
   * This method removes a specific projectInnovationSubIdo value from the database.
   * 
   * @param projectInnovationSubIdoId is the projectInnovationSubIdo identifier.
   * @return true if the projectInnovationSubIdo was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationSubIdo(long projectInnovationSubIdoId);

  /**
   * This method validate if the projectInnovationSubIdo identify with the given id exists in the system.
   * 
   * @param projectInnovationSubIdoID is a projectInnovationSubIdo identifier.
   * @return true if the projectInnovationSubIdo exists, false otherwise.
   */
  public boolean existProjectInnovationSubIdo(long projectInnovationSubIdoID);

  /**
   * This method gets a projectInnovationSubIdo object by a given projectInnovationSubIdo identifier.
   * 
   * @param projectInnovationSubIdoID is the projectInnovationSubIdo identifier.
   * @return a ProjectInnovationSubIdo object.
   */
  public ProjectInnovationSubIdo find(long id);

  /**
   * This method gets a list of projectInnovationSubIdo that are active
   * 
   * @return a list from ProjectInnovationSubIdo null if no exist records
   */
  public List<ProjectInnovationSubIdo> findAll();


  /**
   * This method gets a projectInnovationSubIdo object by a given projectInnovation,SubIdo and Phase identifier.
   * 
   * @param innovationID is the projectInnovation identifier.
   * @param subIdoID is the SubIDO identifier.
   * @param phaseID is the phase identifier
   * @return a ProjectInnovationSubIdo object.
   */
  public ProjectInnovationSubIdo getProjectInnovationSubIdoByPhase(Long innovationID, Long subIdoID, Long phaseID);

  /**
   * This method saves the information of the given projectInnovationSubIdo
   * 
   * @param projectInnovationSubIdo - is the projectInnovationSubIdo object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationSubIdo
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationSubIdo save(ProjectInnovationSubIdo projectInnovationSubIdo);
}
