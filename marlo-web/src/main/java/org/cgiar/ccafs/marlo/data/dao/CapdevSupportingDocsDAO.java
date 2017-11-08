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

import org.cgiar.ccafs.marlo.data.dao.mysql.CapdevSupportingDocsMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevSupportingDocs;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CapdevSupportingDocsMySQLDAO.class)
public interface CapdevSupportingDocsDAO {

  /**
   * This method removes a specific capdevSupportingDocs value from the database.
   * 
   * @param capdevSupportingDocsId is the capdevSupportingDocs identifier.
   */
  public void deleteCapdevSupportingDocs(long capdevSupportingDocsId);

  /**
   * This method validate if the capdevSupportingDocs identify with the given id exists in the system.
   * 
   * @param capdevSupportingDocsID is a capdevSupportingDocs identifier.
   * @return true if the capdevSupportingDocs exists, false otherwise.
   */
  public boolean existCapdevSupportingDocs(long capdevSupportingDocsID);

  /**
   * This method gets a capdevSupportingDocs object by a given capdevSupportingDocs identifier.
   * 
   * @param capdevSupportingDocsID is the capdevSupportingDocs identifier.
   * @return a CapdevSupportingDocs object.
   */
  public CapdevSupportingDocs find(long id);

  /**
   * This method gets a list of capdevSupportingDocs that are active
   * 
   * @return a list from CapdevSupportingDocs null if no exist records
   */
  public List<CapdevSupportingDocs> findAll();


  /**
   * This method saves the information of the given capdevSupportingDocs
   * 
   * @param capdevSupportingDocs - is the capdevSupportingDocs object with the new information to be added/updated.
   * @return CapdevSupportingDocs object
   */
  public CapdevSupportingDocs save(CapdevSupportingDocs capdevSupportingDocs);


  public CapdevSupportingDocs save(CapdevSupportingDocs capdevSupportingDocs, String actionName,
    List<String> relationsName);
}
