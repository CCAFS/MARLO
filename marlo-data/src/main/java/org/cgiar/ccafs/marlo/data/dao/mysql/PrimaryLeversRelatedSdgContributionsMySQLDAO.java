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

import org.cgiar.ccafs.marlo.data.dao.PrimaryLeversRelatedSdgContributionsDAO;
import org.cgiar.ccafs.marlo.data.model.PrimaryLeversRelatedSdgContribution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PrimaryLeversRelatedSdgContributionsMySQLDAO extends AbstractMarloDAO<PrimaryLeversRelatedSdgContribution, Long> implements PrimaryLeversRelatedSdgContributionsDAO {


  @Inject
  public PrimaryLeversRelatedSdgContributionsMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePrimaryLeversRelatedSdgContributions(long primaryLeversRelatedSdgContributionsId) {
    PrimaryLeversRelatedSdgContribution primaryLeversRelatedSdgContribution = this.find(primaryLeversRelatedSdgContributionsId);
    primaryLeversRelatedSdgContribution.setActive(false);
    this.update(primaryLeversRelatedSdgContribution);
  }

  @Override
  public boolean existPrimaryLeversRelatedSdgContributions(long primaryLeversRelatedSdgContributionsID) {
    PrimaryLeversRelatedSdgContribution primaryLeversRelatedSdgContribution = this.find(primaryLeversRelatedSdgContributionsID);
    if (primaryLeversRelatedSdgContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public PrimaryLeversRelatedSdgContribution find(long id) {
    return super.find(PrimaryLeversRelatedSdgContribution.class, id);

  }

  @Override
  public List<PrimaryLeversRelatedSdgContribution> findAll() {
    String query = "from " + PrimaryLeversRelatedSdgContribution.class.getName() + " where is_active=1";
    List<PrimaryLeversRelatedSdgContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PrimaryLeversRelatedSdgContribution save(PrimaryLeversRelatedSdgContribution primaryLeversRelatedSdgContribution) {
    if (primaryLeversRelatedSdgContribution.getId() == null) {
      super.saveEntity(primaryLeversRelatedSdgContribution);
    } else {
      primaryLeversRelatedSdgContribution = super.update(primaryLeversRelatedSdgContribution);
    }


    return primaryLeversRelatedSdgContribution;
  }


}