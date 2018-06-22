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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiarCollaboration;

import java.util.List;


public interface ReportSynthesisCrossCgiarCollaborationDAO {

  /**
   * This method removes a specific reportSynthesisCrossCgiarCollaboration value from the database.
   * 
   * @param reportSynthesisCrossCgiarCollaborationId is the reportSynthesisCrossCgiarCollaboration identifier.
   * @return true if the reportSynthesisCrossCgiarCollaboration was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisCrossCgiarCollaboration(long reportSynthesisCrossCgiarCollaborationId);

  /**
   * This method validate if the reportSynthesisCrossCgiarCollaboration identify with the given id exists in the system.
   * 
   * @param reportSynthesisCrossCgiarCollaborationID is a reportSynthesisCrossCgiarCollaboration identifier.
   * @return true if the reportSynthesisCrossCgiarCollaboration exists, false otherwise.
   */
  public boolean existReportSynthesisCrossCgiarCollaboration(long reportSynthesisCrossCgiarCollaborationID);

  /**
   * This method gets a reportSynthesisCrossCgiarCollaboration object by a given reportSynthesisCrossCgiarCollaboration identifier.
   * 
   * @param reportSynthesisCrossCgiarCollaborationID is the reportSynthesisCrossCgiarCollaboration identifier.
   * @return a ReportSynthesisCrossCgiarCollaboration object.
   */
  public ReportSynthesisCrossCgiarCollaboration find(long id);

  /**
   * This method gets a list of reportSynthesisCrossCgiarCollaboration that are active
   * 
   * @return a list from ReportSynthesisCrossCgiarCollaboration null if no exist records
   */
  public List<ReportSynthesisCrossCgiarCollaboration> findAll();


  /**
   * This method saves the information of the given reportSynthesisCrossCgiarCollaboration
   * 
   * @param reportSynthesisCrossCgiarCollaboration - is the reportSynthesisCrossCgiarCollaboration object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisCrossCgiarCollaboration was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisCrossCgiarCollaboration save(ReportSynthesisCrossCgiarCollaboration reportSynthesisCrossCgiarCollaboration);
}
