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

import org.cgiar.ccafs.marlo.data.manager.impl.IpProjectContributionOverviewManagerImpl;
import org.cgiar.ccafs.marlo.data.model.IpProjectContributionOverview;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(IpProjectContributionOverviewManagerImpl.class)
public interface IpProjectContributionOverviewManager {


  /**
   * This method removes a specific ipProjectContributionOverview value from the database.
   * 
   * @param ipProjectContributionOverviewId is the ipProjectContributionOverview identifier.
   * @return true if the ipProjectContributionOverview was successfully deleted, false otherwise.
   */
  public boolean deleteIpProjectContributionOverview(long ipProjectContributionOverviewId);


  /**
   * This method validate if the ipProjectContributionOverview identify with the given id exists in the system.
   * 
   * @param ipProjectContributionOverviewID is a ipProjectContributionOverview identifier.
   * @return true if the ipProjectContributionOverview exists, false otherwise.
   */
  public boolean existIpProjectContributionOverview(long ipProjectContributionOverviewID);


  /**
   * This method gets a list of ipProjectContributionOverview that are active
   * 
   * @return a list from IpProjectContributionOverview null if no exist records
   */
  public List<IpProjectContributionOverview> findAll();


  /**
   * This method gets a ipProjectContributionOverview object by a given ipProjectContributionOverview identifier.
   * 
   * @param ipProjectContributionOverviewID is the ipProjectContributionOverview identifier.
   * @return a IpProjectContributionOverview object.
   */
  public IpProjectContributionOverview getIpProjectContributionOverviewById(long ipProjectContributionOverviewID);

  /**
   * @param mogId
   * @param year
   * @return
   */
  public List<IpProjectContributionOverview> getProjectContributionOverviewsSytnhesis(long mogId, int year,
    long program);

  /**
   * @param mogId
   * @param year
   * @return
   */
  public List<IpProjectContributionOverview> getProjectContributionOverviewsSytnhesisGlobal(long mogId, int year,
    long program);

  /**
   * This method saves the information of the given ipProjectContributionOverview
   * 
   * @param ipProjectContributionOverview - is the ipProjectContributionOverview object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         ipProjectContributionOverview was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveIpProjectContributionOverview(IpProjectContributionOverview ipProjectContributionOverview);


}
