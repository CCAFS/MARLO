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

import org.cgiar.ccafs.marlo.data.dao.RepIndPolicyInvestimentTypeDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyInvestimentType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndPolicyInvestimentTypeMySQLDAO extends AbstractMarloDAO<RepIndPolicyInvestimentType, Long>
  implements RepIndPolicyInvestimentTypeDAO {


  @Inject
  public RepIndPolicyInvestimentTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndPolicyInvestimentType(long repIndPolicyInvestimentTypeId) {
    RepIndPolicyInvestimentType repIndPolicyInvestimentType = this.find(repIndPolicyInvestimentTypeId);
    this.delete(repIndPolicyInvestimentType);
  }

  @Override
  public boolean existRepIndPolicyInvestimentType(long repIndPolicyInvestimentTypeID) {
    RepIndPolicyInvestimentType repIndPolicyInvestimentType = this.find(repIndPolicyInvestimentTypeID);
    if (repIndPolicyInvestimentType == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndPolicyInvestimentType find(long id) {
    return super.find(RepIndPolicyInvestimentType.class, id);

  }

  @Override
  public List<RepIndPolicyInvestimentType> findAll() {
    String query = "from " + RepIndPolicyInvestimentType.class.getName();
    List<RepIndPolicyInvestimentType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndPolicyInvestimentType save(RepIndPolicyInvestimentType repIndPolicyInvestimentType) {
    if (repIndPolicyInvestimentType.getId() == null) {
      super.saveEntity(repIndPolicyInvestimentType);
    } else {
      repIndPolicyInvestimentType = super.update(repIndPolicyInvestimentType);
    }


    return repIndPolicyInvestimentType;
  }


}