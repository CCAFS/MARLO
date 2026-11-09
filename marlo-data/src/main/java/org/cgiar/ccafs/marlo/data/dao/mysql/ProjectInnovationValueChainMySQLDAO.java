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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationValueChainDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationValueChain;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationValueChainMySQLDAO extends AbstractMarloDAO<ProjectInnovationValueChain, Long>
  implements ProjectInnovationValueChainDAO {

  @Inject
  public ProjectInnovationValueChainMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationValueChain(long projectInnovationValueChainId) {
    ProjectInnovationValueChain projectInnovationValueChain = this.find(projectInnovationValueChainId);
    projectInnovationValueChain.setActive(false);
    this.update(projectInnovationValueChain);
  }

  @Override
  public ProjectInnovationValueChain find(long id) {
    return super.find(ProjectInnovationValueChain.class, id);
  }

  @Override
  public List<ProjectInnovationValueChain> getByInnovationAndPhase(long innovationID, long phaseID) {
    String query = "from " + ProjectInnovationValueChain.class.getName() + " where is_active=1 and innovation_id="
      + innovationID + " and id_phase=" + phaseID;
    return super.findAll(query);
  }

  @Override
  public ProjectInnovationValueChain save(ProjectInnovationValueChain projectInnovationValueChain) {
    if (projectInnovationValueChain.getId() == null) {
      super.saveEntity(projectInnovationValueChain);
    } else {
      projectInnovationValueChain = super.update(projectInnovationValueChain);
    }
    return projectInnovationValueChain;
  }
}
