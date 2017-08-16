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

import org.cgiar.ccafs.marlo.data.dao.ICenterProjectCrosscutingThemeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProjectCrosscutingTheme;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterProjectCrosscutingThemeDAO extends AbstractMarloDAO<CenterProjectCrosscutingTheme, Long>
  implements ICenterProjectCrosscutingThemeDAO {


  @Inject
  public CenterProjectCrosscutingThemeDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteProjectCrosscutingTheme(long projectCrosscutingThemeId) {
    CenterProjectCrosscutingTheme projectCrosscutingTheme = this.find(projectCrosscutingThemeId);
    projectCrosscutingTheme.setActive(false);
    return this.save(projectCrosscutingTheme) > 0;
  }

  @Override
  public boolean existProjectCrosscutingTheme(long projectCrosscutingThemeID) {
    CenterProjectCrosscutingTheme projectCrosscutingTheme = this.find(projectCrosscutingThemeID);
    if (projectCrosscutingTheme == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterProjectCrosscutingTheme find(long id) {
    return super.find(CenterProjectCrosscutingTheme.class, id);

  }

  @Override
  public List<CenterProjectCrosscutingTheme> findAll() {
    String query = "from " + CenterProjectCrosscutingTheme.class.getName();
    List<CenterProjectCrosscutingTheme> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterProjectCrosscutingTheme> getProjectCrosscutingThemesByUserId(long userId) {
    String query = "from " + CenterProjectCrosscutingTheme.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public long save(CenterProjectCrosscutingTheme projectCrosscutingTheme) {
    if (projectCrosscutingTheme.getId() == null) {
      super.saveEntity(projectCrosscutingTheme);
    } else {
      super.update(projectCrosscutingTheme);
    }
    return projectCrosscutingTheme.getId();
  }


}