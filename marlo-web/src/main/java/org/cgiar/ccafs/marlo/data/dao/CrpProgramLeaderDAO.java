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

import org.cgiar.ccafs.marlo.data.dao.mysql.CrpProgramLeaderMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CrpProgramLeaderMySQLDAO.class)
public interface CrpProgramLeaderDAO {

  /**
   * This method removes a specific crpProgramLeader value from the database.
   * 
   * @param crpProgramLeaderId is the crpProgramLeader identifier.
   * @return true if the crpProgramLeader was successfully deleted, false otherwise.
   */
  public boolean deleteCrpProgramLeader(long crpProgramLeaderId);

  /**
   * This method validate if the crpProgramLeader identify with the given id exists in the system.
   * 
   * @param crpProgramLeaderID is a crpProgramLeader identifier.
   * @return true if the crpProgramLeader exists, false otherwise.
   */
  public boolean existCrpProgramLeader(long crpProgramLeaderID);

  /**
   * This method gets a crpProgramLeader object by a given crpProgramLeader identifier.
   * 
   * @param crpProgramLeaderID is the crpProgramLeader identifier.
   * @return a CrpProgramLeader object.
   */
  public CrpProgramLeader find(long id);

  /**
   * This method gets a list of crpProgramLeader that are active
   * 
   * @return a list from CrpProgramLeader null if no exist records
   */
  public List<CrpProgramLeader> findAll();


  /**
   * This method saves the information of the given crpProgramLeader
   * 
   * @param crpProgramLeader - is the crpProgramLeader object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpProgramLeader was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CrpProgramLeader crpProgramLeader);
}
