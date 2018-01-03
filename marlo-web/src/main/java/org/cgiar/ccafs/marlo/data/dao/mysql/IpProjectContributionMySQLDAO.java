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

import org.cgiar.ccafs.marlo.data.dao.IpProjectContributionDAO;
import org.cgiar.ccafs.marlo.data.model.IpProjectContribution;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class IpProjectContributionMySQLDAO extends AbstractMarloDAO<IpProjectContribution, Long> implements IpProjectContributionDAO {


  @Inject
  public IpProjectContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteIpProjectContribution(long ipProjectContributionId) {
    IpProjectContribution ipProjectContribution = this.find(ipProjectContributionId);
    ipProjectContribution.setActive(false);
    this.save(ipProjectContribution);
  }

  @Override
  public boolean existIpProjectContribution(long ipProjectContributionID) {
    IpProjectContribution ipProjectContribution = this.find(ipProjectContributionID);
    if (ipProjectContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public IpProjectContribution find(long id) {
    return super.find(IpProjectContribution.class, id);

  }

  @Override
  public List<IpProjectContribution> findAll() {
    String query = "from " + IpProjectContribution.class.getName() + " where is_active=1";
    List<IpProjectContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public IpProjectContribution save(IpProjectContribution ipProjectContribution) {
    if (ipProjectContribution.getId() == null) {
      super.saveEntity(ipProjectContribution);
    } else {
      ipProjectContribution = super.update(ipProjectContribution);
    }


    return ipProjectContribution;
  }


}