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

import org.cgiar.ccafs.marlo.data.manager.impl.IpIndicatorManagerImpl;
import org.cgiar.ccafs.marlo.data.model.IpIndicator;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(IpIndicatorManagerImpl.class)
public interface IpIndicatorManager {


  /**
   * This method removes a specific ipIndicator value from the database.
   * 
   * @param ipIndicatorId is the ipIndicator identifier.
   * @return true if the ipIndicator was successfully deleted, false otherwise.
   */
  public boolean deleteIpIndicator(long ipIndicatorId);

  /**
   * This method validate if the ipIndicator identify with the given id exists in the system.
   * 
   * @param ipIndicatorID is a ipIndicator identifier.
   * @return true if the ipIndicator exists, false otherwise.
   */
  public boolean existIpIndicator(long ipIndicatorID);


  /**
   * This method gets a list of ipIndicator that are active
   * 
   * @return a list from IpIndicator null if no exist records
   */
  public List<IpIndicator> findAll();


  public List<IpIndicator> findOtherContributions(long projectID);

  public List<IpIndicator> getIndicatorsByElementID(long elementID);

  public List<IpIndicator> getIndicatorsFlagShips();

  /**
   * This method gets a ipIndicator object by a given ipIndicator identifier.
   * 
   * @param ipIndicatorID is the ipIndicator identifier.
   * @return a IpIndicator object.
   */
  public IpIndicator getIpIndicatorById(long ipIndicatorID);

  public long saveIpIndicator(IpIndicator ipIndicator);

}
