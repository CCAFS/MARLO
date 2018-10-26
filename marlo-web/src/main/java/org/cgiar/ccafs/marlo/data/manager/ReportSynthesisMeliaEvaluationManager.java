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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluation;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisMeliaEvaluationManager {


  /**
   * This method removes a specific reportSynthesisMeliaEvaluation value from the database.
   * 
   * @param reportSynthesisMeliaEvaluationId is the reportSynthesisMeliaEvaluation identifier.
   * @return true if the reportSynthesisMeliaEvaluation was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisMeliaEvaluation(long reportSynthesisMeliaEvaluationId);


  /**
   * This method validate if the reportSynthesisMeliaEvaluation identify with the given id exists in the system.
   * 
   * @param reportSynthesisMeliaEvaluationID is a reportSynthesisMeliaEvaluation identifier.
   * @return true if the reportSynthesisMeliaEvaluation exists, false otherwise.
   */
  public boolean existReportSynthesisMeliaEvaluation(long reportSynthesisMeliaEvaluationID);


  /**
   * This method gets a list of reportSynthesisMeliaEvaluation that are active
   * 
   * @return a list from ReportSynthesisMeliaEvaluation null if no exist records
   */
  public List<ReportSynthesisMeliaEvaluation> findAll();


  /**
   * This method gets a reportSynthesisMeliaEvaluation object by a given reportSynthesisMeliaEvaluation identifier.
   * 
   * @param reportSynthesisMeliaEvaluationID is the reportSynthesisMeliaEvaluation identifier.
   * @return a ReportSynthesisMeliaEvaluation object.
   */
  public ReportSynthesisMeliaEvaluation getReportSynthesisMeliaEvaluationById(long reportSynthesisMeliaEvaluationID);

  /**
   * This method saves the information of the given reportSynthesisMeliaEvaluation
   * 
   * @param reportSynthesisMeliaEvaluation - is the reportSynthesisMeliaEvaluation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisMeliaEvaluation was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisMeliaEvaluation saveReportSynthesisMeliaEvaluation(ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation);


}
