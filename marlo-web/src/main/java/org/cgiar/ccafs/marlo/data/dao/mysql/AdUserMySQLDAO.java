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

import org.cgiar.ccafs.marlo.data.dao.AdUserDAO;
import org.cgiar.ccafs.marlo.data.model.AdUser;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class AdUserMySQLDAO extends AbstractMarloDAO<AdUser, Long> implements AdUserDAO {


  @Inject
  public AdUserMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteAdUser(long adUserId) {
    AdUser adUser = this.find(adUserId);
    adUser.setActive(false);
    this.save(adUser);
  }

  @Override
  public boolean existAdUser(long adUserID) {
    AdUser adUser = this.find(adUserID);
    if (adUser == null) {
      return false;
    }
    return true;

  }

  @Override
  public AdUser find(long id) {
    return super.find(AdUser.class, id);

  }

  @Override
  public List<AdUser> findAll() {
    String query = "from " + AdUser.class.getName() + " where is_active=1";
    List<AdUser> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public AdUser findByLogin(String login) {
    String query = "from " + AdUser.class.getName() + " where login='" + login + "'";
    AdUser obj = super.findSingleResult(AdUser.class, query);
    if (obj != null) {
      return obj;
    }
    return null;
  }

  @Override
  public AdUser save(AdUser adUser) {
    if (adUser.getId() == null) {
      super.saveEntity(adUser);
    } else {
      adUser = super.update(adUser);
    }


    return adUser;
  }


}