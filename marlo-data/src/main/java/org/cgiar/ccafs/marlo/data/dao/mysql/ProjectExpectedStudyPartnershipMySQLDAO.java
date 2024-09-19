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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPartnershipDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnership;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyPartnershipMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyPartnership, Long>
  implements ProjectExpectedStudyPartnershipDAO {


  @Inject
  public ProjectExpectedStudyPartnershipMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyPartnership(long projectExpectedStudyPartnershipId) {
    ProjectExpectedStudyPartnership projectExpectedStudyPartnership = this.find(projectExpectedStudyPartnershipId);
    projectExpectedStudyPartnership.setActive(false);
    this.update(projectExpectedStudyPartnership);
  }

  @Override
  public boolean existProjectExpectedStudyPartnership(long projectExpectedStudyPartnershipID) {
    ProjectExpectedStudyPartnership projectExpectedStudyPartnership = this.find(projectExpectedStudyPartnershipID);
    if (projectExpectedStudyPartnership == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyPartnership find(long id) {
    return super.find(ProjectExpectedStudyPartnership.class, id);

  }

  @Override
  public List<ProjectExpectedStudyPartnership> findAll() {
    String query = "from " + ProjectExpectedStudyPartnership.class.getName() + " where is_active=1";
    List<ProjectExpectedStudyPartnership> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectExpectedStudyPartnership> findByExpected(long expectedId) {
    String query =
      "from " + ProjectExpectedStudyPartnership.class.getName() + " where is_active=1 and expected_id=" + expectedId;
    List<ProjectExpectedStudyPartnership> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }


  @Override
  public List<ProjectExpectedStudyPartnership> findByExpectedAndPhase(long expectedId, long phaseId) {
    String query = "from " + ProjectExpectedStudyPartnership.class.getName() + " where is_active=1 and expected_id="
      + expectedId + " and id_phase = " + phaseId;;
    List<ProjectExpectedStudyPartnership> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }


  @Override
  public ProjectExpectedStudyPartnership save(ProjectExpectedStudyPartnership projectExpectedStudyPartnership) {
    if (projectExpectedStudyPartnership.getId() == null) {
      super.saveEntity(projectExpectedStudyPartnership);
    } else {
      projectExpectedStudyPartnership = super.update(projectExpectedStudyPartnership);
    }


    return projectExpectedStudyPartnership;
  }


}