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

import org.cgiar.ccafs.marlo.data.dao.DeliverableGeographicScopeDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableGeographicScope;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableGeographicScopeMySQLDAO extends AbstractMarloDAO<DeliverableGeographicScope, Long>
  implements DeliverableGeographicScopeDAO {


  @Inject
  public DeliverableGeographicScopeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableGeographicScope(long deliverableGeographicScopeId) {
    DeliverableGeographicScope deliverableGeographicScope = this.find(deliverableGeographicScopeId);
    this.delete(deliverableGeographicScope);
  }

  @Override
  public boolean existDeliverableGeographicScope(long deliverableGeographicScopeID) {
    DeliverableGeographicScope deliverableGeographicScope = this.find(deliverableGeographicScopeID);
    if (deliverableGeographicScope == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableGeographicScope find(long id) {
    return super.find(DeliverableGeographicScope.class, id);

  }

  @Override
  public List<DeliverableGeographicScope> findAll() {
    String query = "from " + DeliverableGeographicScope.class.getName();
    List<DeliverableGeographicScope> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableGeographicScope save(DeliverableGeographicScope deliverableGeographicScope) {
    if (deliverableGeographicScope.getId() == null) {
      super.saveEntity(deliverableGeographicScope);
    } else {
      deliverableGeographicScope = super.update(deliverableGeographicScope);
    }


    return deliverableGeographicScope;
  }


}