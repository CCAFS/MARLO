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

import org.cgiar.ccafs.marlo.data.model.PowbIndAssesmentRisk;

import java.util.List;


public interface PowbIndAssesmentRiskDAO {

  /**
   * This method removes a specific powbIndAssesmentRisk value from the database.
   * 
   * @param powbIndAssesmentRiskId is the powbIndAssesmentRisk identifier.
   * @return true if the powbIndAssesmentRisk was successfully deleted, false otherwise.
   */
  public void deletePowbIndAssesmentRisk(long powbIndAssesmentRiskId);

  /**
   * This method validate if the powbIndAssesmentRisk identify with the given id exists in the system.
   * 
   * @param powbIndAssesmentRiskID is a powbIndAssesmentRisk identifier.
   * @return true if the powbIndAssesmentRisk exists, false otherwise.
   */
  public boolean existPowbIndAssesmentRisk(long powbIndAssesmentRiskID);

  /**
   * This method gets a powbIndAssesmentRisk object by a given powbIndAssesmentRisk identifier.
   * 
   * @param powbIndAssesmentRiskID is the powbIndAssesmentRisk identifier.
   * @return a PowbIndAssesmentRisk object.
   */
  public PowbIndAssesmentRisk find(long id);

  /**
   * This method gets a list of powbIndAssesmentRisk that are active
   * 
   * @return a list from PowbIndAssesmentRisk null if no exist records
   */
  public List<PowbIndAssesmentRisk> findAll();


  /**
   * This method saves the information of the given powbIndAssesmentRisk
   * 
   * @param powbIndAssesmentRisk - is the powbIndAssesmentRisk object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbIndAssesmentRisk was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbIndAssesmentRisk save(PowbIndAssesmentRisk powbIndAssesmentRisk);
}
