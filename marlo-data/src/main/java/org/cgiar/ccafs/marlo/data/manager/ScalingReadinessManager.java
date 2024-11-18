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

import org.cgiar.ccafs.marlo.data.model.ScalingReadiness;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ScalingReadinessManager {


  /**
   * This method removes a specific scalingReadiness value from the database.
   * 
   * @param scalingReadinessId is the scalingReadiness identifier.
   * @return true if the scalingReadiness was successfully deleted, false otherwise.
   */
  public void deleteScalingReadiness(long scalingReadinessId);


  /**
   * This method validate if the scalingReadiness identify with the given id exists in the system.
   * 
   * @param scalingReadinessID is a scalingReadiness identifier.
   * @return true if the scalingReadiness exists, false otherwise.
   */
  public boolean existScalingReadiness(long scalingReadinessID);


  /**
   * This method gets a list of scalingReadiness that are active
   * 
   * @return a list from ScalingReadiness null if no exist records
   */
  public List<ScalingReadiness> findAll();


  /**
   * This method gets a scalingReadiness object by a given scalingReadiness identifier.
   * 
   * @param scalingReadinessID is the scalingReadiness identifier.
   * @return a ScalingReadiness object.
   */
  public ScalingReadiness getScalingReadinessById(long scalingReadinessID);

  /**
   * This method saves the information of the given scalingReadiness
   * 
   * @param scalingReadiness - is the scalingReadiness object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the scalingReadiness was
   *         updated
   *         or -1 is some error occurred.
   */
  public ScalingReadiness saveScalingReadiness(ScalingReadiness scalingReadiness);


}
