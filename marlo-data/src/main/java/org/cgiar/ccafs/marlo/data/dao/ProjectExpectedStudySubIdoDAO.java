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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;

import java.util.List;


public interface ProjectExpectedStudySubIdoDAO {

  /**
   * This method removes a specific projectExpectedStudySubIdo value from the database.
   * 
   * @param projectExpectedStudySubIdoId is the projectExpectedStudySubIdo identifier.
   * @return true if the projectExpectedStudySubIdo was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudySubIdo(long projectExpectedStudySubIdoId);

  /**
   * This method validate if the projectExpectedStudySubIdo identify with the given id exists in the system.
   * 
   * @param projectExpectedStudySubIdoID is a projectExpectedStudySubIdo identifier.
   * @return true if the projectExpectedStudySubIdo exists, false otherwise.
   */
  public boolean existProjectExpectedStudySubIdo(long projectExpectedStudySubIdoID);

  /**
   * This method gets a projectExpectedStudySubIdo object by a given projectExpectedStudySubIdo identifier.
   * 
   * @param projectExpectedStudySubIdoID is the projectExpectedStudySubIdo identifier.
   * @return a ProjectExpectedStudySubIdo object.
   */
  public ProjectExpectedStudySubIdo find(long id);

  /**
   * This method gets a list of projectExpectedStudySubIdo that are active
   * 
   * @return a list from ProjectExpectedStudySubIdo null if no exist records
   */
  public List<ProjectExpectedStudySubIdo> findAll();


  /**
   * This method saves the information of the given projectExpectedStudySubIdo
   * 
   * @param projectExpectedStudySubIdo - is the projectExpectedStudySubIdo object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudySubIdo was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudySubIdo save(ProjectExpectedStudySubIdo projectExpectedStudySubIdo);
}
