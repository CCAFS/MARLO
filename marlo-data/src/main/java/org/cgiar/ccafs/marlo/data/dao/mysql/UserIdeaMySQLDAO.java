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

import org.cgiar.ccafs.marlo.data.dao.UserIdeaDAO;
import org.cgiar.ccafs.marlo.data.model.UserIdea;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class UserIdeaMySQLDAO extends AbstractMarloDAO<UserIdea, Long> implements UserIdeaDAO {


  @Inject
  public UserIdeaMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteUserIdea(long userIdeaId) {
    UserIdea userIdea = this.find(userIdeaId);
    userIdea.setActive(false);
    this.update(userIdea);
  }

  @Override
  public boolean existUserIdea(long userIdeaID) {
    UserIdea userIdea = this.find(userIdeaID);
    if (userIdea == null) {
      return false;
    }
    return true;

  }

  @Override
  public UserIdea find(long id) {
    return super.find(UserIdea.class, id);

  }

  @Override
  public List<UserIdea> findAll() {
    String query = "from " + UserIdea.class.getName() + " where is_active=1";
    List<UserIdea> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public UserIdea save(UserIdea userIdea) {
    if (userIdea.getId() == null) {
      super.saveEntity(userIdea);
    } else {
      userIdea = super.update(userIdea);
    }


    return userIdea;
  }


}