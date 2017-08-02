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

import org.cgiar.ccafs.marlo.data.dao.ICenterOutputsNextUserDAO;
import org.cgiar.ccafs.marlo.data.model.CenterOutputsNextUser;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterOutputsNextUserDAO extends AbstractMarloDAO implements ICenterOutputsNextUserDAO {


  @Inject
  public CenterOutputsNextUserDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteResearchOutputsNextUser(long researchOutputsNextUserId) {
    CenterOutputsNextUser researchOutputsNextUser = this.find(researchOutputsNextUserId);
    researchOutputsNextUser.setActive(false);
    return this.save(researchOutputsNextUser) > 0;
  }

  @Override
  public boolean existResearchOutputsNextUser(long researchOutputsNextUserID) {
    CenterOutputsNextUser researchOutputsNextUser = this.find(researchOutputsNextUserID);
    if (researchOutputsNextUser == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterOutputsNextUser find(long id) {
    return super.find(CenterOutputsNextUser.class, id);

  }

  @Override
  public List<CenterOutputsNextUser> findAll() {
    String query = "from " + CenterOutputsNextUser.class.getName();
    List<CenterOutputsNextUser> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterOutputsNextUser> getResearchOutputsNextUsersByUserId(long userId) {
    String query = "from " + CenterOutputsNextUser.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public long save(CenterOutputsNextUser researchOutputsNextUser) {
    if (researchOutputsNextUser.getId() == null) {
      super.save(researchOutputsNextUser);
    } else {
      super.update(researchOutputsNextUser);
    }
    return researchOutputsNextUser.getId();
  }


}