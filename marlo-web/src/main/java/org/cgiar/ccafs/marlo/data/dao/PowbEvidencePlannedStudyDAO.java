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

import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudy;

import java.util.List;


public interface PowbEvidencePlannedStudyDAO {

  /**
   * This method removes a specific powbEvidencePlannedStudy value from the database.
   * 
   * @param powbEvidencePlannedStudyId is the powbEvidencePlannedStudy identifier.
   * @return true if the powbEvidencePlannedStudy was successfully deleted, false otherwise.
   */
  public void deletePowbEvidencePlannedStudy(long powbEvidencePlannedStudyId);

  /**
   * This method validate if the powbEvidencePlannedStudy identify with the given id exists in the system.
   * 
   * @param powbEvidencePlannedStudyID is a powbEvidencePlannedStudy identifier.
   * @return true if the powbEvidencePlannedStudy exists, false otherwise.
   */
  public boolean existPowbEvidencePlannedStudy(long powbEvidencePlannedStudyID);

  /**
   * This method gets a powbEvidencePlannedStudy object by a given powbEvidencePlannedStudy identifier.
   * 
   * @param powbEvidencePlannedStudyID is the powbEvidencePlannedStudy identifier.
   * @return a PowbEvidencePlannedStudy object.
   */
  public PowbEvidencePlannedStudy find(long id);

  /**
   * This method gets a list of powbEvidencePlannedStudy that are active
   * 
   * @return a list from PowbEvidencePlannedStudy null if no exist records
   */
  public List<PowbEvidencePlannedStudy> findAll();


  /**
   * This method saves the information of the given powbEvidencePlannedStudy
   * 
   * @param powbEvidencePlannedStudy - is the powbEvidencePlannedStudy object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbEvidencePlannedStudy was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbEvidencePlannedStudy save(PowbEvidencePlannedStudy powbEvidencePlannedStudy);
}
