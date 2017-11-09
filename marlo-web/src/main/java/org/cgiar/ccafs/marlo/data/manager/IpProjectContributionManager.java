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

import org.cgiar.ccafs.marlo.data.manager.impl.IpProjectContributionManagerImpl;
import org.cgiar.ccafs.marlo.data.model.IpProjectContribution;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(IpProjectContributionManagerImpl.class)
public interface IpProjectContributionManager {


  /**
   * This method removes a specific ipProjectContribution value from the database.
   * 
   * @param ipProjectContributionId is the ipProjectContribution identifier.
   * @return true if the ipProjectContribution was successfully deleted, false otherwise.
   */
  public void deleteIpProjectContribution(long ipProjectContributionId);


  /**
   * This method validate if the ipProjectContribution identify with the given id exists in the system.
   * 
   * @param ipProjectContributionID is a ipProjectContribution identifier.
   * @return true if the ipProjectContribution exists, false otherwise.
   */
  public boolean existIpProjectContribution(long ipProjectContributionID);


  /**
   * This method gets a list of ipProjectContribution that are active
   * 
   * @return a list from IpProjectContribution null if no exist records
   */
  public List<IpProjectContribution> findAll();


  /**
   * This method gets a ipProjectContribution object by a given ipProjectContribution identifier.
   * 
   * @param ipProjectContributionID is the ipProjectContribution identifier.
   * @return a IpProjectContribution object.
   */
  public IpProjectContribution getIpProjectContributionById(long ipProjectContributionID);

  /**
   * This method saves the information of the given ipProjectContribution
   * 
   * @param ipProjectContribution - is the ipProjectContribution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the ipProjectContribution was
   *         updated
   *         or -1 is some error occurred.
   */
  public IpProjectContribution saveIpProjectContribution(IpProjectContribution ipProjectContribution);


}
