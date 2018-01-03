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

import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerContributionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class ProjectPartnerContributionMySQLDAO extends AbstractMarloDAO<ProjectPartnerContribution, Long> implements ProjectPartnerContributionDAO {


  @Inject
  public ProjectPartnerContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPartnerContribution(long projectPartnerContributionId) {
    ProjectPartnerContribution projectPartnerContribution = this.find(projectPartnerContributionId);
    projectPartnerContribution.setActive(false);
    this.save(projectPartnerContribution);
  }

  @Override
  public boolean existProjectPartnerContribution(long projectPartnerContributionID) {
    ProjectPartnerContribution projectPartnerContribution = this.find(projectPartnerContributionID);
    if (projectPartnerContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPartnerContribution find(long id) {
    return super.find(ProjectPartnerContribution.class, id);

  }

  @Override
  public List<ProjectPartnerContribution> findAll() {
    String query = "from " + ProjectPartnerContribution.class.getName() + " where is_active=1";
    List<ProjectPartnerContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectPartnerContribution save(ProjectPartnerContribution projectPartnerContribution) {
    if (projectPartnerContribution.getId() == null) {
      super.saveEntity(projectPartnerContribution);
    } else {
      projectPartnerContribution = super.update(projectPartnerContribution);
    }


    return projectPartnerContribution;
  }


}