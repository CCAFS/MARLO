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

import org.cgiar.ccafs.marlo.data.model.RelatedLeversSDGContribution;

import java.util.List;


/**
 * @author CCAFS
 */

public interface RelatedLeversSDGContributionManager {


  /**
   * This method removes a specific relatedLeversSDGContribution value from the database.
   * 
   * @param relatedLeversSDGContributionId is the relatedLeversSDGContribution identifier.
   * @return true if the relatedLeversSDGContribution was successfully deleted, false otherwise.
   */
  public void deleteRelatedLeversSDGContribution(long relatedLeversSDGContributionId);


  /**
   * This method validate if the relatedLeversSDGContribution identify with the given id exists in the system.
   * 
   * @param relatedLeversSDGContributionID is a relatedLeversSDGContribution identifier.
   * @return true if the relatedLeversSDGContribution exists, false otherwise.
   */
  public boolean existRelatedLeversSDGContribution(long relatedLeversSDGContributionID);


  /**
   * This method gets a list of relatedLeversSDGContribution that are active
   * 
   * @return a list from RelatedLeversSDGContribution null if no exist records
   */
  public List<RelatedLeversSDGContribution> findAll();


  /**
   * This method gets a relatedLeversSDGContribution object by a given relatedLeversSDGContribution identifier.
   * 
   * @param relatedLeversSDGContributionID is the relatedLeversSDGContribution identifier.
   * @return a RelatedLeversSDGContribution object.
   */
  public RelatedLeversSDGContribution getRelatedLeversSDGContributionById(long relatedLeversSDGContributionID);

  /**
   * This method saves the information of the given relatedLeversSDGContribution
   * 
   * @param relatedLeversSDGContribution - is the relatedLeversSDGContribution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the relatedLeversSDGContribution was
   *         updated
   *         or -1 is some error occurred.
   */
  public RelatedLeversSDGContribution saveRelatedLeversSDGContribution(RelatedLeversSDGContribution relatedLeversSDGContribution);


}
