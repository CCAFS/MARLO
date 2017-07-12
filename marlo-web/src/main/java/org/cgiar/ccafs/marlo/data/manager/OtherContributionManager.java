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

import org.cgiar.ccafs.marlo.data.manager.impl.OtherContributionManagerImpl;
import org.cgiar.ccafs.marlo.data.model.OtherContribution;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(OtherContributionManagerImpl.class)
public interface OtherContributionManager {


  /**
   * This method removes a specific otherContribution value from the database.
   * 
   * @param otherContributionId is the otherContribution identifier.
   * @return true if the otherContribution was successfully deleted, false otherwise.
   */
  public boolean deleteOtherContribution(long otherContributionId);


  /**
   * This method validate if the otherContribution identify with the given id exists in the system.
   * 
   * @param otherContributionID is a otherContribution identifier.
   * @return true if the otherContribution exists, false otherwise.
   */
  public boolean existOtherContribution(long otherContributionID);


  /**
   * This method gets a list of otherContribution that are active
   * 
   * @return a list from OtherContribution null if no exist records
   */
  public List<OtherContribution> findAll();


  /**
   * This method gets a otherContribution object by a given otherContribution identifier.
   * 
   * @param otherContributionID is the otherContribution identifier.
   * @return a OtherContribution object.
   */
  public OtherContribution getOtherContributionById(long otherContributionID);

  /**
   * This method saves the information of the given otherContribution
   * 
   * @param otherContribution - is the otherContribution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the otherContribution was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveOtherContribution(OtherContribution otherContribution);


}
