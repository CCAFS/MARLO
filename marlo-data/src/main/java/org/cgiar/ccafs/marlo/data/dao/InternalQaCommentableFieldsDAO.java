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

import org.cgiar.ccafs.marlo.data.model.InternalQaCommentableFields;

import java.util.List;


public interface InternalQaCommentableFieldsDAO {

  /**
   * This method removes a specific internalQaCommentableFields value from the database.
   * 
   * @param internalQaCommentableFieldsId is the internalQaCommentableFields identifier.
   * @return true if the internalQaCommentableFields was successfully deleted, false otherwise.
   */
  public void deleteInternalQaCommentableFields(long internalQaCommentableFieldsId);

  /**
   * This method validate if the internalQaCommentableFields identify with the given id exists in the system.
   * 
   * @param internalQaCommentableFieldsID is a internalQaCommentableFields identifier.
   * @return true if the internalQaCommentableFields exists, false otherwise.
   */
  public boolean existInternalQaCommentableFields(long internalQaCommentableFieldsID);

  /**
   * This method gets a internalQaCommentableFields object by a given internalQaCommentableFields identifier.
   * 
   * @param internalQaCommentableFieldsID is the internalQaCommentableFields identifier.
   * @return a InternalQaCommentableFields object.
   */
  public InternalQaCommentableFields find(long id);

  /**
   * This method gets a list of internalQaCommentableFields that are active
   * 
   * @return a list from InternalQaCommentableFields null if no exist records
   */
  public List<InternalQaCommentableFields> findAll();


  /**
   * This method saves the information of the given internalQaCommentableFields
   * 
   * @param internalQaCommentableFields - is the internalQaCommentableFields object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the internalQaCommentableFields was
   *         updated
   *         or -1 is some error occurred.
   */
  public InternalQaCommentableFields save(InternalQaCommentableFields internalQaCommentableFields);
}
