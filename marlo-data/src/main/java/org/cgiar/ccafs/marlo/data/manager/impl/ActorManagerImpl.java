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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ActorDAO;
import org.cgiar.ccafs.marlo.data.manager.ActorManager;
import org.cgiar.ccafs.marlo.data.model.Actor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ActorManagerImpl implements ActorManager {


  private ActorDAO actorDAO;
  // Managers


  @Inject
  public ActorManagerImpl(ActorDAO actorDAO) {
    this.actorDAO = actorDAO;


  }

  @Override
  public void deleteActor(long actorId) {

    actorDAO.deleteActor(actorId);
  }

  @Override
  public boolean existActor(long actorID) {

    return actorDAO.existActor(actorID);
  }

  @Override
  public List<Actor> findAll() {

    return actorDAO.findAll();

  }

  @Override
  public Actor getActorById(long actorID) {

    return actorDAO.find(actorID);
  }

  @Override
  public Actor saveActor(Actor actor) {

    return actorDAO.save(actor);
  }


}
