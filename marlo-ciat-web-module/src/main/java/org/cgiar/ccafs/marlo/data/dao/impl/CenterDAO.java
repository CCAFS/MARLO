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

package org.cgiar.ccafs.marlo.data.dao.impl;

import org.cgiar.ccafs.marlo.data.dao.ICenterDAO;
import org.cgiar.ccafs.marlo.data.model.Center;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Christian Garcia - CIAT/CCAFS
 */
public class CenterDAO implements ICenterDAO {

  private StandardDAO dao;

  @Inject
  public CenterDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrp(long crpId) {
    Center center = this.find(crpId);
    center.setActive(false);
    return this.save(center) > 0;
  }

  @Override
  public boolean existCrp(long crpID) {
    Center crp = this.find(crpID);
    if (crp == null) {
      return false;
    }
    return true;

  }

  @Override
  public Center find(long id) {
    return dao.find(Center.class, id);

  }

  @Override
  public List<Center> findAll() {
    String query = "from " + Center.class.getName() + " where is_active=1";
    List<Center> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public Center findCrpByAcronym(String acronym) {
    String query = "from " + Center.class.getName() + " where acronym='" + acronym + "'";
    List<Center> list = dao.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public long save(Center crp) {
    if (crp.getId() == null) {
      dao.save(crp);
    } else {
      dao.update(crp);
    }
    return crp.getId();
  }
}
