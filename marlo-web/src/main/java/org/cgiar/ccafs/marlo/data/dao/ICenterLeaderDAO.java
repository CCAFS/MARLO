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
package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.dao.mysql.CenterLeaderDAO;
import org.cgiar.ccafs.marlo.data.model.CenterLeader;

import java.util.List;

import com.google.inject.ImplementedBy;


/**
 * Modified by @author nmatovu last on Oct 10, 2016
 */
@ImplementedBy(CenterLeaderDAO.class)
public interface ICenterLeaderDAO {

  /**
   * This method removes a specific CenterLeader value from the database.
   * 
   * @param researchLeaderId is the CenterLeader identifier.
   * @return true if the CenterLeader was successfully deleted, false otherwise.
   */
  public void deleteResearchLeader(long researchLeaderId);


  /**
   * This method validate if the CenterLeader identify with the given id exists in the system.
   * 
   * @param researchLeaderId is a CenterLeader identifier.
   * @return true if the crpProgramLeader exists, false otherwise.
   */
  public boolean existResearchLeader(long researchLeaderId);

  /**
   * This method gets a research leader record with the given id or identifier.
   * 
   * @param researchLeaderId the identifier or id used to located the record.
   * @return the research leader record.
   */
  public CenterLeader find(long researchLeaderId);

  /**
   * This method gets all the research leaders records that are active in the system
   * 
   * @return a list of research leader records.
   */
  public List<CenterLeader> findAll();

  /**
   * This method saves the information of the given research leader
   * 
   * @param researchLeader - is the research leader object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the research leader was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterLeader save(CenterLeader researchLeader);

}
