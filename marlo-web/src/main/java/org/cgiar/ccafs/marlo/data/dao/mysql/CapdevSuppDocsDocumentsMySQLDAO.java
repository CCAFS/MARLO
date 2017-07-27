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

import org.cgiar.ccafs.marlo.data.dao.CapdevSuppDocsDocumentsDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevSuppDocsDocuments;

import java.util.List;

import com.google.inject.Inject;

public class CapdevSuppDocsDocumentsMySQLDAO implements CapdevSuppDocsDocumentsDAO {

  private StandardDAO dao;

  @Inject
  public CapdevSuppDocsDocumentsMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCapdevSuppDocsDocuments(long capdevSuppDocsDocumentsId) {
    CapdevSuppDocsDocuments capdevSuppDocsDocuments = this.find(capdevSuppDocsDocumentsId);
    capdevSuppDocsDocuments.setActive(false);
    return this.save(capdevSuppDocsDocuments) > 0;
  }

  @Override
  public boolean existCapdevSuppDocsDocuments(long capdevSuppDocsDocumentsID) {
    CapdevSuppDocsDocuments capdevSuppDocsDocuments = this.find(capdevSuppDocsDocumentsID);
    if (capdevSuppDocsDocuments == null) {
      return false;
    }
    return true;

  }

  @Override
  public CapdevSuppDocsDocuments find(long id) {
    return dao.find(CapdevSuppDocsDocuments.class, id);

  }

  @Override
  public List<CapdevSuppDocsDocuments> findAll() {
    String query = "from " + CapdevSuppDocsDocuments.class.getName() + " where is_active=1";
    List<CapdevSuppDocsDocuments> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CapdevSuppDocsDocuments capdevSuppDocsDocuments) {
    if (capdevSuppDocsDocuments.getId() == null) {
      dao.save(capdevSuppDocsDocuments);
    } else {
      dao.update(capdevSuppDocsDocuments);
    }


    return capdevSuppDocsDocuments.getId();
  }


}