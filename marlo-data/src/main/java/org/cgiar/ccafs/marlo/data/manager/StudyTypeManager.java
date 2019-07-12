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

import org.cgiar.ccafs.marlo.data.model.StudyType;

import java.util.List;


/**
 * @author CCAFS
 */

public interface StudyTypeManager {


  /**
   * This method removes a specific studyType value from the database.
   * 
   * @param studyTypeId is the studyType identifier.
   * @return true if the studyType was successfully deleted, false otherwise.
   */
  public void deleteStudyType(long studyTypeId);


  /**
   * This method validate if the studyType identify with the given id exists in the system.
   * 
   * @param studyTypeID is a studyType identifier.
   * @return true if the studyType exists, false otherwise.
   */
  public boolean existStudyType(long studyTypeID);


  /**
   * This method gets a list of studyType that are active
   * 
   * @return a list from StudyType null if no exist records
   */
  public List<StudyType> findAll();


  /**
   * This method gets a studyType object by a given studyType identifier.
   * 
   * @param studyTypeID is the studyType identifier.
   * @return a StudyType object.
   */
  public StudyType getStudyTypeById(long studyTypeID);

  /**
   * This method saves the information of the given studyType
   * 
   * @param studyType - is the studyType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the studyType was
   *         updated
   *         or -1 is some error occurred.
   */
  public StudyType saveStudyType(StudyType studyType);


}
