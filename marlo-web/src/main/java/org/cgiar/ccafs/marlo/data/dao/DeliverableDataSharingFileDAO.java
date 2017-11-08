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

import org.cgiar.ccafs.marlo.data.dao.mysql.DeliverableDataSharingFileMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(DeliverableDataSharingFileMySQLDAO.class)
public interface DeliverableDataSharingFileDAO {

  /**
   * This method removes a specific deliverableDataSharingFile value from the database.
   * 
   * @param deliverableDataSharingFileId is the deliverableDataSharingFile identifier.
   * @return true if the deliverableDataSharingFile was successfully deleted, false otherwise.
   */
  public void deleteDeliverableDataSharingFile(long deliverableDataSharingFileId);

  /**
   * This method validate if the deliverableDataSharingFile identify with the given id exists in the system.
   * 
   * @param deliverableDataSharingFileID is a deliverableDataSharingFile identifier.
   * @return true if the deliverableDataSharingFile exists, false otherwise.
   */
  public boolean existDeliverableDataSharingFile(long deliverableDataSharingFileID);

  /**
   * This method gets a deliverableDataSharingFile object by a given deliverableDataSharingFile identifier.
   * 
   * @param deliverableDataSharingFileID is the deliverableDataSharingFile identifier.
   * @return a DeliverableDataSharingFile object.
   */
  public DeliverableDataSharingFile find(long id);

  /**
   * This method gets a list of deliverableDataSharingFile that are active
   * 
   * @return a list from DeliverableDataSharingFile null if no exist records
   */
  public List<DeliverableDataSharingFile> findAll();


  /**
   * This method saves the information of the given deliverableDataSharingFile
   * 
   * @param deliverableDataSharingFile - is the deliverableDataSharingFile object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableDataSharingFile was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableDataSharingFile save(DeliverableDataSharingFile deliverableDataSharingFile);
}
