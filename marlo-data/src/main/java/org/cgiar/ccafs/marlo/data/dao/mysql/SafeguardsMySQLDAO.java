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

import org.cgiar.ccafs.marlo.data.dao.SafeguardsDAO;
import org.cgiar.ccafs.marlo.data.model.Safeguards;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class SafeguardsMySQLDAO extends AbstractMarloDAO<Safeguards, Long> implements SafeguardsDAO {


  @Inject
  public SafeguardsMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteSafeguards(long safeguardsId) {
    Safeguards safeguards = this.find(safeguardsId);
    safeguards.setActive(false);
    this.update(safeguards);
  }

  @Override
  public boolean existSafeguards(long safeguardsID) {
    Safeguards safeguards = this.find(safeguardsID);
    if (safeguards == null) {
      return false;
    }
    return true;

  }

  @Override
  public Safeguards find(long id) {
    return super.find(Safeguards.class, id);

  }

  @Override
  public List<Safeguards> findAll() {
    String query = "from " + Safeguards.class.getName() + " where is_active=1";
    List<Safeguards> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public Safeguards save(Safeguards safeguards) {
    if (safeguards.getId() == null) {
      super.saveEntity(safeguards);
    } else {
      safeguards = super.update(safeguards);
    }


    return safeguards;
  }


}