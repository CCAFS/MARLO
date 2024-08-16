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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyTagDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyTag;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyTagMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyTag, Long>
  implements ProjectExpectedStudyTagDAO {


  @Inject
  public ProjectExpectedStudyTagMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyTag(long projectExpectedStudyTagId) {
    ProjectExpectedStudyTag projectExpectedStudyTag = this.find(projectExpectedStudyTagId);
    this.delete(projectExpectedStudyTag);
  }

  @Override
  public boolean existProjectExpectedStudyTag(long projectExpectedStudyTagID) {
    ProjectExpectedStudyTag projectExpectedStudyTag = this.find(projectExpectedStudyTagID);
    if (projectExpectedStudyTag == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyTag find(long id) {
    return super.find(ProjectExpectedStudyTag.class, id);

  }

  @Override
  public List<ProjectExpectedStudyTag> findAll() {
    String query = "from " + ProjectExpectedStudyTag.class.getName();
    List<ProjectExpectedStudyTag> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;
  }

  @Override
  public ProjectExpectedStudyTag save(ProjectExpectedStudyTag projectExpectedStudyTag) {
    if (projectExpectedStudyTag.getId() == null) {
      super.saveEntity(projectExpectedStudyTag);
    } else {
      projectExpectedStudyTag = super.update(projectExpectedStudyTag);
    }
    return projectExpectedStudyTag;
  }
}