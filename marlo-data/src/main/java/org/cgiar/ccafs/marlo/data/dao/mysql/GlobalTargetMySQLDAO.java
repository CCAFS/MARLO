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

import org.cgiar.ccafs.marlo.data.dao.GlobalTargetDAO;
import org.cgiar.ccafs.marlo.data.model.GlobalTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class GlobalTargetMySQLDAO extends AbstractMarloDAO<GlobalTarget, Long> implements GlobalTargetDAO {


  @Inject
  public GlobalTargetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteGlobalTarget(long globalTargetId) {
    GlobalTarget globalTarget = this.find(globalTargetId);
    globalTarget.setActive(false);
    this.update(globalTarget);
  }

  @Override
  public boolean existGlobalTarget(long globalTargetID) {
    GlobalTarget globalTarget = this.find(globalTargetID);
    if (globalTarget == null) {
      return false;
    }
    return true;

  }

  @Override
  public GlobalTarget find(long id) {
    return super.find(GlobalTarget.class, id);

  }

  @Override
  public List<GlobalTarget> findAll() {
    String query = "from " + GlobalTarget.class.getName();
    List<GlobalTarget> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<GlobalTarget> findAllByImpactArea(long impactAreaId) {
    String query = "from " + GlobalTarget.class.getName() + " where st_impact_area_id=" + impactAreaId;
    List<GlobalTarget> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public GlobalTarget save(GlobalTarget globalTarget) {
    if (globalTarget.getId() == null) {
      super.saveEntity(globalTarget);
    } else {
      globalTarget = super.update(globalTarget);
    }


    return globalTarget;
  }


}