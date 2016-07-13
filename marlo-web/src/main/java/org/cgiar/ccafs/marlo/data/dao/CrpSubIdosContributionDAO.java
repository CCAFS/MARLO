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

import org.cgiar.ccafs.marlo.data.dao.mysql.CrpSubIdosContributionMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CrpSubIdosContribution;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CrpSubIdosContributionMySQLDAO.class)
public interface CrpSubIdosContributionDAO {

  /**
   * This method removes a specific crpSubIdosContribution value from the database.
   * 
   * @param crpSubIdosContributionId is the crpSubIdosContribution identifier.
   * @return true if the crpSubIdosContribution was successfully deleted, false otherwise.
   */
  public boolean deleteCrpSubIdosContribution(long crpSubIdosContributionId);

  /**
   * This method validate if the crpSubIdosContribution identify with the given id exists in the system.
   * 
   * @param crpSubIdosContributionID is a crpSubIdosContribution identifier.
   * @return true if the crpSubIdosContribution exists, false otherwise.
   */
  public boolean existCrpSubIdosContribution(long crpSubIdosContributionID);

  /**
   * This method gets a crpSubIdosContribution object by a given crpSubIdosContribution identifier.
   * 
   * @param crpSubIdosContributionID is the crpSubIdosContribution identifier.
   * @return a CrpSubIdosContribution object.
   */
  public CrpSubIdosContribution find(long id);

  /**
   * This method gets a list of crpSubIdosContribution that are active
   * 
   * @return a list from CrpSubIdosContribution null if no exist records
   */
  public List<CrpSubIdosContribution> findAll();


  /**
   * This method saves the information of the given crpSubIdosContribution
   * 
   * @param crpSubIdosContribution - is the crpSubIdosContribution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database,
   *         0 if the crpSubIdosContribution was updated
   *         or -1 is some error occurred.
   */
  public long save(CrpSubIdosContribution crpSubIdosContribution);
}
