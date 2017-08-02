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

import org.cgiar.ccafs.marlo.data.dao.ProjectOutcomePandrDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcomePandr;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class ProjectOutcomePandrMySQLDAO extends AbstractMarloDAO implements ProjectOutcomePandrDAO {


  @Inject
  public ProjectOutcomePandrMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteProjectOutcomePandr(long projectOutcomePandrId) {
    ProjectOutcomePandr projectOutcomePandr = this.find(projectOutcomePandrId);
    projectOutcomePandr.setActive(false);
    return this.save(projectOutcomePandr) > 0;
  }

  @Override
  public boolean existProjectOutcomePandr(long projectOutcomePandrID) {
    ProjectOutcomePandr projectOutcomePandr = this.find(projectOutcomePandrID);
    if (projectOutcomePandr == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectOutcomePandr find(long id) {
    return super.find(ProjectOutcomePandr.class, id);

  }

  @Override
  public List<ProjectOutcomePandr> findAll() {
    String query = "from " + ProjectOutcomePandr.class.getName() + " where is_active=1";
    List<ProjectOutcomePandr> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectOutcomePandr projectOutcomePandr) {
    if (projectOutcomePandr.getId() == null) {
      super.save(projectOutcomePandr);
    } else {
      super.update(projectOutcomePandr);
    }


    return projectOutcomePandr.getId();
  }


}