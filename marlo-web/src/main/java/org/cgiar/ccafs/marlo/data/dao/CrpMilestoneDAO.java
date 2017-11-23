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

import org.cgiar.ccafs.marlo.data.dao.mysql.CrpMilestoneMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CrpMilestoneMySQLDAO.class)
public interface CrpMilestoneDAO {

  /**
   * This method removes a specific crpMilestone value from the database.
   * 
   * @param crpMilestoneId is the crpMilestone identifier.
   * @return true if the crpMilestone was successfully deleted, false otherwise.
   */
  public void deleteCrpMilestone(long crpMilestoneId);

  /**
   * This method validate if the crpMilestone identify with the given id exists in the system.
   * 
   * @param crpMilestoneID is a crpMilestone identifier.
   * @return true if the crpMilestone exists, false otherwise.
   */
  public boolean existCrpMilestone(long crpMilestoneID);

  /**
   * This method gets a crpMilestone object by a given crpMilestone identifier.
   * 
   * @param crpMilestoneID is the crpMilestone identifier.
   * @return a CrpMilestone object.
   */
  public CrpMilestone find(long id);

  /**
   * This method gets a list of crpMilestone that are active
   * 
   * @return a list from CrpMilestone null if no exist records
   */
  public List<CrpMilestone> findAll();

  public CrpMilestone getCrpMilestone(String composedId, CrpProgramOutcome crpProgramOutcome);


  /**
   * This method saves the information of the given crpMilestone
   * 
   * @param crpMilestone - is the crpMilestone object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpMilestone was
   *         updated
   *         or -1 is some error occurred.
   */
  public CrpMilestone save(CrpMilestone crpMilestone);
}
