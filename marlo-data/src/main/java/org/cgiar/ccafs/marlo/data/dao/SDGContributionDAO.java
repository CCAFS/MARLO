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

import org.cgiar.ccafs.marlo.data.model.SDGContribution;

import java.util.List;


public interface SDGContributionDAO {

  /**
   * This method removes a specific sDGContribution value from the database.
   * 
   * @param sDGContributionId is the sDGContribution identifier.
   * @return true if the sDGContribution was successfully deleted, false otherwise.
   */
  public void deleteSDGContribution(long sDGContributionId);

  /**
   * This method validate if the sDGContribution identify with the given id exists in the system.
   * 
   * @param sDGContributionID is a sDGContribution identifier.
   * @return true if the sDGContribution exists, false otherwise.
   */
  public boolean existSDGContribution(long sDGContributionID);

  /**
   * This method gets a sDGContribution object by a given sDGContribution identifier.
   * 
   * @param sDGContributionID is the sDGContribution identifier.
   * @return a SDGContribution object.
   */
  public SDGContribution find(long id);

  /**
   * This method gets a list of sDGContribution that are active
   * 
   * @return a list from SDGContribution null if no exist records
   */
  public List<SDGContribution> findAll();


  List<SDGContribution> findSDGcontributionByExpectedPhaseAndLever(long phase, long expectedId, long lever,
    int isPrimary);

  /**
   * This method saves the information of the given sDGContribution
   * 
   * @param sDGContribution - is the sDGContribution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the sDGContribution was
   *         updated
   *         or -1 is some error occurred.
   */
  public SDGContribution save(SDGContribution sDGContribution);
}
