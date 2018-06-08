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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisProgramVariance;

import java.util.List;


public interface ReportSynthesisProgramVarianceDAO {

  /**
   * This method removes a specific reportSynthesisProgramVariance value from the database.
   * 
   * @param reportSynthesisProgramVarianceId is the reportSynthesisProgramVariance identifier.
   * @return true if the reportSynthesisProgramVariance was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisProgramVariance(long reportSynthesisProgramVarianceId);

  /**
   * This method validate if the reportSynthesisProgramVariance identify with the given id exists in the system.
   * 
   * @param reportSynthesisProgramVarianceID is a reportSynthesisProgramVariance identifier.
   * @return true if the reportSynthesisProgramVariance exists, false otherwise.
   */
  public boolean existReportSynthesisProgramVariance(long reportSynthesisProgramVarianceID);

  /**
   * This method gets a reportSynthesisProgramVariance object by a given reportSynthesisProgramVariance identifier.
   * 
   * @param reportSynthesisProgramVarianceID is the reportSynthesisProgramVariance identifier.
   * @return a ReportSynthesisProgramVariance object.
   */
  public ReportSynthesisProgramVariance find(long id);

  /**
   * This method gets a list of reportSynthesisProgramVariance that are active
   * 
   * @return a list from ReportSynthesisProgramVariance null if no exist records
   */
  public List<ReportSynthesisProgramVariance> findAll();


  /**
   * This method saves the information of the given reportSynthesisProgramVariance
   * 
   * @param reportSynthesisProgramVariance - is the reportSynthesisProgramVariance object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisProgramVariance was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisProgramVariance save(ReportSynthesisProgramVariance reportSynthesisProgramVariance);
}
