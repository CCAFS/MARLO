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

import org.cgiar.ccafs.marlo.data.model.RelatedAllianceLeverSdgContribution;

import java.util.List;


/**
 * @author CCAFS
 */

public interface RelatedAllianceLeverSdgContributionManager {


  /**
   * This method removes a specific relatedAllianceLeverSdgContribution value from the database.
   * 
   * @param relatedAllianceLeverSdgContributionId is the relatedAllianceLeverSdgContribution identifier.
   * @return true if the relatedAllianceLeverSdgContribution was successfully deleted, false otherwise.
   */
  public void deleteRelatedAllianceLeverSdgContribution(long relatedAllianceLeverSdgContributionId);


  /**
   * This method validate if the relatedAllianceLeverSdgContribution identify with the given id exists in the system.
   * 
   * @param relatedAllianceLeverSdgContributionID is a relatedAllianceLeverSdgContribution identifier.
   * @return true if the relatedAllianceLeverSdgContribution exists, false otherwise.
   */
  public boolean existRelatedAllianceLeverSdgContribution(long relatedAllianceLeverSdgContributionID);


  /**
   * This method gets a list of relatedAllianceLeverSdgContribution that are active
   * 
   * @return a list from RelatedAllianceLeverSdgContribution null if no exist records
   */
  public List<RelatedAllianceLeverSdgContribution> findAll();


  /**
   * This method gets a relatedAllianceLeverSdgContribution object by a given relatedAllianceLeverSdgContribution identifier.
   * 
   * @param relatedAllianceLeverSdgContributionID is the relatedAllianceLeverSdgContribution identifier.
   * @return a RelatedAllianceLeverSdgContribution object.
   */
  public RelatedAllianceLeverSdgContribution getRelatedAllianceLeverSdgContributionById(long relatedAllianceLeverSdgContributionID);

  /**
   * This method saves the information of the given relatedAllianceLeverSdgContribution
   * 
   * @param relatedAllianceLeverSdgContribution - is the relatedAllianceLeverSdgContribution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the relatedAllianceLeverSdgContribution was
   *         updated
   *         or -1 is some error occurred.
   */
  public RelatedAllianceLeverSdgContribution saveRelatedAllianceLeverSdgContribution(RelatedAllianceLeverSdgContribution relatedAllianceLeverSdgContribution);


}
