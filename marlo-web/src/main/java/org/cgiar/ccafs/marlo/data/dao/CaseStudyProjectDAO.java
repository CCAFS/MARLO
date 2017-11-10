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

import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;

import java.util.List;


public interface CaseStudyProjectDAO {

  /**
   * This method removes a specific caseStudyProject value from the database.
   * 
   * @param caseStudyProjectId is the caseStudyProject identifier.
   * @return true if the caseStudyProject was successfully deleted, false otherwise.
   */
  public void deleteCaseStudyProject(long caseStudyProjectId);

  /**
   * This method validate if the caseStudyProject identify with the given id exists in the system.
   * 
   * @param caseStudyProjectID is a caseStudyProject identifier.
   * @return true if the caseStudyProject exists, false otherwise.
   */
  public boolean existCaseStudyProject(long caseStudyProjectID);

  /**
   * This method gets a caseStudyProject object by a given caseStudyProject identifier.
   * 
   * @param caseStudyProjectID is the caseStudyProject identifier.
   * @return a CaseStudyProject object.
   */
  public CaseStudyProject find(long id);

  /**
   * This method gets a list of caseStudyProject that are active
   * 
   * @return a list from CaseStudyProject null if no exist records
   */
  public List<CaseStudyProject> findAll();


  /**
   * This method saves the information of the given caseStudyProject
   * 
   * @param caseStudyProject - is the caseStudyProject object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the caseStudyProject was
   *         updated
   *         or -1 is some error occurred.
   */
  public CaseStudyProject save(CaseStudyProject caseStudyProject);
}
