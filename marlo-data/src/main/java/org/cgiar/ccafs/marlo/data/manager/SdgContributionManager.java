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

import org.cgiar.ccafs.marlo.data.model.SdgContribution;

import java.util.List;


/**
 * @author CCAFS
 */

public interface SdgContributionManager {


  /**
   * This method removes a specific sdgContribution value from the database.
   * 
   * @param sdgContributionId is the sdgContribution identifier.
   * @return true if the sdgContribution was successfully deleted, false otherwise.
   */
  public void deleteSdgContribution(long sdgContributionId);


  /**
   * This method validate if the sdgContribution identify with the given id exists in the system.
   * 
   * @param sdgContributionID is a sdgContribution identifier.
   * @return true if the sdgContribution exists, false otherwise.
   */
  public boolean existSdgContribution(long sdgContributionID);


  /**
   * This method gets a list of sdgContribution that are active
   * 
   * @return a list from SdgContribution null if no exist records
   */
  public List<SdgContribution> findAll();


  /**
   * This method gets a sdgContribution object by a given sdgContribution identifier.
   * 
   * @param sdgContributionID is the sdgContribution identifier.
   * @return a SdgContribution object.
   */
  public SdgContribution getSdgContributionById(long sdgContributionID);

  /**
   * This method saves the information of the given sdgContribution
   * 
   * @param sdgContribution - is the sdgContribution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the sdgContribution was
   *         updated
   *         or -1 is some error occurred.
   */
  public SdgContribution saveSdgContribution(SdgContribution sdgContribution);


}
