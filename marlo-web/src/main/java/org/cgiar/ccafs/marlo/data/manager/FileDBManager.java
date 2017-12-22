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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.FileDB;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface FileDBManager {


  /**
   * This method removes a specific fileDB value from the database.
   * 
   * @param fileDBId is the fileDB identifier.
   * @return true if the fileDB was successfully deleted, false otherwise.
   */
  public void deleteFileDB(long fileDBId);


  /**
   * This method validate if the fileDB identify with the given id exists in the system.
   * 
   * @param fileDBID is a fileDB identifier.
   * @return true if the fileDB exists, false otherwise.
   */
  public boolean existFileDB(long fileDBID);


  /**
   * This method gets a list of fileDB that are active
   * 
   * @return a list from FileDB null if no exist records
   */
  public List<FileDB> findAll();


  /**
   * This method gets a fileDB object by a given fileDB identifier.
   * 
   * @param fileDBID is the fileDB identifier.
   * @return a FileDB object.
   */
  public FileDB getFileDBById(long fileDBID);

  /**
   * This method saves the information of the given fileDB
   * 
   * @param fileDB - is the fileDB object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the fileDB was
   *         updated
   *         or -1 is some error occurred.
   */
  public FileDB saveFileDB(FileDB fileDB);


}
