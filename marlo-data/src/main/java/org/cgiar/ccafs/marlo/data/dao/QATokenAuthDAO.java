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

import org.cgiar.ccafs.marlo.data.model.QATokenAuth;

import java.util.List;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
public interface QATokenAuthDAO {

  /**
   * This method removes a specific qATokenAuth value from the database.
   * 
   * @param qATokenAuthId is the qATokenAuth identifier.
   * @return true if the qATokenAuth was successfully deleted, false otherwise.
   */
  public void deleteQATokenAuth(long qATokenAuthId);

  /**
   * This method validate if the qATokenAuth identify with the given id exists in the system.
   * 
   * @param qATokenAuthID is a qATokenAuth identifier.
   * @return true if the qATokenAuth exists, false otherwise.
   */
  public boolean existQATokenAuth(long qATokenAuthID);

  /**
   * This method gets a qATokenAuth object by a given qATokenAuth identifier.
   * 
   * @param qATokenAuthID is the qATokenAuth identifier.
   * @return a QATokenAuth object.
   */
  public QATokenAuth find(long id);

  /**
   * This method gets a list of qATokenAuth that are active
   * 
   * @return a list from QATokenAuth null if no exist records
   */
  public List<QATokenAuth> findAll();


  /**
   * This method saves the information of the given qATokenAuth
   * 
   * @param qATokenAuth - is the qATokenAuth object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the qATokenAuth was
   *         updated
   *         or -1 is some error occurred.
   */
  public QATokenAuth save(QATokenAuth qATokenAuth);
}
