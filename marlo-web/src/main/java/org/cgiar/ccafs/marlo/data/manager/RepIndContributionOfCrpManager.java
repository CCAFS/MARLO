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

import org.cgiar.ccafs.marlo.data.model.RepIndContributionOfCrp;

import java.util.List;


/**
 * @author CCAFS
 */

public interface RepIndContributionOfCrpManager {


  /**
   * This method removes a specific repIndContributionOfCrp value from the database.
   * 
   * @param repIndContributionOfCrpId is the repIndContributionOfCrp identifier.
   * @return true if the repIndContributionOfCrp was successfully deleted, false otherwise.
   */
  public void deleteRepIndContributionOfCrp(long repIndContributionOfCrpId);


  /**
   * This method validate if the repIndContributionOfCrp identify with the given id exists in the system.
   * 
   * @param repIndContributionOfCrpID is a repIndContributionOfCrp identifier.
   * @return true if the repIndContributionOfCrp exists, false otherwise.
   */
  public boolean existRepIndContributionOfCrp(long repIndContributionOfCrpID);


  /**
   * This method gets a list of repIndContributionOfCrp that are active
   * 
   * @return a list from RepIndContributionOfCrp null if no exist records
   */
  public List<RepIndContributionOfCrp> findAll();


  /**
   * This method gets a repIndContributionOfCrp object by a given repIndContributionOfCrp identifier.
   * 
   * @param repIndContributionOfCrpID is the repIndContributionOfCrp identifier.
   * @return a RepIndContributionOfCrp object.
   */
  public RepIndContributionOfCrp getRepIndContributionOfCrpById(long repIndContributionOfCrpID);

  /**
   * This method saves the information of the given repIndContributionOfCrp
   * 
   * @param repIndContributionOfCrp - is the repIndContributionOfCrp object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndContributionOfCrp was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndContributionOfCrp saveRepIndContributionOfCrp(RepIndContributionOfCrp repIndContributionOfCrp);


}
