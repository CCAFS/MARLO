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

import org.cgiar.ccafs.marlo.data.manager.impl.SrfSloIndicatorTargetManagerImpl;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(SrfSloIndicatorTargetManagerImpl.class)
public interface SrfSloIndicatorTargetManager {


  /**
   * This method removes a specific srfSloIndicatorTarget value from the database.
   * 
   * @param srfSloIndicatorTargetId is the srfSloIndicatorTarget identifier.
   * @return true if the srfSloIndicatorTarget was successfully deleted, false otherwise.
   */
  public boolean deleteSrfSloIndicatorTarget(long srfSloIndicatorTargetId);


  /**
   * This method validate if the srfSloIndicatorTarget identify with the given id exists in the system.
   * 
   * @param srfSloIndicatorTargetID is a srfSloIndicatorTarget identifier.
   * @return true if the srfSloIndicatorTarget exists, false otherwise.
   */
  public boolean existSrfSloIndicatorTarget(long srfSloIndicatorTargetID);


  /**
   * This method gets a list of srfSloIndicatorTarget that are active
   * 
   * @return a list from SrfSloIndicatorTarget null if no exist records
   */
  public List<SrfSloIndicatorTarget> findAll();


  /**
   * This method gets a srfSloIndicatorTarget object by a given srfSloIndicatorTarget identifier.
   * 
   * @param srfSloIndicatorTargetID is the srfSloIndicatorTarget identifier.
   * @return a SrfSloIndicatorTarget object.
   */
  public SrfSloIndicatorTarget getSrfSloIndicatorTargetById(long srfSloIndicatorTargetID);

  /**
   * This method saves the information of the given srfSloIndicatorTarget
   * 
   * @param srfSloIndicatorTarget - is the srfSloIndicatorTarget object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the srfSloIndicatorTarget
   *         was updated
   *         or -1 is some error occurred.
   */
  public long saveSrfSloIndicatorTarget(SrfSloIndicatorTarget srfSloIndicatorTarget);


}
