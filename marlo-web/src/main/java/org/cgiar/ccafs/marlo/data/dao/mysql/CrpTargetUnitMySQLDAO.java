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

import org.cgiar.ccafs.marlo.data.dao.CrpTargetUnitDAO;
import org.cgiar.ccafs.marlo.data.model.CrpTargetUnit;

import java.util.List;

import com.google.inject.Inject;

public class CrpTargetUnitMySQLDAO implements CrpTargetUnitDAO {

  private StandardDAO dao;

  @Inject
  public CrpTargetUnitMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrpTargetUnit(long crpTargetUnitId) {
    CrpTargetUnit crpTargetUnit = this.find(crpTargetUnitId);
    crpTargetUnit.setActive(false);
    return this.save(crpTargetUnit) > 0;
  }

  @Override
  public boolean existCrpTargetUnit(long crpTargetUnitID) {
    CrpTargetUnit crpTargetUnit = this.find(crpTargetUnitID);
    if (crpTargetUnit == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpTargetUnit find(long id) {
    return dao.find(CrpTargetUnit.class, id);

  }

  @Override
  public List<CrpTargetUnit> findAll() {
    String query = "from " + CrpTargetUnit.class.getName() + " where is_active=1";
    List<CrpTargetUnit> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CrpTargetUnit crpTargetUnit) {
    if (crpTargetUnit.getId() == null) {
      dao.save(crpTargetUnit);
    } else {
      dao.update(crpTargetUnit);
    }


    return crpTargetUnit.getId();
  }


}