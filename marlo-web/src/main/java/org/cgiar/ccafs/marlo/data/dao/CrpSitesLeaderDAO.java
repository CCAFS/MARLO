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

import org.cgiar.ccafs.marlo.data.dao.mysql.CrpSitesLeaderMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CrpSitesLeader;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CrpSitesLeaderMySQLDAO.class)
public interface CrpSitesLeaderDAO {

  /**
   * This method removes a specific crpSitesLeader value from the database.
   * 
   * @param crpSitesLeaderId is the crpSitesLeader identifier.
   * @return true if the crpSitesLeader was successfully deleted, false otherwise.
   */
  public boolean deleteCrpSitesLeader(long crpSitesLeaderId);

  /**
   * This method validate if the crpSitesLeader identify with the given id exists in the system.
   * 
   * @param crpSitesLeaderID is a crpSitesLeader identifier.
   * @return true if the crpSitesLeader exists, false otherwise.
   */
  public boolean existCrpSitesLeader(long crpSitesLeaderID);

  /**
   * This method gets a crpSitesLeader object by a given crpSitesLeader identifier.
   * 
   * @param crpSitesLeaderID is the crpSitesLeader identifier.
   * @return a CrpSitesLeader object.
   */
  public CrpSitesLeader find(long id);

  /**
   * This method gets a list of crpSitesLeader that are active
   * 
   * @return a list from CrpSitesLeader null if no exist records
   */
  public List<CrpSitesLeader> findAll();


  /**
   * This method saves the information of the given crpSitesLeader
   * 
   * @param crpSitesLeader - is the crpSitesLeader object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpSitesLeader was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CrpSitesLeader crpSitesLeader);
}
