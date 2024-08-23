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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyRelatedLeverSdgContributionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRelatedLeverSdgContribution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyRelatedLeverSdgContributionMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyRelatedLeverSdgContribution, Long> implements ProjectExpectedStudyRelatedLeverSdgContributionDAO {


  @Inject
  public ProjectExpectedStudyRelatedLeverSdgContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyRelatedLeverSdgContribution(long projectExpectedStudyRelatedLeverSdgContributionId) {
    ProjectExpectedStudyRelatedLeverSdgContribution projectExpectedStudyRelatedLeverSdgContribution = this.find(projectExpectedStudyRelatedLeverSdgContributionId);
    projectExpectedStudyRelatedLeverSdgContribution.setActive(false);
    this.update(projectExpectedStudyRelatedLeverSdgContribution);
  }

  @Override
  public boolean existProjectExpectedStudyRelatedLeverSdgContribution(long projectExpectedStudyRelatedLeverSdgContributionID) {
    ProjectExpectedStudyRelatedLeverSdgContribution projectExpectedStudyRelatedLeverSdgContribution = this.find(projectExpectedStudyRelatedLeverSdgContributionID);
    if (projectExpectedStudyRelatedLeverSdgContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyRelatedLeverSdgContribution find(long id) {
    return super.find(ProjectExpectedStudyRelatedLeverSdgContribution.class, id);

  }

  @Override
  public List<ProjectExpectedStudyRelatedLeverSdgContribution> findAll() {
    String query = "from " + ProjectExpectedStudyRelatedLeverSdgContribution.class.getName() + " where is_active=1";
    List<ProjectExpectedStudyRelatedLeverSdgContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyRelatedLeverSdgContribution save(ProjectExpectedStudyRelatedLeverSdgContribution projectExpectedStudyRelatedLeverSdgContribution) {
    if (projectExpectedStudyRelatedLeverSdgContribution.getId() == null) {
      super.saveEntity(projectExpectedStudyRelatedLeverSdgContribution);
    } else {
      projectExpectedStudyRelatedLeverSdgContribution = super.update(projectExpectedStudyRelatedLeverSdgContribution);
    }


    return projectExpectedStudyRelatedLeverSdgContribution;
  }


}