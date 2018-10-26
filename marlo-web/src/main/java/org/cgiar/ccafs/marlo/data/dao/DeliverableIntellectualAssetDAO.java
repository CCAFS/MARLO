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

import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;


public interface DeliverableIntellectualAssetDAO {

  /**
   * This method removes a specific deliverableIntellectualAsset value from the database.
   * 
   * @param deliverableIntellectualAssetId is the deliverableIntellectualAsset identifier.
   * @return true if the deliverableIntellectualAsset was successfully deleted, false otherwise.
   */
  public void deleteDeliverableIntellectualAsset(long deliverableIntellectualAssetId);

  /**
   * This method validate if the deliverableIntellectualAsset identify with the given id exists in the system.
   * 
   * @param deliverableIntellectualAssetID is a deliverableIntellectualAsset identifier.
   * @return true if the deliverableIntellectualAsset exists, false otherwise.
   */
  public boolean existDeliverableIntellectualAsset(long deliverableIntellectualAssetID);

  /**
   * This method gets a deliverableIntellectualAsset object by a given deliverableIntellectualAsset identifier.
   * 
   * @param deliverableIntellectualAssetID is the deliverableIntellectualAsset identifier.
   * @return a DeliverableIntellectualAsset object.
   */
  public DeliverableIntellectualAsset find(long id);

  /**
   * This method gets a list of deliverableIntellectualAsset that are active
   * 
   * @return a list from DeliverableIntellectualAsset null if no exist records
   */
  public List<DeliverableIntellectualAsset> findAll();


  public DeliverableIntellectualAsset findIntellectualAssetByPhaseAndDeliverable(Phase phase, Deliverable deliverable);

  /**
   * This method saves the information of the given deliverableIntellectualAsset
   * 
   * @param deliverableIntellectualAsset - is the deliverableIntellectualAsset object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         deliverableIntellectualAsset was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableIntellectualAsset save(DeliverableIntellectualAsset deliverableIntellectualAsset);
}
