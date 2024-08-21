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

import org.cgiar.ccafs.marlo.data.dao.RelatedAllianceLeverSdgContributionDAO;
import org.cgiar.ccafs.marlo.data.model.RelatedAllianceLeverSdgContribution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RelatedAllianceLeverSdgContributionMySQLDAO extends AbstractMarloDAO<RelatedAllianceLeverSdgContribution, Long> implements RelatedAllianceLeverSdgContributionDAO {


  @Inject
  public RelatedAllianceLeverSdgContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRelatedAllianceLeverSdgContribution(long relatedAllianceLeverSdgContributionId) {
    RelatedAllianceLeverSdgContribution relatedAllianceLeverSdgContribution = this.find(relatedAllianceLeverSdgContributionId);
    relatedAllianceLeverSdgContribution.setActive(false);
    this.update(relatedAllianceLeverSdgContribution);
  }

  @Override
  public boolean existRelatedAllianceLeverSdgContribution(long relatedAllianceLeverSdgContributionID) {
    RelatedAllianceLeverSdgContribution relatedAllianceLeverSdgContribution = this.find(relatedAllianceLeverSdgContributionID);
    if (relatedAllianceLeverSdgContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public RelatedAllianceLeverSdgContribution find(long id) {
    return super.find(RelatedAllianceLeverSdgContribution.class, id);

  }

  @Override
  public List<RelatedAllianceLeverSdgContribution> findAll() {
    String query = "from " + RelatedAllianceLeverSdgContribution.class.getName() + " where is_active=1";
    List<RelatedAllianceLeverSdgContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RelatedAllianceLeverSdgContribution save(RelatedAllianceLeverSdgContribution relatedAllianceLeverSdgContribution) {
    if (relatedAllianceLeverSdgContribution.getId() == null) {
      super.saveEntity(relatedAllianceLeverSdgContribution);
    } else {
      relatedAllianceLeverSdgContribution = super.update(relatedAllianceLeverSdgContribution);
    }


    return relatedAllianceLeverSdgContribution;
  }


}