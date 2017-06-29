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

import org.cgiar.ccafs.marlo.data.dao.mysql.DeliverablePublicationMetadataMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(DeliverablePublicationMetadataMySQLDAO.class)
public interface DeliverablePublicationMetadataDAO {

  /**
   * This method removes a specific deliverablePublicationMetadata value from the database.
   * 
   * @param deliverablePublicationMetadataId is the deliverablePublicationMetadata identifier.
   * @return true if the deliverablePublicationMetadata was successfully deleted, false otherwise.
   */
  public boolean deleteDeliverablePublicationMetadata(long deliverablePublicationMetadataId);

  /**
   * This method validate if the deliverablePublicationMetadata identify with the given id exists in the system.
   * 
   * @param deliverablePublicationMetadataID is a deliverablePublicationMetadata identifier.
   * @return true if the deliverablePublicationMetadata exists, false otherwise.
   */
  public boolean existDeliverablePublicationMetadata(long deliverablePublicationMetadataID);

  /**
   * This method gets a deliverablePublicationMetadata object by a given deliverablePublicationMetadata identifier.
   * 
   * @param deliverablePublicationMetadataID is the deliverablePublicationMetadata identifier.
   * @return a DeliverablePublicationMetadata object.
   */
  public DeliverablePublicationMetadata find(long id);

  /**
   * This method gets a list of deliverablePublicationMetadata that are active
   * 
   * @return a list from DeliverablePublicationMetadata null if no exist records
   */
  public List<DeliverablePublicationMetadata> findAll();


  /**
   * This method saves the information of the given deliverablePublicationMetadata
   * 
   * @param deliverablePublicationMetadata - is the deliverablePublicationMetadata object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverablePublicationMetadata was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(DeliverablePublicationMetadata deliverablePublicationMetadata);
}
