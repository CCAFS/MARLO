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

import org.cgiar.ccafs.marlo.data.model.BiReports;

import java.util.List;


/**
 * @author Luis Benavides - CIAT/CCAFS
 */
public interface BiReportsManager {


  /**
   * This method removes a specific biReports value from the database.
   * 
   * @param biReportsId is the biReports identifier.
   * @return true if the biReports was successfully deleted, false otherwise.
   */
  public void deleteBiReports(long biReportsId);


  /**
   * This method validate if the biReports identify with the given id exists in the system.
   * 
   * @param biReportsID is a biReports identifier.
   * @return true if the biReports exists, false otherwise.
   */
  public boolean existBiReports(long biReportsID);


  /**
   * This method gets a list of biReports that are active
   * 
   * @return a list from BiReports null if no exist records
   */
  public List<BiReports> findAll();


  /**
   * This method gets a biReports object by a given biReports identifier.
   * 
   * @param biReportsID is the biReports identifier.
   * @return a BiReports object.
   */
  public BiReports getBiReportsById(long biReportsID);

  /**
   * This method saves the information of the given biReports
   * 
   * @param biReports - is the biReports object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the biReports was
   *         updated
   *         or -1 is some error occurred.
   */
  public BiReports saveBiReports(BiReports biReports);


}
