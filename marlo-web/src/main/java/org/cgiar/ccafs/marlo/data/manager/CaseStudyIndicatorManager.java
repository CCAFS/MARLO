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

import org.cgiar.ccafs.marlo.data.model.CaseStudyIndicator;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface CaseStudyIndicatorManager {


  /**
   * This method removes a specific caseStudyIndicator value from the database.
   * 
   * @param caseStudyIndicatorId is the caseStudyIndicator identifier.
   * @return true if the caseStudyIndicator was successfully deleted, false otherwise.
   */
  public void deleteCaseStudyIndicator(long caseStudyIndicatorId);


  /**
   * This method validate if the caseStudyIndicator identify with the given id exists in the system.
   * 
   * @param caseStudyIndicatorID is a caseStudyIndicator identifier.
   * @return true if the caseStudyIndicator exists, false otherwise.
   */
  public boolean existCaseStudyIndicator(long caseStudyIndicatorID);


  /**
   * This method gets a list of caseStudyIndicator that are active
   * 
   * @return a list from CaseStudyIndicator null if no exist records
   */
  public List<CaseStudyIndicator> findAll();


  /**
   * This method gets a caseStudyIndicator object by a given caseStudyIndicator identifier.
   * 
   * @param caseStudyIndicatorID is the caseStudyIndicator identifier.
   * @return a CaseStudyIndicator object.
   */
  public CaseStudyIndicator getCaseStudyIndicatorById(long caseStudyIndicatorID);

  /**
   * This method saves the information of the given caseStudyIndicator
   * 
   * @param caseStudyIndicator - is the caseStudyIndicator object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the caseStudyIndicator was
   *         updated
   *         or -1 is some error occurred.
   */
  public CaseStudyIndicator saveCaseStudyIndicator(CaseStudyIndicator caseStudyIndicator);


}
