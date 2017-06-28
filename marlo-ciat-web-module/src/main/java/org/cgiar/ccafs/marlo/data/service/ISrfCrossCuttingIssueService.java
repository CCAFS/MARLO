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
package org.cgiar.ccafs.marlo.data.service;

import org.cgiar.ccafs.marlo.data.model.SrfCrossCuttingIssue;
import org.cgiar.ccafs.marlo.data.service.impl.SrfCrossCuttingIssueService;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(SrfCrossCuttingIssueService.class)
public interface ISrfCrossCuttingIssueService {


  /**
   * This method removes a specific srfCrossCuttingIssue value from the database.
   * 
   * @param srfCrossCuttingIssueId is the srfCrossCuttingIssue identifier.
   * @return true if the srfCrossCuttingIssue was successfully deleted, false otherwise.
   */
  public boolean deleteSrfCrossCuttingIssue(long srfCrossCuttingIssueId);


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
   * This method gets a list of srfCrossCuttingIssues belongs of the user
   * 
   * @param userId - the user id
   * @return List of SrfCrossCuttingIssues or null if the user is invalid or not have roles.
   */
  public List<SrfCrossCuttingIssue> getSrfCrossCuttingIssuesByUserId(Long userId);

  /**
   * This method saves the information of the given srfCrossCuttingIssue
   * 
   * @param srfCrossCuttingIssue - is the srfCrossCuttingIssue object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the srfCrossCuttingIssue was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveSrfCrossCuttingIssue(SrfCrossCuttingIssue srfCrossCuttingIssue);

  /**
   * This method saves the information of the given srfCrossCuttingIssue
   * 
   * @param srfCrossCuttingIssue - is the srfCrossCuttingIssue object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the srfCrossCuttingIssue was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveSrfCrossCuttingIssue(SrfCrossCuttingIssue srfCrossCuttingIssue, String actionName, List<String> relationsName);


}
