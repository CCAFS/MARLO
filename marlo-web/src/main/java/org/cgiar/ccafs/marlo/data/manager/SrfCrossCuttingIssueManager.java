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

import org.cgiar.ccafs.marlo.data.model.SrfCrossCuttingIssue;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface SrfCrossCuttingIssueManager {


  /**
   * This method removes a specific srfCrossCuttingIssue value from the database.
   * 
   * @param srfCrossCuttingIssueId is the srfCrossCuttingIssue identifier.
   * @return true if the srfCrossCuttingIssue was successfully deleted, false otherwise.
   */
  public void deleteSrfCrossCuttingIssue(long srfCrossCuttingIssueId);


  /**
   * This method validate if the srfCrossCuttingIssue identify with the given id exists in the system.
   * 
   * @param srfCrossCuttingIssueID is a srfCrossCuttingIssue identifier.
   * @return true if the srfCrossCuttingIssue exists, false otherwise.
   */
  public boolean existSrfCrossCuttingIssue(long srfCrossCuttingIssueID);


  /**
   * This method gets a list of srfCrossCuttingIssue that are active
   * 
   * @return a list from SrfCrossCuttingIssue null if no exist records
   */
  public List<SrfCrossCuttingIssue> findAll();


  /**
   * This method gets a srfCrossCuttingIssue object by a given srfCrossCuttingIssue identifier.
   * 
   * @param srfCrossCuttingIssueID is the srfCrossCuttingIssue identifier.
   * @return a SrfCrossCuttingIssue object.
   */
  public SrfCrossCuttingIssue getSrfCrossCuttingIssueById(long srfCrossCuttingIssueID);

  /**
   * This method saves the information of the given srfCrossCuttingIssue
   * 
   * @param srfCrossCuttingIssue - is the srfCrossCuttingIssue object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the srfCrossCuttingIssue was
   *         updated
   *         or -1 is some error occurred.
   */
  public SrfCrossCuttingIssue saveSrfCrossCuttingIssue(SrfCrossCuttingIssue srfCrossCuttingIssue);


}
