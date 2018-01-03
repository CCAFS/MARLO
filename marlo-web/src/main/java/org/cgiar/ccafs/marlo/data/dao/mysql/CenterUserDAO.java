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

import org.cgiar.ccafs.marlo.data.dao.ICenterUserDAO;
import org.cgiar.ccafs.marlo.data.model.CenterUser;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CenterUserDAO extends AbstractMarloDAO<CenterUser, Long> implements ICenterUserDAO {


  @Inject
  public CenterUserDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpUser(long crpUserId) {
    CenterUser crpUser = this.find(crpUserId);
    crpUser.setActive(false);
    this.save(crpUser);
  }

  @Override
  public boolean existCrpUser(long crpUserID) {
    CenterUser crpUser = this.find(crpUserID);
    if (crpUser == null) {
      return false;
    }
    return true;

  }

  @Override
  public boolean existCrpUser(long userId, long crpId) {
    String query = "from " + CenterUser.class.getName() + " where user_id=" + userId + " and center_id=" + crpId;
    List<CenterUser> crpUser = super.findAll(query);
    if (crpUser != null && crpUser.size() > 0) {
      return true;
    }
    return false;
  }

  @Override
  public CenterUser find(long id) {
    return super.find(CenterUser.class, id);

  }

  @Override
  public List<CenterUser> findAll() {
    String query = "from " + CenterUser.class.getName() + " where is_active=1";
    List<CenterUser> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CenterUser save(CenterUser crpUser) {
    if (crpUser.getId() == null) {
      super.saveEntity(crpUser);
    } else {
      crpUser = super.update(crpUser);
    }
    return crpUser;
  }


}