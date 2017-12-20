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

import org.cgiar.ccafs.marlo.data.dao.CrpUserDAO;
import org.cgiar.ccafs.marlo.data.model.CrpUser;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CrpUserMySQLDAO extends AbstractMarloDAO<CrpUser, Long> implements CrpUserDAO {


  @Inject
  public CrpUserMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpUser(long crpUserId) {
    CrpUser crpUser = this.find(crpUserId);
    crpUser.setActive(false);
    this.save(crpUser);
  }

  @Override
  public boolean existActiveCrpUser(long userId, long crpId) {
    String query =
      "from " + CrpUser.class.getName() + " where user_id=" + userId + " and crp_id=" + crpId + " and is_active=1";
    List<CrpUser> crpUser = super.findAll(query);
    if (crpUser != null && crpUser.size() > 0) {
      return true;
    }
    return false;
  }

  @Override
  public boolean existCrpUser(long crpUserID) {
    CrpUser crpUser = this.find(crpUserID);
    if (crpUser == null) {
      return false;
    }
    return true;

  }

  @Override
  public boolean existCrpUser(long userId, long crpId) {
    String query = "from " + CrpUser.class.getName() + " where user_id=" + userId + " and global_unit_id=" + crpId;
    List<CrpUser> crpUser = super.findAll(query);
    if (crpUser != null && crpUser.size() > 0) {
      return true;
    }
    return false;
  }

  @Override
  public CrpUser find(long id) {
    return super.find(CrpUser.class, id);

  }

  @Override
  public List<CrpUser> findAll() {
    String query = "from " + CrpUser.class.getName() + " where is_active=1";
    List<CrpUser> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CrpUser save(CrpUser crpUser) {
    if (crpUser.getId() == null) {
      super.saveEntity(crpUser);
    } else {
      crpUser = super.update(crpUser);
    }
    return crpUser;
  }


}