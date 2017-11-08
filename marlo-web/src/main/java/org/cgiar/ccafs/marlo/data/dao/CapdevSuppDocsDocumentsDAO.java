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

import org.cgiar.ccafs.marlo.data.dao.mysql.CapdevSuppDocsDocumentsMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevSuppDocsDocuments;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CapdevSuppDocsDocumentsMySQLDAO.class)
public interface CapdevSuppDocsDocumentsDAO {

  /**
   * This method removes a specific capdevSuppDocsDocuments value from the database.
   * 
   * @param capdevSuppDocsDocumentsId is the capdevSuppDocsDocuments identifier.
   */
  public void deleteCapdevSuppDocsDocuments(long capdevSuppDocsDocumentsId);

  /**
   * This method validate if the capdevSuppDocsDocuments identify with the given id exists in the system.
   * 
   * @param capdevSuppDocsDocumentsID is a capdevSuppDocsDocuments identifier.
   * @return true if the capdevSuppDocsDocuments exists, false otherwise.
   */
  public boolean existCapdevSuppDocsDocuments(long capdevSuppDocsDocumentsID);

  /**
   * This method gets a capdevSuppDocsDocuments object by a given capdevSuppDocsDocuments identifier.
   * 
   * @param capdevSuppDocsDocumentsID is the capdevSuppDocsDocuments identifier.
   * @return a CapdevSuppDocsDocuments object.
   */
  public CapdevSuppDocsDocuments find(long id);

  /**
   * This method gets a list of capdevSuppDocsDocuments that are active
   * 
   * @return a list from CapdevSuppDocsDocuments null if no exist records
   */
  public List<CapdevSuppDocsDocuments> findAll();


  /**
   * This method saves the information of the given capdevSuppDocsDocuments
   * 
   * @param capdevSuppDocsDocuments - is the capdevSuppDocsDocuments object with the new information to be
   *        added/updated.
   * @return CapdevSuppDocsDocuments object.
   */
  public CapdevSuppDocsDocuments save(CapdevSuppDocsDocuments capdevSuppDocsDocuments);
}
