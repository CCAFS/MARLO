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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationReferenceDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReference;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationReferenceMySQLDAO extends AbstractMarloDAO<ProjectInnovationReference, Long>
  implements ProjectInnovationReferenceDAO {


  @Inject
  public ProjectInnovationReferenceMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationReference(long projectInnovationReferenceId) {
    ProjectInnovationReference projectInnovationReference = this.find(projectInnovationReferenceId);
    /*
     * projectInnovationReference.setActive(false);
     * this.update(projectInnovationReference);
     */
    this.delete(projectInnovationReference);
  }

  @Override
  public boolean existProjectInnovationReference(long projectInnovationReferenceID) {
    ProjectInnovationReference projectInnovationReference = this.find(projectInnovationReferenceID);
    if (projectInnovationReference == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationReference find(long id) {
    return super.find(ProjectInnovationReference.class, id);

  }

  @Override
  public List<ProjectInnovationReference> findAll() {
    String query = "from " + ProjectInnovationReference.class.getName() + " where is_active=1";
    List<ProjectInnovationReference> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectInnovationReference> getProjectInnovationReferenceByPhaseAndInnovation(long phaseID,
    long innovationID) {
    String query = "from " + ProjectInnovationReference.class.getName()
      + " where is_active=1 and project_innovation_id=" + innovationID + " and id_phase=" + phaseID;
    List<ProjectInnovationReference> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();
  }

  @Override
  public ProjectInnovationReference save(ProjectInnovationReference projectInnovationReference) {
    if (projectInnovationReference.getId() == null) {
      super.saveEntity(projectInnovationReference);
    } else {
      projectInnovationReference = super.update(projectInnovationReference);
    }


    return projectInnovationReference;
  }


}