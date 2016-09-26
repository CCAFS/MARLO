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

import org.cgiar.ccafs.marlo.data.dao.mysql.CrpClusterKeyOutputOutcomeMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CrpClusterKeyOutputOutcomeMySQLDAO.class)
public interface CrpClusterKeyOutputOutcomeDAO {

  /**
   * This method removes a specific crpClusterKeyOutputOutcome value from the database.
   * 
   * @param crpClusterKeyOutputOutcomeId is the crpClusterKeyOutputOutcome identifier.
   * @return true if the crpClusterKeyOutputOutcome was successfully deleted, false otherwise.
   */
  public boolean deleteCrpClusterKeyOutputOutcome(long crpClusterKeyOutputOutcomeId);

  /**
   * This method validate if the crpClusterKeyOutputOutcome identify with the given id exists in the system.
   * 
   * @param crpClusterKeyOutputOutcomeID is a crpClusterKeyOutputOutcome identifier.
   * @return true if the crpClusterKeyOutputOutcome exists, false otherwise.
   */
  public boolean existCrpClusterKeyOutputOutcome(long crpClusterKeyOutputOutcomeID);

  /**
   * This method gets a crpClusterKeyOutputOutcome object by a given crpClusterKeyOutputOutcome identifier.
   * 
   * @param crpClusterKeyOutputOutcomeID is the crpClusterKeyOutputOutcome identifier.
   * @return a CrpClusterKeyOutputOutcome object.
   */
  public CrpClusterKeyOutputOutcome find(long id);

  /**
   * This method gets a list of crpClusterKeyOutputOutcome that are active
   * 
   * @return a list from CrpClusterKeyOutputOutcome null if no exist records
   */
  public List<CrpClusterKeyOutputOutcome> findAll();


  /**
   * This method saves the information of the given crpClusterKeyOutputOutcome
   * 
   * @param crpClusterKeyOutputOutcome - is the crpClusterKeyOutputOutcome object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpClusterKeyOutputOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome);
}
