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

import org.cgiar.ccafs.marlo.data.dao.RepIndGeographicScopeDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndGeographicScopeMySQLDAO extends AbstractMarloDAO<RepIndGeographicScope, Long>
  implements RepIndGeographicScopeDAO {


  @Inject
  public RepIndGeographicScopeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndGeographicScope(long repIndGeographicScopeId) {
    RepIndGeographicScope repIndGeographicScope = this.find(repIndGeographicScopeId);
    this.delete(repIndGeographicScope);
  }

  @Override
  public boolean existRepIndGeographicScope(long repIndGeographicScopeID) {
    RepIndGeographicScope repIndGeographicScope = this.find(repIndGeographicScopeID);
    if (repIndGeographicScope == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndGeographicScope find(long id) {
    return super.find(RepIndGeographicScope.class, id);

  }

  @Override
  public List<RepIndGeographicScope> findAll() {
    String query = "from " + RepIndGeographicScope.class.getName();
    List<RepIndGeographicScope> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndGeographicScope save(RepIndGeographicScope repIndGeographicScope) {
    if (repIndGeographicScope.getId() == null) {
      super.saveEntity(repIndGeographicScope);
    } else {
      repIndGeographicScope = super.update(repIndGeographicScope);
    }


    return repIndGeographicScope;
  }


}