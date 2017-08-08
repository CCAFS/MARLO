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

import org.cgiar.ccafs.marlo.data.dao.ICenterTargetUnitDAO;
import org.cgiar.ccafs.marlo.data.model.CenterTargetUnit;

import java.util.List;

import com.google.inject.Inject;

public class CenterTargetUnitDAO implements ICenterTargetUnitDAO {

  private StandardDAO dao;

  @Inject
  public CenterTargetUnitDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteTargetUnit(long targetUnitId) {
    CenterTargetUnit targetUnit = this.find(targetUnitId);
    targetUnit.setActive(false);
    return this.save(targetUnit) > 0;
  }

  @Override
  public boolean existTargetUnit(long targetUnitID) {
    CenterTargetUnit targetUnit = this.find(targetUnitID);
    if (targetUnit == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterTargetUnit find(long id) {
    return dao.find(CenterTargetUnit.class, id);

  }

  @Override
  public List<CenterTargetUnit> findAll() {
    String query = "from " + CenterTargetUnit.class.getName();
    List<CenterTargetUnit> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterTargetUnit> getTargetUnitsByUserId(long userId) {
    String query = "from " + CenterTargetUnit.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterTargetUnit targetUnit) {
    if (targetUnit.getId() == null) {
      dao.save(targetUnit);
    } else {
      dao.update(targetUnit);
    }
    return targetUnit.getId();
  }


}