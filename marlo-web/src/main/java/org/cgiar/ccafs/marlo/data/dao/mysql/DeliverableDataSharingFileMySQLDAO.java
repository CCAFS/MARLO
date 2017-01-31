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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.DeliverableDataSharingFileDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;

import java.util.List;

import com.google.inject.Inject;

public class DeliverableDataSharingFileMySQLDAO implements DeliverableDataSharingFileDAO {

  private StandardDAO dao;

  @Inject
  public DeliverableDataSharingFileMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteDeliverableDataSharingFile(long deliverableDataSharingFileId) {
    DeliverableDataSharingFile deliverableDataSharingFile = this.find(deliverableDataSharingFileId);
    return dao.delete(deliverableDataSharingFile);
  }

  @Override
  public boolean existDeliverableDataSharingFile(long deliverableDataSharingFileID) {
    DeliverableDataSharingFile deliverableDataSharingFile = this.find(deliverableDataSharingFileID);
    if (deliverableDataSharingFile == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableDataSharingFile find(long id) {
    return dao.find(DeliverableDataSharingFile.class, id);

  }

  @Override
  public List<DeliverableDataSharingFile> findAll() {
    String query = "from " + DeliverableDataSharingFile.class.getName() + " where is_active=1";
    List<DeliverableDataSharingFile> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(DeliverableDataSharingFile deliverableDataSharingFile) {
    if (deliverableDataSharingFile.getId() == null) {
      dao.save(deliverableDataSharingFile);
    } else {
      dao.update(deliverableDataSharingFile);
    }


    return deliverableDataSharingFile.getId();
  }


}