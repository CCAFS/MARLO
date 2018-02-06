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

import org.cgiar.ccafs.marlo.data.dao.ICapdevPartnersDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevPartners;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;

public class CapdevPartnersDAO extends AbstractMarloDAO<CapdevPartners, Long> implements ICapdevPartnersDAO {


  @Inject
  public CapdevPartnersDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCapdevPartners(long capdevPartnersId) {
    CapdevPartners capdevPartners = this.find(capdevPartnersId);
    capdevPartners.setActive(false);
    this.save(capdevPartners);
  }

  @Override
  public boolean existCapdevPartners(long capdevPartnersID) {
    CapdevPartners capdevPartners = this.find(capdevPartnersID);
    if (capdevPartners == null) {
      return false;
    }
    return true;

  }

  @Override
  public CapdevPartners find(long id) {
    return super.find(CapdevPartners.class, id);

  }

  @Override
  public List<CapdevPartners> findAll() {
    String query = "from " + CapdevPartners.class.getName();
    List<CapdevPartners> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CapdevPartners> getCapdevPartnerssByUserId(long userId) {
    String query = "from " + CapdevPartners.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CapdevPartners save(CapdevPartners capdevPartners) {
    if (capdevPartners.getId() == null) {
      super.saveEntity(capdevPartners);
    } else {
      super.update(capdevPartners);
    }
    return capdevPartners;
  }

  @Override
  public CapdevPartners save(CapdevPartners capdevPartners, String actionName, List<String> relationsName) {
    if (capdevPartners.getId() == null) {
      super.saveEntity(capdevPartners, actionName, relationsName);
    } else {
      super.update(capdevPartners, actionName, relationsName);
    }
    return capdevPartners;
  }


}