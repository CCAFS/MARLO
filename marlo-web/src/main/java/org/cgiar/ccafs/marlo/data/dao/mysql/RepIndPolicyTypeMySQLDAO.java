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

import org.cgiar.ccafs.marlo.data.dao.RepIndPolicyTypeDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndPolicyTypeMySQLDAO extends AbstractMarloDAO<RepIndPolicyType, Long> implements RepIndPolicyTypeDAO {


  @Inject
  public RepIndPolicyTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndPolicyType(long repIndPolicyTypeId) {
    this.delete(repIndPolicyTypeId);
  }

  @Override
  public boolean existRepIndPolicyType(long repIndPolicyTypeID) {
    RepIndPolicyType repIndPolicyType = this.find(repIndPolicyTypeID);
    if (repIndPolicyType == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndPolicyType find(long id) {
    return super.find(RepIndPolicyType.class, id);

  }

  @Override
  public List<RepIndPolicyType> findAll() {
    String query = "from " + RepIndPolicyType.class.getName() + " where is_active=1";
    List<RepIndPolicyType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndPolicyType save(RepIndPolicyType repIndPolicyType) {
    if (repIndPolicyType.getId() == null) {
      super.saveEntity(repIndPolicyType);
    } else {
      repIndPolicyType = super.update(repIndPolicyType);
    }


    return repIndPolicyType;
  }


}