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


import org.cgiar.ccafs.marlo.data.dao.CapdevSuppDocsDocumentsDAO;
import org.cgiar.ccafs.marlo.data.manager.CapdevSuppDocsDocumentsManager;
import org.cgiar.ccafs.marlo.data.model.CapdevSuppDocsDocuments;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CapdevSuppDocsDocumentsManagerImpl implements CapdevSuppDocsDocumentsManager {


  private CapdevSuppDocsDocumentsDAO capdevSuppDocsDocumentsDAO;
  // Managers


  @Inject
  public CapdevSuppDocsDocumentsManagerImpl(CapdevSuppDocsDocumentsDAO capdevSuppDocsDocumentsDAO) {
    this.capdevSuppDocsDocumentsDAO = capdevSuppDocsDocumentsDAO;


  }

  @Override
  public void deleteCapdevSuppDocsDocuments(long capdevSuppDocsDocumentsId) {

    capdevSuppDocsDocumentsDAO.deleteCapdevSuppDocsDocuments(capdevSuppDocsDocumentsId);
  }

  @Override
  public boolean existCapdevSuppDocsDocuments(long capdevSuppDocsDocumentsID) {

    return capdevSuppDocsDocumentsDAO.existCapdevSuppDocsDocuments(capdevSuppDocsDocumentsID);
  }

  @Override
  public List<CapdevSuppDocsDocuments> findAll() {

    return capdevSuppDocsDocumentsDAO.findAll();

  }

  @Override
  public CapdevSuppDocsDocuments getCapdevSuppDocsDocumentsById(long capdevSuppDocsDocumentsID) {

    return capdevSuppDocsDocumentsDAO.find(capdevSuppDocsDocumentsID);
  }

  @Override
  public CapdevSuppDocsDocuments saveCapdevSuppDocsDocuments(CapdevSuppDocsDocuments capdevSuppDocsDocuments) {

    return capdevSuppDocsDocumentsDAO.save(capdevSuppDocsDocuments);
  }


}
