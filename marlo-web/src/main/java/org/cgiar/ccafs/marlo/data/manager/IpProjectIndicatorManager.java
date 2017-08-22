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

import org.cgiar.ccafs.marlo.data.manager.impl.IpProjectIndicatorManagerImpl;
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(IpProjectIndicatorManagerImpl.class)
public interface IpProjectIndicatorManager {


  /**
   * This method removes a specific ipProjectIndicator value from the database.
   * 
   * @param ipProjectIndicatorId is the ipProjectIndicator identifier.
   * @return true if the ipProjectIndicator was successfully deleted, false otherwise.
   */
  public void deleteIpProjectIndicator(long ipProjectIndicatorId);


  /**
   * This method validate if the ipProjectIndicator identify with the given id exists in the system.
   * 
   * @param ipProjectIndicatorID is a ipProjectIndicator identifier.
   * @return true if the ipProjectIndicator exists, false otherwise.
   */
  public boolean existIpProjectIndicator(long ipProjectIndicatorID);


  /**
   * This method gets a list of ipProjectIndicator that are active
   * 
   * @return a list from IpProjectIndicator null if no exist records
   */
  public List<IpProjectIndicator> findAll();


  /**
   * This method gets a ipProjectIndicator object by a given ipProjectIndicator identifier.
   * 
   * @param ipProjectIndicatorID is the ipProjectIndicator identifier.
   * @return a IpProjectIndicator object.
   */
  public IpProjectIndicator getIpProjectIndicatorById(long ipProjectIndicatorID);

  /**
   * This method saves the information of the given ipIndicator
   * 
   * @param ipIndicator - is the ipIndicator object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the ipIndicator was
   *         updated
   *         or -1 is some error occurred.
   */
  public List<IpProjectIndicator> getProjectIndicators(int year, long indicator, Long program, long midOutcome);


  /**
   * This method saves the information of the given ipProjectIndicator
   * 
   * @param ipProjectIndicator - is the ipProjectIndicator object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the ipProjectIndicator was
   *         updated
   *         or -1 is some error occurred.
   */
  public IpProjectIndicator saveIpProjectIndicator(IpProjectIndicator ipProjectIndicator);


}
