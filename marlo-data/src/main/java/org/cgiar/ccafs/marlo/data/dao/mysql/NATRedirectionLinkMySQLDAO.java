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

import org.cgiar.ccafs.marlo.data.dao.NATRedirectionLinkDAO;
import org.cgiar.ccafs.marlo.data.model.NATRedirectionLink;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class NATRedirectionLinkMySQLDAO extends AbstractMarloDAO<NATRedirectionLink, Long>
  implements NATRedirectionLinkDAO {


  @Inject
  public NATRedirectionLinkMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteNATRedirectionLink(long NATRedirectionLinkId) {
    NATRedirectionLink NATRedirectionLink = this.find(NATRedirectionLinkId);
    this.delete(NATRedirectionLink);
  }

  @Override
  public boolean existNATRedirectionLink(long NATRedirectionLinkID) {
    NATRedirectionLink NATRedirectionLink = this.find(NATRedirectionLinkID);
    if (NATRedirectionLink == null) {
      return false;
    }
    return true;

  }

  @Override
  public NATRedirectionLink find(long id) {
    return super.find(NATRedirectionLink.class, id);

  }

  @Override
  public List<NATRedirectionLink> findAll() {
    String query = "from " + NATRedirectionLink.class.getName();
    List<NATRedirectionLink> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public NATRedirectionLink findByIndicatorAndId(String indicatorName, long indicatorId) {
    String query = "select nrl from NATRedirectionLink nrl "
      + "where nrl.indicatorName = :indicatorName and nrl.indicatorId = :indicatorId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);

    createQuery.setParameter("indicatorName", indicatorName);
    createQuery.setParameter("indicatorId", indicatorId);

    List<NATRedirectionLink> matches = super.findAll(createQuery);

    return matches == null || matches.isEmpty() ? null : matches.get(0);
  }

  @Override
  public NATRedirectionLink save(NATRedirectionLink NATRedirectionLink) {
    if (NATRedirectionLink.getId() == null) {
      super.saveEntity(NATRedirectionLink);
    } else {
      NATRedirectionLink = super.update(NATRedirectionLink);
    }


    return NATRedirectionLink;
  }


}