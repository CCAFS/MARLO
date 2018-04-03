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

import org.cgiar.ccafs.marlo.data.dao.ProjectHighlightInfoDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightInfo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectHighlightInfoMySQLDAO extends AbstractMarloDAO<ProjectHighlightInfo, Long>
  implements ProjectHighlightInfoDAO {


  @Inject
  public ProjectHighlightInfoMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectHighlightInfo(long projectHighlightInfoId) {
    ProjectHighlightInfo projectHighlightInfo = this.find(projectHighlightInfoId);
    this.delete(projectHighlightInfo);
  }

  @Override
  public boolean existProjectHighlightInfo(long projectHighlightInfoID) {
    ProjectHighlightInfo projectHighlightInfo = this.find(projectHighlightInfoID);
    if (projectHighlightInfo == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectHighlightInfo find(long id) {
    return super.find(ProjectHighlightInfo.class, id);

  }

  @Override
  public List<ProjectHighlightInfo> findAll() {
    String query = "from " + ProjectHighlightInfo.class.getName() + "";
    List<ProjectHighlightInfo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectHighlightInfo save(ProjectHighlightInfo projectHighlightInfo) {
    if (projectHighlightInfo.getId() == null) {
      super.saveEntity(projectHighlightInfo);
    } else {
      projectHighlightInfo = super.update(projectHighlightInfo);
    }


    return projectHighlightInfo;
  }


}