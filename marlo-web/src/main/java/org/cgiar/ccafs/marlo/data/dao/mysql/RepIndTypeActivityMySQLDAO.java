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

import org.cgiar.ccafs.marlo.data.dao.RepIndTypeActivityDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndTypeActivity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndTypeActivityMySQLDAO extends AbstractMarloDAO<RepIndTypeActivity, Long>
  implements RepIndTypeActivityDAO {


  @Inject
  public RepIndTypeActivityMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndTypeActivity(long repIndTypeActivityId) {
    RepIndTypeActivity repIndTypeActivity = this.find(repIndTypeActivityId);
    this.delete(repIndTypeActivity);
  }

  @Override
  public boolean existRepIndTypeActivity(long repIndTypeActivityID) {
    RepIndTypeActivity repIndTypeActivity = this.find(repIndTypeActivityID);
    if (repIndTypeActivity == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndTypeActivity find(long id) {
    return super.find(RepIndTypeActivity.class, id);

  }

  @Override
  public List<RepIndTypeActivity> findAll() {
    String query = "from " + RepIndTypeActivity.class.getName();
    List<RepIndTypeActivity> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndTypeActivity save(RepIndTypeActivity repIndTypeActivity) {
    if (repIndTypeActivity.getId() == null) {
      super.saveEntity(repIndTypeActivity);
    } else {
      repIndTypeActivity = super.update(repIndTypeActivity);
    }


    return repIndTypeActivity;
  }


}