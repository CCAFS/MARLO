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

import org.cgiar.ccafs.marlo.data.model.CrossCuttingScoring;

import java.util.List;

public interface CrossCuttingScoringDAO {

  /**
   * This method validate if the CrossCuttingScoring identify with the given id exists in the system.
   * 
   * @param crossCuttingId is a CrossCuttingScoring identifier.
   * @return true if the CrossCuttingScoring exists, false otherwise.
   */
  public boolean existCrossCuttingScoring(long crossCuttingId);

  /**
   * This method gets a CrossCuttingScoring object by a given activity identifier.
   * 
   * @param crossCuttingId is the CrossCuttingScoring identifier.
   * @return a CrossCuttingScoring object.
   */
  public CrossCuttingScoring find(long crossCuttingId);

  /**
   * This method gets a list of CrossCuttingScoring that are active
   * 
   * @return a list from CrossCuttingScoring null if no exist records
   */
  public List<CrossCuttingScoring> findAll();


  /**
   * This method saves the information of the given CrossCuttingScoring
   * 
   * @param crossCutting - is the CrossCuttingScoring object with the new information to be added/updated.
   * @return the object that was saved before
   */
  public CrossCuttingScoring save(CrossCuttingScoring crossCutting);
}
