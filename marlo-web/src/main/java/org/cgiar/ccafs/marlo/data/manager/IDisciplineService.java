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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.Discipline;

import java.util.List;

/**
 * @author Christian Garcia
 */
public interface IDisciplineService {


  /**
   * This method removes a specific discipline value from the database.
   * 
   * @param disciplineId is the discipline identifier.
   */
  public void deleteDiscipline(long disciplineId);


  /**
   * This method validate if the discipline identify with the given id exists in the system.
   * 
   * @param disciplineID is a discipline identifier.
   * @return true if the discipline exists, false otherwise.
   */
  public boolean existDiscipline(long disciplineID);


  /**
   * This method gets a list of discipline that are active
   * 
   * @return a list from Discipline null if no exist records
   */
  public List<Discipline> findAll();


  /**
   * This method gets a discipline object by a given discipline identifier.
   * 
   * @param disciplineID is the discipline identifier.
   * @return a Discipline object.
   */
  public Discipline getDisciplineById(long disciplineID);

  /**
   * This method gets a list of disciplines belongs of the user
   * 
   * @param userId - the user id
   * @return List of Disciplines or null if the user is invalid or not have roles.
   */
  public List<Discipline> getDisciplinesByUserId(Long userId);

  /**
   * This method saves the information of the given discipline
   * 
   * @param discipline - is the discipline object with the new information to be added/updated.
   * @return a object.
   */
  public Discipline saveDiscipline(Discipline discipline);

  /**
   * This method saves the information of the given discipline
   * 
   * @param discipline - is the discipline object with the new information to be added/updated.
   * @return a object.
   */
  public Discipline saveDiscipline(Discipline discipline, String actionName, List<String> relationsName);


}
