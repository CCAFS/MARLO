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

import org.cgiar.ccafs.marlo.data.dao.ProjectNextuserDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectNextuser;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class ProjectNextuserMySQLDAO extends AbstractMarloDAO<ProjectNextuser, Long> implements ProjectNextuserDAO {


  @Inject
  public ProjectNextuserMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteProjectNextuser(long projectNextuserId) {
    ProjectNextuser projectNextuser = this.find(projectNextuserId);
    projectNextuser.setActive(false);
    return this.save(projectNextuser) > 0;
  }

  @Override
  public boolean existProjectNextuser(long projectNextuserID) {
    ProjectNextuser projectNextuser = this.find(projectNextuserID);
    if (projectNextuser == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectNextuser find(long id) {
    return super.find(ProjectNextuser.class, id);

  }

  @Override
  public List<ProjectNextuser> findAll() {
    String query = "from " + ProjectNextuser.class.getName() + " where is_active=1";
    List<ProjectNextuser> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectNextuser projectNextuser) {
    if (projectNextuser.getId() == null) {
      super.saveEntity(projectNextuser);
    } else {
      super.update(projectNextuser);
    }


    return projectNextuser.getId();
  }


}