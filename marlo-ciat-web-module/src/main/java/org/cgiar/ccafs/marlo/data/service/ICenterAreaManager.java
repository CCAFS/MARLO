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

/**
 * 
 */
package org.cgiar.ccafs.marlo.data.service;

import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.service.impl.CenterAreaManager;

import java.util.List;

import com.google.inject.ImplementedBy;


/**
 * Modified by @author nmatovu last on Oct 7, 2016
 */
@ImplementedBy(CenterAreaManager.class)
public interface ICenterAreaManager {

  /**
   * This method removes/deletes a research area with the specified id from the database.
   * 
   * @param researchAreaId the research area idor identifier
   * @return true if the research area record was successfully deleted, false otherwise.
   */
  public boolean deleteResearchArea(long researchAreaId);

  /**
   * This finds if the research area with the specified id exists in the database or system.
   * 
   * @param researchAreaId the identifier or id of the research area.
   * @return true if the research area exists, false otherwise.
   */
  public boolean existResearchArea(long researchAreaId);

  /**
   * This method gets a research area record with the given id or identifier.
   * 
   * @param researchId the identifier or id used to located the record.
   * @return the research area record.
   */
  public CenterArea find(long researchId);

  /**
   * This method gets all the research area records that are active in the system
   * 
   * @return a list of research area records.
   */
  public List<CenterArea> findAll();

  /**
   * This method find a research area using the specified acronym
   * 
   * @param acronym - the acronym used to find the record
   * @return a research area record
   */
  public CenterArea findResearchAreaByAcronym(String acronym);


  /**
   * This method saves or updated the specified research area into the database or system
   * 
   * @param researchArea the research area record to save or update.
   * @return a number greater than 0 representing the new ID assigned by the database if a new record was added and 0 if
   *         the research area was updated or -1 is some error occurred.
   */
  public long save(CenterArea researchArea);
}
