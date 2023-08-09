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

import org.cgiar.ccafs.marlo.data.dao.ActivityTitleDAO;
import org.cgiar.ccafs.marlo.data.model.ActivityTitle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ActivityTitleMySQLDAO extends AbstractMarloDAO<ActivityTitle, Long> implements ActivityTitleDAO {


  @Inject
  public ActivityTitleMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteActivityTitle(long activityTitleId) {
    ActivityTitle activityTitle = this.find(activityTitleId);
    this.delete(activityTitle);
  }

  @Override
  public boolean existActivityTitle(long activityTitleID) {
    ActivityTitle activityTitle = this.find(activityTitleID);
    if (activityTitle == null) {
      return false;
    }
    return true;

  }

  @Override
  public ActivityTitle find(long id) {
    return super.find(ActivityTitle.class, id);

  }

  @Override
  public List<ActivityTitle> findAll() {
    String query = "from " + ActivityTitle.class.getName();
    List<ActivityTitle> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ActivityTitle> findByCurrentYear(int year) {
    String query = "select pat from ActivityTitle pat where pat.endYear >= :year and pat.startYear < :year";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("year", year);
    List<ActivityTitle> list = super.findAll(createQuery);
    if (!list.isEmpty()) {
      return list;
    }
    return null;
  }

  @Override
  public ActivityTitle save(ActivityTitle activityTitle) {
    if (activityTitle.getId() == null) {
      super.saveEntity(activityTitle);
    } else {
      activityTitle = super.update(activityTitle);
    }


    return activityTitle;
  }


}