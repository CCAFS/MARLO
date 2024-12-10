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

import org.cgiar.ccafs.marlo.data.model.Actor;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ActorManager {


  /**
   * This method removes a specific actor value from the database.
   * 
   * @param actorId is the actor identifier.
   * @return true if the actor was successfully deleted, false otherwise.
   */
  public void deleteActor(long actorId);


  /**
   * This method validate if the actor identify with the given id exists in the system.
   * 
   * @param actorID is a actor identifier.
   * @return true if the actor exists, false otherwise.
   */
  public boolean existActor(long actorID);


  /**
   * This method gets a list of actor that are active
   * 
   * @return a list from Actor null if no exist records
   */
  public List<Actor> findAll();


  /**
   * This method gets a actor object by a given actor identifier.
   * 
   * @param actorID is the actor identifier.
   * @return a Actor object.
   */
  public Actor getActorById(long actorID);

  /**
   * This method saves the information of the given actor
   * 
   * @param actor - is the actor object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the actor was
   *         updated
   *         or -1 is some error occurred.
   */
  public Actor saveActor(Actor actor);


}
