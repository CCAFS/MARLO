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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ActorDAO;
import org.cgiar.ccafs.marlo.data.model.Actor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ActorMySQLDAO extends AbstractMarloDAO<Actor, Long> implements ActorDAO {


  @Inject
  public ActorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteActor(long actorId) {
    Actor actor = this.find(actorId);
    actor.setActive(false);
    this.update(actor);
  }

  @Override
  public boolean existActor(long actorID) {
    Actor actor = this.find(actorID);
    if (actor == null) {
      return false;
    }
    return true;

  }

  @Override
  public Actor find(long id) {
    return super.find(Actor.class, id);

  }

  @Override
  public List<Actor> findAll() {
    String query = "from " + Actor.class.getName() + " where is_active=1";
    List<Actor> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public Actor save(Actor actor) {
    if (actor.getId() == null) {
      super.saveEntity(actor);
    } else {
      actor = super.update(actor);
    }


    return actor;
  }


}