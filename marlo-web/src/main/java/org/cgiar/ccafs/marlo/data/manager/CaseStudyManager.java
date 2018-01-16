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

import org.cgiar.ccafs.marlo.data.model.CaseStudy;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface CaseStudyManager {


  /**
   * This method removes a specific caseStudy value from the database.
   * 
   * @param caseStudyId is the caseStudy identifier.
   * @return true if the caseStudy was successfully deleted, false otherwise.
   */
  public void deleteCaseStudy(long caseStudyId);


  /**
   * This method validate if the caseStudy identify with the given id exists in the system.
   * 
   * @param caseStudyID is a caseStudy identifier.
   * @return true if the caseStudy exists, false otherwise.
   */
  public boolean existCaseStudy(long caseStudyID);


  /**
   * This method gets a list of caseStudy that are active
   * 
   * @return a list from CaseStudy null if no exist records
   */
  public List<CaseStudy> findAll();


  /**
   * This method gets a caseStudy object by a given caseStudy identifier.
   * 
   * @param caseStudyID is the caseStudy identifier.
   * @return a CaseStudy object.
   */
  public CaseStudy getCaseStudyById(long caseStudyID);

  /**
   * This method saves the information of the given caseStudy
   * 
   * @param caseStudy - is the caseStudy object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the caseStudy was
   *         updated
   *         or -1 is some error occurred.
   */
  public CaseStudy saveCaseStudy(CaseStudy caseStudy);

  public CaseStudy saveCaseStudy(CaseStudy caseStudy, String section, List<String> relationsName);


}
