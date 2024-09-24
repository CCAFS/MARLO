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

import org.cgiar.ccafs.marlo.data.model.AllianceLeversSdgContribution;

import java.util.List;


public interface AllianceLeversSdgContributionDAO {

  /**
   * This method removes a specific allianceLeversSdgContribution value from the database.
   * 
   * @param allianceLeversSdgContributionId is the allianceLeversSdgContribution identifier.
   * @return true if the allianceLeversSdgContribution was successfully deleted, false otherwise.
   */
  public void deleteAllianceLeversSdgContribution(long allianceLeversSdgContributionId);

  /**
   * This method validate if the allianceLeversSdgContribution identify with the given id exists in the system.
   * 
   * @param allianceLeversSdgContributionID is a allianceLeversSdgContribution identifier.
   * @return true if the allianceLeversSdgContribution exists, false otherwise.
   */
  public boolean existAllianceLeversSdgContribution(long allianceLeversSdgContributionID);

  /**
   * This method gets a allianceLeversSdgContribution object by a given allianceLeversSdgContribution identifier.
   * 
   * @param allianceLeversSdgContributionID is the allianceLeversSdgContribution identifier.
   * @return a AllianceLeversSdgContribution object.
   */
  public AllianceLeversSdgContribution find(long id);

  /**
   * This method gets a list of allianceLeversSdgContribution that are active
   * 
   * @return a list from AllianceLeversSdgContribution null if no exist records
   */
  public List<AllianceLeversSdgContribution> findAll();


  List<AllianceLeversSdgContribution> findAllByLeverId(long leverId);

  /**
   * This method saves the information of the given allianceLeversSdgContribution
   * 
   * @param allianceLeversSdgContribution - is the allianceLeversSdgContribution object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         allianceLeversSdgContribution was
   *         updated
   *         or -1 is some error occurred.
   */
  public AllianceLeversSdgContribution save(AllianceLeversSdgContribution allianceLeversSdgContribution);
}
