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

import org.cgiar.ccafs.marlo.data.dao.SrfTargetUnitDAO;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;

import java.util.List;

import com.google.inject.Inject;

public class SrfTargetUnitMySQLDAO implements SrfTargetUnitDAO {

  private StandardDAO dao;

  @Inject
  public SrfTargetUnitMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteSrfTargetUnit(long srfTargetUnitId) {
    SrfTargetUnit srfTargetUnit = this.find(srfTargetUnitId);
    srfTargetUnit.setActive(false);
    return this.save(srfTargetUnit) > 0;
  }

  @Override
  public boolean existSrfTargetUnit(long srfTargetUnitID) {
    SrfTargetUnit srfTargetUnit = this.find(srfTargetUnitID);
    if (srfTargetUnit == null) {
      return false;
    }
    return true;

  }

  @Override
  public SrfTargetUnit find(long id) {
    return dao.find(SrfTargetUnit.class, id);

  }

  @Override
  public List<SrfTargetUnit> findAll() {
    String query = "from " + SrfTargetUnit.class.getName() + " where is_active=1";
    List<SrfTargetUnit> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(SrfTargetUnit srfTargetUnit) {
    if (srfTargetUnit.getId() == null) {
      dao.save(srfTargetUnit);
    } else {
      dao.update(srfTargetUnit);
    }
    return srfTargetUnit.getId();
  }


}