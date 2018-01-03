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

import org.cgiar.ccafs.marlo.data.dao.ProjectCommunicationDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectCommunication;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class ProjectCommunicationMySQLDAO extends AbstractMarloDAO<ProjectCommunication, Long> implements ProjectCommunicationDAO {


  @Inject
  public ProjectCommunicationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectCommunication(long projectCommunicationId) {
    ProjectCommunication projectCommunication = this.find(projectCommunicationId);
    projectCommunication.setActive(false);
    this.save(projectCommunication);
  }

  @Override
  public boolean existProjectCommunication(long projectCommunicationID) {
    ProjectCommunication projectCommunication = this.find(projectCommunicationID);
    if (projectCommunication == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectCommunication find(long id) {
    return super.find(ProjectCommunication.class, id);

  }

  @Override
  public List<ProjectCommunication> findAll() {
    String query = "from " + ProjectCommunication.class.getName() + " where is_active=1";
    List<ProjectCommunication> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectCommunication save(ProjectCommunication projectCommunication) {
    if (projectCommunication.getId() == null) {
      super.saveEntity(projectCommunication);
    } else {
      projectCommunication = super.update(projectCommunication);
    }


    return projectCommunication;
  }


}