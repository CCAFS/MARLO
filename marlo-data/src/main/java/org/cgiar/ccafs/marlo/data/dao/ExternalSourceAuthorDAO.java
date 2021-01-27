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

import org.cgiar.ccafs.marlo.data.model.ExternalSourceAuthor;

import java.util.List;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public interface ExternalSourceAuthorDAO {

  /**
   * This method removes a specific externalSourceAuthor value from the database.
   * 
   * @param externalSourceAuthorId is the externalSourceAuthor identifier.
   * @return true if the externalSourceAuthor was successfully deleted, false otherwise.
   */
  public void deleteExternalSourceAuthor(long externalSourceAuthorId);

  /**
   * This method validate if the externalSourceAuthor identify with the given id exists in the system.
   * 
   * @param externalSourceAuthorID is a externalSourceAuthor identifier.
   * @return true if the externalSourceAuthor exists, false otherwise.
   */
  public boolean existExternalSourceAuthor(long externalSourceAuthorID);

  /**
   * This method gets a externalSourceAuthor object by a given externalSourceAuthor
   * identifier.
   * 
   * @param externalSourceAuthorID is the externalSourceAuthor identifier.
   * @return a ExternalSourceAuthor object.
   */
  public ExternalSourceAuthor find(long id);

  /**
   * This method gets a list of externalSourceAuthor that are active
   * 
   * @return a list from ExternalSourceAuthor null if no exist records
   */
  public List<ExternalSourceAuthor> findAll();

  /**
   * This method saves the information of the given externalSourceAuthor
   * 
   * @param externalSourceAuthor - is the externalSourceAuthor object with the new information
   *        to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         externalSourceAuthor was
   *         updated
   *         or -1 is some error occurred.
   */
  public ExternalSourceAuthor save(ExternalSourceAuthor externalSourceAuthor);
}
