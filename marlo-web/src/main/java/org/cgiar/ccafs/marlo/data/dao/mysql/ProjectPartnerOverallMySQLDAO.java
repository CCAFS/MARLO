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

import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerOverallDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerOverall;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class ProjectPartnerOverallMySQLDAO extends AbstractMarloDAO<ProjectPartnerOverall, Long> implements ProjectPartnerOverallDAO {


  @Inject
  public ProjectPartnerOverallMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteProjectPartnerOverall(long projectPartnerOverallId) {
    ProjectPartnerOverall projectPartnerOverall = this.find(projectPartnerOverallId);

    return super.delete(projectPartnerOverall);
  }

  @Override
  public boolean existProjectPartnerOverall(long projectPartnerOverallID) {
    ProjectPartnerOverall projectPartnerOverall = this.find(projectPartnerOverallID);
    if (projectPartnerOverall == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPartnerOverall find(long id) {
    return super.find(ProjectPartnerOverall.class, id);

  }

  @Override
  public List<ProjectPartnerOverall> findAll() {
    String query = "from " + ProjectPartnerOverall.class.getName() + " where is_active=1";
    List<ProjectPartnerOverall> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectPartnerOverall projectPartnerOverall) {
    if (projectPartnerOverall.getId() == null) {
      super.saveEntity(projectPartnerOverall);
    } else {
      super.update(projectPartnerOverall);
    }


    return projectPartnerOverall.getId();
  }


}