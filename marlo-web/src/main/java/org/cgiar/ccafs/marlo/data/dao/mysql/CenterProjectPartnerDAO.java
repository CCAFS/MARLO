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

import org.cgiar.ccafs.marlo.data.dao.ICenterProjectPartnerDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProjectPartner;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterProjectPartnerDAO extends AbstractMarloDAO<CenterProjectPartner, Long>
  implements ICenterProjectPartnerDAO {


  @Inject
  public CenterProjectPartnerDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPartner(long projectPartnerId) {
    CenterProjectPartner projectPartner = this.find(projectPartnerId);
    projectPartner.setActive(false);
    this.save(projectPartner);
  }

  @Override
  public boolean existProjectPartner(long projectPartnerID) {
    CenterProjectPartner projectPartner = this.find(projectPartnerID);
    if (projectPartner == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterProjectPartner find(long id) {
    return super.find(CenterProjectPartner.class, id);

  }

  @Override
  public List<CenterProjectPartner> findAll() {
    String query = "from " + CenterProjectPartner.class.getName();
    List<CenterProjectPartner> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterProjectPartner> getProjectPartnersByUserId(long userId) {
    String query = "from " + CenterProjectPartner.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterProjectPartner save(CenterProjectPartner projectPartner) {
    if (projectPartner.getId() == null) {
      super.saveEntity(projectPartner);
    } else {
      projectPartner = super.update(projectPartner);
    }
    return projectPartner;
  }


}