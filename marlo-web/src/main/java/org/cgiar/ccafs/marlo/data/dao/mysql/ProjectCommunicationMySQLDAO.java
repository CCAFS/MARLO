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

import com.google.inject.Inject;

public class ProjectCommunicationMySQLDAO implements ProjectCommunicationDAO {

  private StandardDAO dao;

  @Inject
  public ProjectCommunicationMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectCommunication(long projectCommunicationId) {
    ProjectCommunication projectCommunication = this.find(projectCommunicationId);
    projectCommunication.setActive(false);
    return this.save(projectCommunication) > 0;
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
    return dao.find(ProjectCommunication.class, id);

  }

  @Override
  public List<ProjectCommunication> findAll() {
    String query = "from " + ProjectCommunication.class.getName() + " where is_active=1";
    List<ProjectCommunication> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectCommunication projectCommunication) {
    if (projectCommunication.getId() == null) {
      dao.save(projectCommunication);
    } else {
      dao.update(projectCommunication);
    }


    return projectCommunication.getId();
  }


}