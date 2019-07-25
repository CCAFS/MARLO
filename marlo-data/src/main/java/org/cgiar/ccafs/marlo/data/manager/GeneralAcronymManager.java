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

import org.cgiar.ccafs.marlo.data.model.GeneralAcronym;

import java.util.List;


/**
 * @author CCAFS
 */

public interface GeneralAcronymManager {


  /**
   * This method removes a specific generalAcronym value from the database.
   * 
   * @param generalAcronymId is the generalAcronym identifier.
   * @return true if the generalAcronym was successfully deleted, false otherwise.
   */
  public void deleteGeneralAcronym(long generalAcronymId);


  /**
   * This method validate if the generalAcronym identify with the given id exists in the system.
   * 
   * @param generalAcronymID is a generalAcronym identifier.
   * @return true if the generalAcronym exists, false otherwise.
   */
  public boolean existGeneralAcronym(long generalAcronymID);


  /**
   * This method gets a list of generalAcronym that are active
   * 
   * @return a list from GeneralAcronym null if no exist records
   */
  public List<GeneralAcronym> findAll();

  /**
   * This method gets a list of generalAcronym that has the acronym
   * 
   * @return a list from GeneralAcronym null if no exist records
   */
  public List<GeneralAcronym> getGeneralAcronymByAcronym(String acronym);


  /**
   * This method gets a generalAcronym object by a given generalAcronym identifier.
   * 
   * @param generalAcronymID is the generalAcronym identifier.
   * @return a GeneralAcronym object.
   */
  public GeneralAcronym getGeneralAcronymById(long generalAcronymID);

  /**
   * This method saves the information of the given generalAcronym
   * 
   * @param generalAcronym - is the generalAcronym object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the generalAcronym was
   *         updated
   *         or -1 is some error occurred.
   */
  public GeneralAcronym saveGeneralAcronym(GeneralAcronym generalAcronym);


}
