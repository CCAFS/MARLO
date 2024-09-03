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

import org.cgiar.ccafs.marlo.data.dao.RelatedLeversSDGContributionDAO;
import org.cgiar.ccafs.marlo.data.model.RelatedLeversSDGContribution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RelatedLeversSDGContributionMySQLDAO extends AbstractMarloDAO<RelatedLeversSDGContribution, Long> implements RelatedLeversSDGContributionDAO {


  @Inject
  public RelatedLeversSDGContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRelatedLeversSDGContribution(long relatedLeversSDGContributionId) {
    RelatedLeversSDGContribution relatedLeversSDGContribution = this.find(relatedLeversSDGContributionId);
    relatedLeversSDGContribution.setActive(false);
    this.update(relatedLeversSDGContribution);
  }

  @Override
  public boolean existRelatedLeversSDGContribution(long relatedLeversSDGContributionID) {
    RelatedLeversSDGContribution relatedLeversSDGContribution = this.find(relatedLeversSDGContributionID);
    if (relatedLeversSDGContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public RelatedLeversSDGContribution find(long id) {
    return super.find(RelatedLeversSDGContribution.class, id);

  }

  @Override
  public List<RelatedLeversSDGContribution> findAll() {
    String query = "from " + RelatedLeversSDGContribution.class.getName() + " where is_active=1";
    List<RelatedLeversSDGContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RelatedLeversSDGContribution save(RelatedLeversSDGContribution relatedLeversSDGContribution) {
    if (relatedLeversSDGContribution.getId() == null) {
      super.saveEntity(relatedLeversSDGContribution);
    } else {
      relatedLeversSDGContribution = super.update(relatedLeversSDGContribution);
    }


    return relatedLeversSDGContribution;
  }


}