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

import org.cgiar.ccafs.marlo.data.dao.LiaisonUserDAO;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;

import java.util.List;

import com.google.inject.Inject;

public class LiaisonUserMySQLDAO implements LiaisonUserDAO {

  private StandardDAO dao;

  @Inject
  public LiaisonUserMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteLiaisonUser(long liaisonUserId) {
    LiaisonUser liaisonUser = this.find(liaisonUserId);

    liaisonUser.setActive(false);
    return this.save(liaisonUser) > 0;

  }

  @Override
  public boolean existLiaisonUser(long liaisonUserID) {
    LiaisonUser liaisonUser = this.find(liaisonUserID);
    if (liaisonUser == null) {
      return false;
    }
    return true;

  }

  @Override
  public LiaisonUser find(long id) {
    return dao.find(LiaisonUser.class, id);

  }

  @Override
  public List<LiaisonUser> findAll() {
    String query = "from " + LiaisonUser.class.getName();
    List<LiaisonUser> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<LiaisonUser> findByInstitutionId(Long institutionId) {
    String query =
      "from " + LiaisonUser.class.getName() + " where institution_id =" + institutionId + " and is_active=1";
    List<LiaisonUser> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public LiaisonUser findByUser(long id, long crpID) {
    String query = "from " + LiaisonUser.class.getName() + " where user_id=" + id + " and crp_id=" + crpID + "";
    List<LiaisonUser> list = dao.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public long save(LiaisonUser liaisonUser) {
    if (liaisonUser.getId() == null) {
      liaisonUser.setActive(true);
      dao.save(liaisonUser);
    } else {
      dao.update(liaisonUser);
    }


    return liaisonUser.getId();
  }


}