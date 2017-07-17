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

import com.google.inject.Inject;

public class CapdevPartnersDAO implements ICapdevPartnersDAO {

  private StandardDAO dao;

  @Inject
  public CapdevPartnersDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCapdevPartners(long capdevPartnersId) {
    CapdevPartners capdevPartners = this.find(capdevPartnersId);
    capdevPartners.setActive(false);
    return this.save(capdevPartners) > 0;
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
    return dao.find(CapdevPartners.class, id);

  }

  @Override
  public List<CapdevPartners> findAll() {
    String query = "from " + CapdevPartners.class.getName();
    List<CapdevPartners> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CapdevPartners> getCapdevPartnerssByUserId(long userId) {
    String query = "from " + CapdevPartners.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CapdevPartners capdevPartners) {
    if (capdevPartners.getId() == null) {
      dao.save(capdevPartners);
    } else {
      dao.update(capdevPartners);
    }
    return capdevPartners.getId();
  }

  @Override
  public long save(CapdevPartners capdevPartners, String actionName, List<String> relationsName) {
    if (capdevPartners.getId() == null) {
      dao.save(capdevPartners, actionName, relationsName);
    } else {
      dao.update(capdevPartners, actionName, relationsName);
    }
    return capdevPartners.getId();
  }


}