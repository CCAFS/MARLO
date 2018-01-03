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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.DeliverableDataSharingFileDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableDataSharingFileManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableDataSharingFileManagerImpl implements DeliverableDataSharingFileManager {


  private DeliverableDataSharingFileDAO deliverableDataSharingFileDAO;
  // Managers


  @Inject
  public DeliverableDataSharingFileManagerImpl(DeliverableDataSharingFileDAO deliverableDataSharingFileDAO) {
    this.deliverableDataSharingFileDAO = deliverableDataSharingFileDAO;


  }

  @Override
  public void deleteDeliverableDataSharingFile(long deliverableDataSharingFileId) {

    deliverableDataSharingFileDAO.deleteDeliverableDataSharingFile(deliverableDataSharingFileId);
  }

  @Override
  public boolean existDeliverableDataSharingFile(long deliverableDataSharingFileID) {

    return deliverableDataSharingFileDAO.existDeliverableDataSharingFile(deliverableDataSharingFileID);
  }

  @Override
  public List<DeliverableDataSharingFile> findAll() {

    return deliverableDataSharingFileDAO.findAll();

  }

  @Override
  public DeliverableDataSharingFile getDeliverableDataSharingFileById(long deliverableDataSharingFileID) {

    return deliverableDataSharingFileDAO.find(deliverableDataSharingFileID);
  }

  @Override
  public DeliverableDataSharingFile saveDeliverableDataSharingFile(DeliverableDataSharingFile deliverableDataSharingFile) {

    return deliverableDataSharingFileDAO.save(deliverableDataSharingFile);
  }


}
