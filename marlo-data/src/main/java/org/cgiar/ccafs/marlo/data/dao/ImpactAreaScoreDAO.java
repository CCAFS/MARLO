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

import org.cgiar.ccafs.marlo.data.model.ImpactAreaScore;

import java.util.List;


public interface ImpactAreaScoreDAO {

  /**
   * This method removes a specific impactAreaScore value from the database.
   * 
   * @param impactAreaScoreId is the impactAreaScore identifier.
   * @return true if the impactAreaScore was successfully deleted, false otherwise.
   */
  public void deleteImpactAreaScore(long impactAreaScoreId);

  /**
   * This method validate if the impactAreaScore identify with the given id exists in the system.
   * 
   * @param impactAreaScoreID is a impactAreaScore identifier.
   * @return true if the impactAreaScore exists, false otherwise.
   */
  public boolean existImpactAreaScore(long impactAreaScoreID);

  /**
   * This method gets a impactAreaScore object by a given impactAreaScore identifier.
   * 
   * @param impactAreaScoreID is the impactAreaScore identifier.
   * @return a ImpactAreaScore object.
   */
  public ImpactAreaScore find(long id);

  /**
   * This method gets a list of impactAreaScore that are active
   * 
   * @return a list from ImpactAreaScore null if no exist records
   */
  public List<ImpactAreaScore> findAll();


  /**
   * This method saves the information of the given impactAreaScore
   * 
   * @param impactAreaScore - is the impactAreaScore object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the impactAreaScore was
   *         updated
   *         or -1 is some error occurred.
   */
  public ImpactAreaScore save(ImpactAreaScore impactAreaScore);
}
