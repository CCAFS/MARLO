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

import org.cgiar.ccafs.marlo.data.model.DeliverableGeographicRegion;

import java.util.List;


public interface DeliverableGeographicRegionDAO {

  /**
   * This method removes a specific deliverableGeographicRegion value from the database.
   * 
   * @param deliverableGeographicRegionId is the deliverableGeographicRegion identifier.
   * @return true if the deliverableGeographicRegion was successfully deleted, false otherwise.
   */
  public void deleteDeliverableGeographicRegion(long deliverableGeographicRegionId);

  /**
   * This method validate if the deliverableGeographicRegion identify with the given id exists in the system.
   * 
   * @param deliverableGeographicRegionID is a deliverableGeographicRegion identifier.
   * @return true if the deliverableGeographicRegion exists, false otherwise.
   */
  public boolean existDeliverableGeographicRegion(long deliverableGeographicRegionID);

  /**
   * This method gets a deliverableGeographicRegion object by a given deliverableGeographicRegion identifier.
   * 
   * @param deliverableGeographicRegionID is the deliverableGeographicRegion identifier.
   * @return a DeliverableGeographicRegion object.
   */
  public DeliverableGeographicRegion find(long id);

  /**
   * This method gets a list of deliverableGeographicRegion that are active
   * 
   * @return a list from DeliverableGeographicRegion null if no exist records
   */
  public List<DeliverableGeographicRegion> findAll();


  public List<DeliverableGeographicRegion> getDeliverableGeographicRegionbyPhase(long deliverableID, long phaseID);

  /**
   * This method saves the information of the given deliverableGeographicRegion
   * 
   * @param deliverableGeographicRegion - is the deliverableGeographicRegion object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         deliverableGeographicRegion was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableGeographicRegion save(DeliverableGeographicRegion deliverableGeographicRegion);
}
