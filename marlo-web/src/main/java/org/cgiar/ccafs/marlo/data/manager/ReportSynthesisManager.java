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

import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisManager {


  /**
   * This method removes a specific reportSynthesis value from the database.
   * 
   * @param reportSynthesisId is the reportSynthesis identifier.
   * @return true if the reportSynthesis was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesis(long reportSynthesisId);


  /**
   * This method validate if the reportSynthesis identify with the given id exists in the system.
   * 
   * @param reportSynthesisID is a reportSynthesis identifier.
   * @return true if the reportSynthesis exists, false otherwise.
   */
  public boolean existReportSynthesis(long reportSynthesisID);


  /**
   * This method gets a list of reportSynthesis that are active
   * 
   * @return a list from ReportSynthesis null if no exist records
   */
  public List<ReportSynthesis> findAll();


  /**
   * This method gets a reportSynthesis by phase and liaison Institution
   * 
   * @return a ReportSynthesis object or null if no exist records
   */
  public ReportSynthesis findSynthesis(long phaseID, long liaisonInstitutionID);

  /**
   * This method gets a reportSynthesis object by a given reportSynthesis identifier.
   * 
   * @param reportSynthesisID is the reportSynthesis identifier.
   * @return a ReportSynthesis object.
   */
  public ReportSynthesis getReportSynthesisById(long reportSynthesisID);

  public ReportSynthesis save(ReportSynthesis reportSynthesis, String sectionName, List<String> relationsName,
    Phase phase);

  /**
   * This method saves the information of the given reportSynthesis
   * 
   * @param reportSynthesis - is the reportSynthesis object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesis was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesis saveReportSynthesis(ReportSynthesis reportSynthesis);


}
