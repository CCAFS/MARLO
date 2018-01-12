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

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class SrfTargetUnitMySQLDAO extends AbstractMarloDAO<SrfTargetUnit, Long> implements SrfTargetUnitDAO {


  @Inject
  public SrfTargetUnitMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteSrfTargetUnit(long srfTargetUnitId) {
    SrfTargetUnit srfTargetUnit = this.find(srfTargetUnitId);
    srfTargetUnit.setActive(false);
    this.save(srfTargetUnit);
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
    return super.find(SrfTargetUnit.class, id);

  }

  @Override
  public List<SrfTargetUnit> findAll() {
    String query = "from " + SrfTargetUnit.class.getName() + " where is_active=1";
    List<SrfTargetUnit> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public SrfTargetUnit save(SrfTargetUnit srfTargetUnit) {
    if (srfTargetUnit.getId() == null) {
      super.saveEntity(srfTargetUnit);
    } else {
      srfTargetUnit = super.update(srfTargetUnit);
    }
    return srfTargetUnit;
  }


}