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

import org.cgiar.ccafs.marlo.data.dao.CapdevSupportingDocsDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevSupportingDocs;

import java.util.List;

import com.google.inject.Inject;

public class CapdevSupportingDocsMySQLDAO implements CapdevSupportingDocsDAO {

  private StandardDAO dao;

  @Inject
  public CapdevSupportingDocsMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCapdevSupportingDocs(long capdevSupportingDocsId) {
    CapdevSupportingDocs capdevSupportingDocs = this.find(capdevSupportingDocsId);
    capdevSupportingDocs.setActive(false);
    return this.save(capdevSupportingDocs) > 0;
  }

  @Override
  public boolean existCapdevSupportingDocs(long capdevSupportingDocsID) {
    CapdevSupportingDocs capdevSupportingDocs = this.find(capdevSupportingDocsID);
    if (capdevSupportingDocs == null) {
      return false;
    }
    return true;

  }

  @Override
  public CapdevSupportingDocs find(long id) {
    return dao.find(CapdevSupportingDocs.class, id);

  }

  @Override
  public List<CapdevSupportingDocs> findAll() {
    String query = "from " + CapdevSupportingDocs.class.getName() + " where is_active=1";
    List<CapdevSupportingDocs> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CapdevSupportingDocs capdevSupportingDocs) {
    if (capdevSupportingDocs.getId() == null) {
      dao.save(capdevSupportingDocs);
    } else {
      dao.update(capdevSupportingDocs);
    }


    return capdevSupportingDocs.getId();
  }


}