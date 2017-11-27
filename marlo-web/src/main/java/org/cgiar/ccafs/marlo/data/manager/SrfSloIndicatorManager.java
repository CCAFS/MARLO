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

import org.cgiar.ccafs.marlo.data.manager.impl.SrfSloIndicatorManagerImpl;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(SrfSloIndicatorManagerImpl.class)
public interface SrfSloIndicatorManager {


  /**
   * This method removes a specific srfSloIndicator value from the database.
   * 
   * @param srfSloIndicatorId is the srfSloIndicator identifier.
   * @return true if the srfSloIndicator was successfully deleted, false otherwise.
   */
  public void deleteSrfSloIndicator(long srfSloIndicatorId);


  /**
   * This method validate if the srfSloIndicator identify with the given id exists in the system.
   * 
   * @param srfSloIndicatorID is a srfSloIndicator identifier.
   * @return true if the srfSloIndicator exists, false otherwise.
   */
  public boolean existSrfSloIndicator(long srfSloIndicatorID);


  /**
   * This method gets a list of srfSloIndicator that are active
   * 
   * @return a list from SrfSloIndicator null if no exist records
   */
  public List<SrfSloIndicator> findAll();


  /**
   * This method gets a srfSloIndicator object by a given srfSloIndicator identifier.
   * 
   * @param srfSloIndicatorID is the srfSloIndicator identifier.
   * @return a SrfSloIndicator object.
   */
  public SrfSloIndicator getSrfSloIndicatorById(long srfSloIndicatorID);

  /**
   * This method saves the information of the given srfSloIndicator
   * 
   * @param srfSloIndicator - is the srfSloIndicator object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the srfSloIndicator was
   *         updated
   *         or -1 is some error occurred.
   */
  public SrfSloIndicator saveSrfSloIndicator(SrfSloIndicator srfSloIndicator);


}
