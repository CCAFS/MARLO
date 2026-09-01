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

import org.cgiar.ccafs.marlo.data.dao.HomepageBannerDAO;
import org.cgiar.ccafs.marlo.data.model.HomepageBanner;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

@Named
public class HomepageBannerMySQLDAO extends AbstractMarloDAO<HomepageBanner, Long> implements HomepageBannerDAO {

  @Inject
  public HomepageBannerMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public HomepageBanner find(long homepageBannerID) {
    return super.find(HomepageBanner.class, homepageBannerID);
  }

  @Override
  public HomepageBanner findByGlobalUnit(long globalUnitID) {
    // Parameterised rather than concatenated: the id is a long here, but the pattern is the one that stays safe when
    // somebody later widens the filter.
    String hql = "from " + HomepageBanner.class.getName() + " where globalUnit.id = :globalUnitID";
    Query<HomepageBanner> query = this.getSessionFactory().getCurrentSession().createQuery(hql);
    query.setParameter("globalUnitID", globalUnitID);
    return super.findSingleResult(HomepageBanner.class, query);
  }

  @Override
  public HomepageBanner save(HomepageBanner homepageBanner) {
    if (homepageBanner.getId() == null) {
      return super.saveEntity(homepageBanner);
    }
    return super.update(homepageBanner);
  }
}
